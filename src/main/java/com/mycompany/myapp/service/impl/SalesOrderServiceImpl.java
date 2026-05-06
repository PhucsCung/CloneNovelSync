package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.InventoryBalance;
import com.mycompany.myapp.domain.InventoryTransaction;
import com.mycompany.myapp.domain.SalesOrder;
import com.mycompany.myapp.domain.SalesOrderLine;
import com.mycompany.myapp.domain.enumeration.ReferenceType;
import com.mycompany.myapp.domain.enumeration.SalesStatus;
import com.mycompany.myapp.domain.enumeration.TransactionType;
import com.mycompany.myapp.repository.InventoryBalanceRepository;
import com.mycompany.myapp.repository.InventoryTransactionRepository;
import com.mycompany.myapp.repository.SalesOrderLineRepository;
import com.mycompany.myapp.repository.SalesOrderRepository;
import com.mycompany.myapp.service.SalesOrderService;
import com.mycompany.myapp.service.dto.SalesOrderDTO;
import com.mycompany.myapp.service.mapper.SalesOrderMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link SalesOrder}.
 */
@Service
@Transactional
public class SalesOrderServiceImpl implements SalesOrderService {

    private final Logger log = LoggerFactory.getLogger(SalesOrderServiceImpl.class);

    private final SalesOrderRepository salesOrderRepository;

    private final SalesOrderMapper salesOrderMapper;

    private final SalesOrderLineRepository salesOrderLineRepository;

    private final InventoryBalanceRepository inventoryBalanceRepository;

    private final InventoryTransactionRepository inventoryTransactionRepository;

    public SalesOrderServiceImpl(
        SalesOrderRepository salesOrderRepository,
        SalesOrderMapper salesOrderMapper,
        SalesOrderLineRepository salesOrderLineRepository,
        InventoryBalanceRepository inventoryBalanceRepository,
        InventoryTransactionRepository inventoryTransactionRepository
    ) {
        this.salesOrderRepository = salesOrderRepository;
        this.salesOrderMapper = salesOrderMapper;
        this.salesOrderLineRepository = salesOrderLineRepository;
        this.inventoryBalanceRepository = inventoryBalanceRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
    }

    @Override
    public SalesOrderDTO save(SalesOrderDTO salesOrderDTO) {
        log.debug("Request to save SalesOrder : {}", salesOrderDTO);
        SalesOrder salesOrder = salesOrderMapper.toEntity(salesOrderDTO);
        salesOrder = salesOrderRepository.save(salesOrder);
        return salesOrderMapper.toDto(salesOrder);
    }

    @Override
    public SalesOrderDTO update(SalesOrderDTO salesOrderDTO) {
        log.debug("Request to update SalesOrder : {}", salesOrderDTO);
        SalesOrder salesOrder = salesOrderMapper.toEntity(salesOrderDTO);
        salesOrder = salesOrderRepository.save(salesOrder);
        return salesOrderMapper.toDto(salesOrder);
    }

    @Override
    public Optional<SalesOrderDTO> partialUpdate(SalesOrderDTO salesOrderDTO) {
        log.debug("Request to partially update SalesOrder : {}", salesOrderDTO);

        return salesOrderRepository
            .findById(salesOrderDTO.getId())
            .map(existingSalesOrder -> {
                salesOrderMapper.partialUpdate(existingSalesOrder, salesOrderDTO);

                return existingSalesOrder;
            })
            .map(salesOrderRepository::save)
            .map(salesOrderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SalesOrderDTO> findAll(Pageable pageable) {
        log.debug("Request to get all SalesOrders");
        return salesOrderRepository.findAll(pageable).map(salesOrderMapper::toDto);
    }

    public Page<SalesOrderDTO> findAllWithEagerRelationships(Pageable pageable) {
        return salesOrderRepository.findAllWithEagerRelationships(pageable).map(salesOrderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SalesOrderDTO> findOne(Long id) {
        log.debug("Request to get SalesOrder : {}", id);
        return salesOrderRepository.findOneWithEagerRelationships(id).map(salesOrderMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete SalesOrder : {}", id);
        salesOrderRepository.deleteById(id);
    }

    @Override
    @Transactional
    public SalesOrderDTO completeOrder(Long id) {
        SalesOrder salesOrder = salesOrderRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy đơn hàng", "salesOrder", "notfound"));

        if (salesOrder.getStatus() == SalesStatus.COMPLETED) {
            throw new BadRequestAlertException("Đơn hàng này đã được xuất kho trước đó", "salesOrder", "alreadycompleted");
        }

        List<SalesOrderLine> lines = salesOrderLineRepository.findBySalesOrderId(id);
        if (lines.isEmpty()) {
            throw new BadRequestAlertException("Đơn xuất trống, không thể hoàn thành", "salesOrder", "emptyorder");
        }

        for (SalesOrderLine line : lines) {
            Long bookId = line.getBook().getId();
            InventoryBalance balance = inventoryBalanceRepository
                .findByBookId(bookId)
                .orElseThrow(() -> new BadRequestAlertException("Sách không có trong kho", "salesOrder", "outofstock"));

            if (balance.getQuantityOnHand() < line.getQuantity()) {
                throw new BadRequestAlertException(
                    "Không đủ hàng trong kho. Số lượng còn: " + balance.getQuantityOnHand(),
                    "salesOrder",
                    "insufficientstock"
                );
            }
        }

        for (SalesOrderLine line : lines) {
            Long bookId = line.getBook().getId();

            InventoryBalance balance = inventoryBalanceRepository.findByBookId(bookId).get();
            balance.setQuantityOnHand(balance.getQuantityOnHand() - line.getQuantity());
            inventoryBalanceRepository.save(balance);

            InventoryTransaction transaction = new InventoryTransaction();
            transaction.setTransactionType(TransactionType.OUT);
            transaction.setReferenceType(ReferenceType.SO);
            transaction.setReferenceId(salesOrder.getId());
            transaction.setBook(line.getBook());
            transaction.setQuantity(line.getQuantity());
            inventoryTransactionRepository.save(transaction);
        }

        salesOrder.setStatus(SalesStatus.COMPLETED);
        salesOrder = salesOrderRepository.save(salesOrder);

        return salesOrderMapper.toDto(salesOrder);
    }
}
