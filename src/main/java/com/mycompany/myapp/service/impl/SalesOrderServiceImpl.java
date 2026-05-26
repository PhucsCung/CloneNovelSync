package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.domain.enumeration.ReferenceType;
import com.mycompany.myapp.domain.enumeration.SalesStatus;
import com.mycompany.myapp.domain.enumeration.TransactionType;
import com.mycompany.myapp.repository.*;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.NotificationService;
import com.mycompany.myapp.service.SalesOrderService;
import com.mycompany.myapp.service.dto.SalesOrderCreationRequest;
import com.mycompany.myapp.service.dto.SalesOrderDTO;
import com.mycompany.myapp.service.mapper.SalesOrderMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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

    private final NotificationService notificationService;

    private final UserRepository userRepository;

    private final BookRepository bookRepository;

    public SalesOrderServiceImpl(
        SalesOrderRepository salesOrderRepository,
        SalesOrderMapper salesOrderMapper,
        SalesOrderLineRepository salesOrderLineRepository,
        InventoryBalanceRepository inventoryBalanceRepository,
        InventoryTransactionRepository inventoryTransactionRepository,
        NotificationService notificationService,
        UserRepository userRepository,
        BookRepository bookRepository
    ) {
        this.salesOrderRepository = salesOrderRepository;
        this.salesOrderMapper = salesOrderMapper;
        this.salesOrderLineRepository = salesOrderLineRepository;
        this.inventoryBalanceRepository = inventoryBalanceRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public SalesOrderDTO save(SalesOrderDTO salesOrderDTO) {
        log.debug("Request to save SalesOrder : {}", salesOrderDTO);
        SalesOrder salesOrder = salesOrderMapper.toEntity(salesOrderDTO);
        salesOrder = salesOrderRepository.save(salesOrder);
        final String savedOrderCode = salesOrder.getCode();

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);

        if (currentUserLogin != null) {
            boolean isAdmin = "admin".equalsIgnoreCase(currentUserLogin);

            if (!isAdmin) {
                userRepository.findOneByLogin(currentUserLogin).ifPresent(user -> {
                    notificationService.createNotification(
                        "Đơn hàng đang chờ duyệt",
                        "Bạn vừa tạo đơn hàng nháp " + savedOrderCode + ". Hãy kiểm tra lại và bấm duyệt để xuất kho nhé!",
                        user.getId()
                    );
                });

                userRepository.findOneByLogin("admin").ifPresent(admin -> {
                    notificationService.createNotification(
                        "Có đơn hàng mới cần duyệt",
                        "Nhân viên " + currentUserLogin + " vừa tạo đơn hàng " + savedOrderCode + ". Sếp vào duyệt nhé!",
                        admin.getId()
                    );
                });

            } else {
                userRepository.findOneByLogin("admin").ifPresent(admin -> {
                    notificationService.createNotification(
                        "Đơn hàng đang chờ duyệt",
                        "Bạn vừa tạo đơn hàng nháp " + savedOrderCode + ". Hãy kiểm tra lại và bấm duyệt để xuất kho nhé!",
                        admin.getId()
                    );
                });
            }
        }

        return salesOrderMapper.toDto(salesOrder);
    }

    @Override
    public SalesOrderDTO update(SalesOrderDTO salesOrderDTO) {
        log.debug("Request to update SalesOrder : {}", salesOrderDTO);

        SalesOrder existingOrder = salesOrderRepository.findById(salesOrderDTO.getId())
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", "salesOrder", "idnotfound"));
        salesOrderDTO.setStatus(existingOrder.getStatus());

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
                // Xóa status khỏi DTO trước khi map để giữ nguyên status cũ
                salesOrderDTO.setStatus(null);
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
        SalesOrder salesOrder = salesOrderRepository.findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy đơn hàng", "salesOrder", "notfound"));
        if (salesOrder.getStatus() == SalesStatus.COMPLETED) {
            throw new BadRequestAlertException(
                "Không thể xóa đơn hàng đã hoàn thành vì sách đã được xuất kho!",
                "salesOrder",
                "cannotDeleteCompleted"
            );
        }
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse("Ai đó");
        User creator = salesOrder.getUser();
        String orderCode = salesOrder.getCode();
        if (creator != null && creator.getId() != null) {
            notificationService.createNotification(
                "Đơn hàng đã bị hủy",
                "Đơn bán hàng nháp " + orderCode + " đã bị xóa khỏi hệ thống bởi " + currentUserLogin + ".",
                creator.getId()
            );
        }
        // Gửi thông báo báo cáo cho Sếp (Admin)
        userRepository.findOneByLogin("admin").ifPresent(admin -> {
            // Tránh spam admin nếu chính admin là người xóa
            if (!admin.getLogin().equals(currentUserLogin)) {
                notificationService.createNotification(
                    "Đơn bán hàng bị xóa",
                    "Không duyệt đơn bán hàng nháp " + orderCode + ".",
                    admin.getId()
                );
            }
        });
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

        //Dùng distinct để loại bỏ trùng lặp
        List<Long> bookIds = lines.stream()
            .map(line -> line.getBook().getId())
            .distinct()
            .collect(Collectors.toList());

        List<InventoryBalance> existingBalances = inventoryBalanceRepository.findByBookIdIn(bookIds);

        Map<Long, InventoryBalance> balanceMap = existingBalances.stream()
            .collect(Collectors.toMap(b -> b.getBook().getId(), b -> b));

        List<InventoryTransaction> transactionsToSave = new ArrayList<>();
        List<InventoryBalance> balancesToSave = new ArrayList<>();

        for (SalesOrderLine line : lines) {
            Long bookId = line.getBook().getId();
            InventoryBalance balance = balanceMap.get(bookId);

            if (balance == null) {
                throw new BadRequestAlertException("Sách không có trong kho (BookID: " + bookId + ")", "salesOrder", "outofstock");
            }

            if (balance.getQuantityOnHand() < line.getQuantity()) {
                throw new BadRequestAlertException(
                    "Không đủ hàng trong kho. Số lượng còn: " + balance.getQuantityOnHand(),
                    "salesOrder",
                    "insufficientstock"
                );
            }

            balance.setQuantityOnHand(balance.getQuantityOnHand() - line.getQuantity());
            balancesToSave.add(balance);

            InventoryTransaction transaction = new InventoryTransaction();
            transaction.setTransactionType(TransactionType.OUT);
            transaction.setReferenceType(ReferenceType.SO);
            transaction.setReferenceId(salesOrder.getId());
            transaction.setBook(line.getBook());
            transaction.setQuantity(line.getQuantity());
            transactionsToSave.add(transaction);
        }

        try {
            inventoryBalanceRepository.saveAll(balancesToSave);
            inventoryTransactionRepository.saveAll(transactionsToSave);
            inventoryBalanceRepository.flush();
            inventoryTransactionRepository.flush();
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
            log.error("Xung đột dữ liệu tồn kho do có người thao tác cùng lúc trên đơn hàng SO: {}", salesOrder.getCode(), e);
            throw new BadRequestAlertException(
                "Sách trong đơn hàng vừa được cập nhật kho bởi một nhân viên khác. Vui lòng F5 tải lại trang và thử lại!",
                "salesOrder",
                "inventoryConflict"
            );
        }

        salesOrder.setStatus(SalesStatus.COMPLETED);
        salesOrder = salesOrderRepository.save(salesOrder);

        User creator = salesOrder.getUser();

        if (creator != null && creator.getId() != null) {
            notificationService.createNotification(
                "Đơn hàng đã được duyệt!",
                "Chúc mừng! Đơn hàng #" + salesOrder.getCode() + " của bạn đã được duyệt và xuất kho thành công.",
                creator.getId() // Lấy thẳng ID của người tạo đơn
            );
        }

        return salesOrderMapper.toDto(salesOrder);
    }

    @Override
    @Transactional
    public SalesOrderDTO createWithLines(SalesOrderCreationRequest request) {
        log.debug("Creating SalesOrder and Lines together for code: {}", request.getCode());

        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setCode(request.getCode());
        salesOrder.setStatus(SalesStatus.DRAFT);
        salesOrder.setCreatedAt(java.time.Instant.now());

        User userProxy = userRepository.getReferenceById(request.getUserId());
        salesOrder.setUser(userProxy);

        final SalesOrder savedOrder = salesOrderRepository.save(salesOrder);

        Set<Long> bookIds = request.getItems().stream()
            .map(SalesOrderCreationRequest.LineItemRequest::getBookId)
            .collect(Collectors.toSet());

        List<Book> books = bookRepository.findAllById(bookIds);

        Map<Long, Book> bookMap = books.stream()
            .collect(Collectors.toMap(Book::getId, b -> b));

        List<SalesOrderLine> linesToSave = new ArrayList<>();
        BigDecimal calculatedTotal = BigDecimal.ZERO;

        for (SalesOrderCreationRequest.LineItemRequest item : request.getItems()) {
            Book book = bookMap.get(item.getBookId());
            if (book == null) {
                throw new BadRequestAlertException("Sách ID " + item.getBookId() + " không tồn tại", "salesOrder", "booknotfound");
            }

            SalesOrderLine line = new SalesOrderLine();
            line.setSalesOrder(savedOrder);
            line.setBook(book);
            line.setQuantity(item.getQuantity());

            BigDecimal unitPrice = book.getRetailPrice();
            if (unitPrice == null) {
                unitPrice = BigDecimal.ZERO;
            }
            line.setUnitPrice(unitPrice);

            BigDecimal lineTotal = unitPrice.multiply(new BigDecimal(item.getQuantity()));
            calculatedTotal = calculatedTotal.add(lineTotal);

            linesToSave.add(line);
        }
        salesOrderLineRepository.saveAll(linesToSave);

        BigDecimal feTotalAmount = request.getTotalAmount() != null ? request.getTotalAmount() : BigDecimal.ZERO;
        if (feTotalAmount.compareTo(calculatedTotal) != 0) {
            throw new BadRequestAlertException(
                "Tổng tiền không khớp! Hệ thống tính: " + calculatedTotal + ", Front-end gửi: " + feTotalAmount,
                "salesOrder",
                "totalAmountMismatch"
            );
        }
        savedOrder.setTotalAmount(calculatedTotal);

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (currentUserLogin != null) {
            userRepository.findOneByLogin(currentUserLogin).ifPresent(u -> {
                notificationService.createNotification(
                    "Đơn hàng đang chờ duyệt",
                    "Đơn hàng nháp " + savedOrder.getCode() + " đã được gửi. Vui lòng chờ sếp duyệt!",
                    u.getId()
                );
            });
            userRepository.findOneByLogin("admin").ifPresent(admin -> {
                notificationService.createNotification(
                    "Có đơn hàng mới cần duyệt",
                    "Nhân viên Sales " + currentUserLogin + " vừa tạo đơn " + savedOrder.getCode() + ".",
                    admin.getId()
                );
            });
        }
        return salesOrderMapper.toDto(savedOrder);
    }
}
