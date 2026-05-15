package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.domain.enumeration.PurchaseStatus;
import com.mycompany.myapp.domain.enumeration.ReferenceType;
import com.mycompany.myapp.domain.enumeration.TransactionType;
import com.mycompany.myapp.repository.*;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.NotificationService;
import com.mycompany.myapp.service.PurchaseOrderService;
import com.mycompany.myapp.service.dto.PurchaseOrderCreationRequest;
import com.mycompany.myapp.service.dto.PurchaseOrderDTO;
import com.mycompany.myapp.service.mapper.PurchaseOrderMapper;
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

    private final NotificationService notificationService;

    private final UserRepository userRepository;

    private final BookRepository bookRepository;

    public PurchaseOrderServiceImpl(
        PurchaseOrderRepository purchaseOrderRepository,
        PurchaseOrderMapper purchaseOrderMapper,
        PurchaseOrderLineRepository purchaseOrderLineRepository,
        InventoryBalanceRepository inventoryBalanceRepository,
        InventoryTransactionRepository inventoryTransactionRepository,
        NotificationService notificationService,
        UserRepository userRepository,
        BookRepository bookRepository
    ) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderMapper = purchaseOrderMapper;
        this.purchaseOrderLineRepository = purchaseOrderLineRepository;
        this.inventoryBalanceRepository = inventoryBalanceRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public PurchaseOrderDTO save(PurchaseOrderDTO purchaseOrderDTO) {
        log.debug("Request to save PurchaseOrder : {}", purchaseOrderDTO);
        PurchaseOrder purchaseOrder = purchaseOrderMapper.toEntity(purchaseOrderDTO);
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        final Long savedOrderId = purchaseOrder.getId();

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);

        if (currentUserLogin != null) {
            // 1. Gửi cho Thủ kho
            userRepository.findOneByLogin(currentUserLogin).ifPresent(user -> {
                notificationService.createNotification(
                    "Phiếu nhập đang chờ duyệt",
                    "Phiếu nhập nháp #" + savedOrderId + " đã được gửi. Vui lòng chờ sếp duyệt!",
                    user.getId()
                );
            });

            // 2. Gửi cho Admin
            userRepository.findOneByLogin("admin").ifPresent(admin -> {
                notificationService.createNotification(
                    "Có phiếu nhập mới cần duyệt",
                    "Thủ kho " + currentUserLogin + " vừa tạo phiếu nhập #" + savedOrderId + ". Sếp vào duyệt nhé!",
                    admin.getId()
                );
            });
        }

        return purchaseOrderMapper.toDto(purchaseOrder);
    }

    @Override
    public PurchaseOrderDTO update(PurchaseOrderDTO purchaseOrderDTO) {
        log.debug("Request to update PurchaseOrder : {}", purchaseOrderDTO);

        PurchaseOrder existingOrder = purchaseOrderRepository.findById(purchaseOrderDTO.getId())
            .orElseThrow(()-> new BadRequestAlertException("Entity not found", "purchaseOrder", "idnotfound"));
        purchaseOrderDTO.setStatus(existingOrder.getStatus());

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
                purchaseOrderDTO.setStatus(null);
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
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy đơn hàng", "purchaseOrder", "notfound"));
        if (purchaseOrder.getStatus() == PurchaseStatus.COMPLETED) {
            throw new BadRequestAlertException(
                "Không thể xóa đơn nhập kho đã hoàn thành vì sách đã được cộng vào kho!",
                "purchaseOrder",
                "cannotDeleteCompleted"
            );
        }
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

        List<Long> bookIds = lines.stream()
            .map(line -> line.getBook().getId())
            .distinct()//dùng để tạo danh sách duy nhất không trùng lặp
            .collect(Collectors.toList());

        List<InventoryBalance> existingBalances = inventoryBalanceRepository.findByBookIdIn(bookIds);

        Map<Long, InventoryBalance> balanceMap = existingBalances.stream()
            .collect(Collectors.toMap(b -> b.getBook().getId(), b -> b));

        List<InventoryBalance> balancesToSave = new ArrayList<>();
        List<InventoryTransaction> transactionsToSave = new ArrayList<>();

        for (PurchaseOrderLine line : lines) {
            Long bookId = line.getBook().getId();
            Integer quantityToAdd = line.getQuantity();

            // Lấy từ Map ra. Nếu là sách mới tinh chưa có trong Map -> Tạo mới InventoryBalance
            InventoryBalance balance = balanceMap.getOrDefault(bookId,
                new InventoryBalance().book(line.getBook()).quantityOnHand(0));

            balance.setQuantityOnHand(balance.getQuantityOnHand() + quantityToAdd);
            balancesToSave.add(balance);

            InventoryTransaction transaction = new InventoryTransaction();
            transaction.setTransactionType(TransactionType.IN);
            transaction.setReferenceType(ReferenceType.PO);
            transaction.setReferenceId(purchaseOrder.getId());
            transaction.setBook(line.getBook());
            transaction.setQuantity(quantityToAdd);
            transactionsToSave.add(transaction);
        }

        try {
            inventoryBalanceRepository.saveAll(balancesToSave);
            inventoryTransactionRepository.saveAll(transactionsToSave);
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
            log.error("Xung đột dữ liệu tồn kho do có người thao tác cùng lúc trên phiếu nhập PO: {}", purchaseOrder.getCode(), e);
            throw new BadRequestAlertException(
                "Tồn kho của sách vừa thay đổi. Vui lòng F5 tải lại trang và xác nhận lại phiếu nhập!",
                "purchaseOrder",
                "inventoryConflict"
            );
        }

        purchaseOrder.setStatus(PurchaseStatus.COMPLETED);
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);

        User creator = purchaseOrder.getUser();

        if (creator != null && creator.getId() != null) {
            notificationService.createNotification(
                "Phiếu nhập đã được duyệt!",
                "Sếp đã duyệt phiếu nhập #" + purchaseOrder.getId() + " của bạn. Sách đã được cộng vào kho thành công!",
                creator.getId()
            );
        }

        return purchaseOrderMapper.toDto(purchaseOrder);
    }

    @Override
    @Transactional
    public PurchaseOrderDTO createWithLines(PurchaseOrderCreationRequest request) {
        log.debug("Creating PurchaseOrder and Lines together for code: {}", request.getCode());

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setCode(request.getCode());
        purchaseOrder.setStatus(PurchaseStatus.DRAFT);
        purchaseOrder.setCreatedAt(java.time.Instant.now());

        User userProxy = userRepository.getReferenceById(request.getUserId());
        purchaseOrder.setUser(userProxy);

        // Lưu Vỏ đơn để DB cấp cho cái ID
        final PurchaseOrder savedOrder = purchaseOrderRepository.save(purchaseOrder);

        Set<Long> bookIds = request.getItems().stream()
            .map(PurchaseOrderCreationRequest.LineItemRequest::getBookId)
            .collect(Collectors.toSet());

        List<Book> books = bookRepository.findAllById(bookIds);

        Map<Long, Book> bookMap = books.stream()
            .collect(Collectors.toMap(Book::getId, b -> b));

        List<PurchaseOrderLine> linesToSave = new ArrayList<>();
        BigDecimal calculatedTotal = BigDecimal.ZERO;

        for (PurchaseOrderCreationRequest.LineItemRequest item : request.getItems()) {
            // Lấy sách ra từ cái Map trên RAM
            Book book = bookMap.get(item.getBookId());
            if (book == null) {
                throw new BadRequestAlertException("Sách ID " + item.getBookId() + " không tồn tại", "purchaseOrder", "booknotfound");
            }

            PurchaseOrderLine line = new PurchaseOrderLine();
            line.setPurchaseOrder(savedOrder);
            line.setBook(book);
            line.setQuantity(item.getQuantity());

            BigDecimal unitPrice = book.getRetailPrice();
            if (unitPrice == null) {
                unitPrice = BigDecimal.ZERO;
            }
            line.setUnitCost(unitPrice);
            BigDecimal lineTotal = unitPrice.multiply(new BigDecimal(item.getQuantity()));
            calculatedTotal = calculatedTotal.add(lineTotal);
            linesToSave.add(line);
        }
        purchaseOrderLineRepository.saveAll(linesToSave);

        BigDecimal feTotalAmount = request.getTotalAmount() != null ? request.getTotalAmount() : BigDecimal.ZERO;
        if (feTotalAmount.compareTo(calculatedTotal) != 0) {
            throw new BadRequestAlertException(
                "Tổng tiền không khớp! Hệ thống tính: " + calculatedTotal + ", nhưng Front-end gửi lên: " + feTotalAmount,
                "purchaseOrder",
                "totalAmountMismatch"
            );
        }
        savedOrder.setTotalAmount(calculatedTotal);

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (currentUserLogin != null) {
            userRepository.findOneByLogin(currentUserLogin).ifPresent(u -> {
                notificationService.createNotification(
                    "Phiếu nhập đang chờ duyệt",
                    "Phiếu nhập nháp #" + savedOrder.getId() + " đã được gửi. Vui lòng chờ sếp duyệt!",
                    u.getId()
                );
            });
            userRepository.findOneByLogin("admin").ifPresent(admin -> {
                notificationService.createNotification(
                    "Có phiếu nhập mới cần duyệt",
                    "Thủ kho " + currentUserLogin + " vừa tạo phiếu nhập #" + savedOrder.getId() + ". Sếp vào duyệt nhé!",
                    admin.getId()
                );
            });
        }

        return purchaseOrderMapper.toDto(savedOrder);
    }
}
