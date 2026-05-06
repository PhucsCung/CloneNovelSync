package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.InventoryBalance;
import com.mycompany.myapp.domain.InventoryTransaction;
import com.mycompany.myapp.domain.PurchaseOrder;
import com.mycompany.myapp.domain.PurchaseOrderLine;
import com.mycompany.myapp.domain.enumeration.PurchaseStatus;
import com.mycompany.myapp.domain.enumeration.ReferenceType;
import com.mycompany.myapp.domain.enumeration.TransactionType;
import com.mycompany.myapp.repository.InventoryBalanceRepository;
import com.mycompany.myapp.repository.InventoryTransactionRepository;
import com.mycompany.myapp.repository.PurchaseOrderLineRepository;
import com.mycompany.myapp.repository.PurchaseOrderRepository;
import com.mycompany.myapp.service.PurchaseOrderService;
import com.mycompany.myapp.service.dto.PurchaseOrderDTO;
import com.mycompany.myapp.service.mapper.PurchaseOrderMapper;
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
 * Service Implementation for managing {@link PurchaseOrder}.
 */
@Service
@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final Logger log = LoggerFactory.getLogger(PurchaseOrderServiceImpl.class);

    private final PurchaseOrderRepository purchaseOrderRepository;

    private final PurchaseOrderMapper purchaseOrderMapper;

    private final PurchaseOrderLineRepository purchaseOrderLineRepository;

    private final InventoryBalanceRepository inventoryBalanceRepository;

    private final InventoryTransactionRepository inventoryTransactionRepository;

    public PurchaseOrderServiceImpl(
        PurchaseOrderRepository purchaseOrderRepository,
        PurchaseOrderMapper purchaseOrderMapper,
        PurchaseOrderLineRepository purchaseOrderLineRepository,
        InventoryBalanceRepository inventoryBalanceRepository,
        InventoryTransactionRepository inventoryTransactionRepository
    ) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderMapper = purchaseOrderMapper;
        this.purchaseOrderLineRepository = purchaseOrderLineRepository;
        this.inventoryBalanceRepository = inventoryBalanceRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
    }

    @Override
    public PurchaseOrderDTO save(PurchaseOrderDTO purchaseOrderDTO) {
        log.debug("Request to save PurchaseOrder : {}", purchaseOrderDTO);
        PurchaseOrder purchaseOrder = purchaseOrderMapper.toEntity(purchaseOrderDTO);
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        return purchaseOrderMapper.toDto(purchaseOrder);
    }

    @Override
    public PurchaseOrderDTO update(PurchaseOrderDTO purchaseOrderDTO) {
        log.debug("Request to update PurchaseOrder : {}", purchaseOrderDTO);
        PurchaseOrder purchaseOrder = purchaseOrderMapper.toEntity(purchaseOrderDTO);
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        return purchaseOrderMapper.toDto(purchaseOrder);
    }

    @Override
    public Optional<PurchaseOrderDTO> partialUpdate(PurchaseOrderDTO purchaseOrderDTO) {
        log.debug("Request to partially update PurchaseOrder : {}", purchaseOrderDTO);

        return purchaseOrderRepository
            .findById(purchaseOrderDTO.getId())
            .map(existingPurchaseOrder -> {
                purchaseOrderMapper.partialUpdate(existingPurchaseOrder, purchaseOrderDTO);

                return existingPurchaseOrder;
            })
            .map(purchaseOrderRepository::save)
            .map(purchaseOrderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseOrderDTO> findAll(Pageable pageable) {
        log.debug("Request to get all PurchaseOrders");
        return purchaseOrderRepository.findAll(pageable).map(purchaseOrderMapper::toDto);
    }

    public Page<PurchaseOrderDTO> findAllWithEagerRelationships(Pageable pageable) {
        return purchaseOrderRepository.findAllWithEagerRelationships(pageable).map(purchaseOrderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PurchaseOrderDTO> findOne(Long id) {
        log.debug("Request to get PurchaseOrder : {}", id);
        return purchaseOrderRepository.findOneWithEagerRelationships(id).map(purchaseOrderMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete PurchaseOrder : {}", id);
        purchaseOrderRepository.deleteById(id);
    }

    @Override
    @Transactional
    public PurchaseOrderDTO completeOrder(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy đơn hàng", "purchaseOrder", "notfound"));

        if (purchaseOrder.getStatus() == PurchaseStatus.COMPLETED) {
            throw new BadRequestAlertException("Đơn hàng này đã được hoàn thành trước đó", "purchaseOrder", "alreadycompleted");
        }

        List<PurchaseOrderLine> lines = purchaseOrderLineRepository.findByPurchaseOrderId(id);
        if (lines.isEmpty()) {
            throw new BadRequestAlertException("Đơn hàng trống, không thể hoàn thành", "purchaseOrder", "emptyorder");
        }

        for (PurchaseOrderLine line : lines) {
            Long bookId = line.getBook().getId();
            Integer quantityToAdd = line.getQuantity();

            InventoryBalance balance = inventoryBalanceRepository
                .findByBookId(bookId)
                .orElse(new InventoryBalance().book(line.getBook()).quantityOnHand(0));

            balance.setQuantityOnHand(balance.getQuantityOnHand() + quantityToAdd);
            inventoryBalanceRepository.save(balance);

            InventoryTransaction transaction = new InventoryTransaction();
            transaction.setTransactionType(TransactionType.IN);
            transaction.setReferenceType(ReferenceType.PO);
            transaction.setReferenceId(purchaseOrder.getId());
            transaction.setBook(line.getBook());
            transaction.setQuantity(quantityToAdd);

            inventoryTransactionRepository.save(transaction);
        }

        purchaseOrder.setStatus(PurchaseStatus.COMPLETED);
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);

        return purchaseOrderMapper.toDto(purchaseOrder);
    }
}
