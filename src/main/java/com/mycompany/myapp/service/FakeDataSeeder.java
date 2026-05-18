package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.domain.enumeration.*;
import com.mycompany.myapp.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.UUID;

/**
 * Class này sẽ tự động chạy 1 lần duy nhất mỗi khi bật Server
 * để bơm dữ liệu mẫu (Sách, Tồn Kho, Lịch sử bán 3 tháng) vào DB.
 */
@Component
public class FakeDataSeeder implements CommandLineRunner {

    private final Logger log = LoggerFactory.getLogger(FakeDataSeeder.class);

    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;
    private final InventoryBalanceRepository inventoryBalanceRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderLineRepository salesOrderLineRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate; // Vũ khí bí mật để hack ngày tháng

    public FakeDataSeeder(
        BookRepository bookRepository,
        PublisherRepository publisherRepository,
        InventoryBalanceRepository inventoryBalanceRepository,
        SalesOrderRepository salesOrderRepository,
        SalesOrderLineRepository salesOrderLineRepository,
        UserRepository userRepository,
        JdbcTemplate jdbcTemplate
    ) {
        this.bookRepository = bookRepository;
        this.publisherRepository = publisherRepository;
        this.inventoryBalanceRepository = inventoryBalanceRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.salesOrderLineRepository = salesOrderLineRepository;
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Nếu trong DB đã có Sách rồi thì thôi không tạo nữa, tránh bị lặp data
        if (bookRepository.count() > 0) {
            log.info("✅ Dữ liệu Sách đã tồn tại. Bỏ qua bước Fake Data.");
            return;
        }

        log.info("⏳ Đang tiến hành bơm Fake Data để test AI...");

        // 1. Tạo Nhà xuất bản
        Publisher nxb = new Publisher();
        nxb.setCode("NXB001");
        nxb.setName("NXB Kim Đồng");
        nxb.setStatus(PublisherStatus.ACTIVE);
        nxb = publisherRepository.save(nxb);

        // 2. Tạo 2 cuốn Sách để test
        // Cuốn 1: Bán siêu chạy, sắp hết hàng (Để kích hoạt AI cảnh báo)
        Book book1 = new Book();
        book1.setCode("BOOK-HOT");
        book1.setTitle("Đắc Nhân Tâm (Bán Siêu Chạy)");
        book1.setRetailPrice(new BigDecimal("100000"));
        book1.setStatus(BookStatus.AVAILABLE);
        book1.setPublisher(nxb);
        book1 = bookRepository.save(book1);

        // Cuốn 2: Bán ế, tồn kho nhiều (Để xem AI có bơ nó đi không)
        Book book2 = new Book();
        book2.setCode("BOOK-NORMAL");
        book2.setTitle("Sách Lập Trình Java (Bán Chậm)");
        book2.setRetailPrice(new BigDecimal("200000"));
        book2.setStatus(BookStatus.AVAILABLE);
        book2.setPublisher(nxb);
        book2 = bookRepository.save(book2);

        // 3. Tạo Tồn kho cho 2 cuốn sách
        InventoryBalance ib1 = new InventoryBalance();
        ib1.setBook(book1);
        ib1.setQuantityOnHand(15); // Chỉ còn 15 cuốn
        inventoryBalanceRepository.save(ib1);

        InventoryBalance ib2 = new InventoryBalance();
        ib2.setBook(book2);
        ib2.setQuantityOnHand(500); // Còn tận 500 cuốn
        inventoryBalanceRepository.save(ib2);

        // 4. Lấy Admin để gán vào hóa đơn
        User admin = userRepository.findOneByLogin("admin").orElse(null);
        if (admin == null) {
            log.warn("❌ Không tìm thấy user 'admin'. Dừng Fake Data Hóa đơn.");
            return;
        }

        // 5. Tạo Hóa Đơn Bán Hàng (SalesOrder) lùi về 3 tháng trước
        YearMonth today = YearMonth.now();

        // Mảng giả lập số lượng bán của "Đắc Nhân Tâm" [Tháng -3, Tháng -2, Tháng -1]
        // Trend đang tăng mạnh: 50 -> 80 -> 150
        int[] hotSales = {50, 80, 150};

        // Mảng giả lập số lượng bán của "Sách Java" (Đều đều 5 cuốn)
        int[] normalSales = {5, 3, 5};

        for (int i = 3; i >= 1; i--) {
            // Lấy 1 ngày bất kỳ ở giữa tháng trước (VD: ngày 15)
            Instant historicalDate = today.minusMonths(i).atDay(15).atStartOfDay(ZoneId.systemDefault()).toInstant();

            // Lưu Order (Spring Boot sẽ tự lấy ngày hiện tại đè vào createdAt)
            SalesOrder order = new SalesOrder();
            order.setCode("SO-" + UUID.randomUUID().toString().substring(0, 8));
            order.setStatus(SalesStatus.COMPLETED); // Bắt buộc phải COMPLETED thì AI mới tính
            order.setUser(admin);
            order.setTotalAmount(new BigDecimal("500000"));
            order = salesOrderRepository.save(order);

            // ⚠️ HACK NGÀY THÁNG: Dùng JDBC đè ngày quá khứ vào thẳng Database
            jdbcTemplate.update("UPDATE sales_order SET created_at = ? WHERE id = ?", historicalDate, order.getId());

            // Lưu Order Line cho Sách HOT
            SalesOrderLine line1 = new SalesOrderLine();
            line1.setSalesOrder(order);
            line1.setBook(book1);
            line1.setQuantity(hotSales[3 - i]); // Gắn số lượng bán tương ứng
            line1.setUnitPrice(book1.getRetailPrice());
            salesOrderLineRepository.save(line1);

            // Lưu Order Line cho Sách NORMAL
            SalesOrderLine line2 = new SalesOrderLine();
            line2.setSalesOrder(order);
            line2.setBook(book2);
            line2.setQuantity(normalSales[3 - i]);
            line2.setUnitPrice(book2.getRetailPrice());
            salesOrderLineRepository.save(line2);
        }

        log.info("🎉 Bơm Fake Data thành công! DB đã có sẵn Sách, Tồn kho và Hóa đơn 3 tháng qua.");
    }
}
