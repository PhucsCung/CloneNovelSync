package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.repository.BookRepository;
import com.mycompany.myapp.repository.InventoryBalanceRepository;
import com.mycompany.myapp.repository.SalesOrderLineRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.dto.PredictRequestDTO;
import com.mycompany.myapp.service.dto.PredictResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventoryAiJob {

    private final Logger log = LoggerFactory.getLogger(InventoryAiJob.class);

    private final InventoryAiService aiService;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final InventoryBalanceRepository inventoryBalanceRepository;
    private final SalesOrderLineRepository salesOrderLineRepository;

    public InventoryAiJob(
        InventoryAiService aiService,
        BookRepository bookRepository,
        UserRepository userRepository,
        NotificationService notificationService,
        InventoryBalanceRepository inventoryBalanceRepository,
        SalesOrderLineRepository salesOrderLineRepository
    ) {
        this.aiService = aiService;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.inventoryBalanceRepository = inventoryBalanceRepository;
        this.salesOrderLineRepository = salesOrderLineRepository;
    }

        @Scheduled(cron = "0 0 2 * * ?") // 2h sáng
//    @Scheduled(fixedRate = 60000) // Test 1 phút
    public void runDailyInventoryPrediction() {
        log.info("[AI-JOB] Bắt đầu quét kho với tốc độ ánh sáng (No N+1)...");

        userRepository.findOneByLogin("admin").ifPresent(admin -> {
            List<Book> books = bookRepository.findAll();
            //TẤT CẢ tồn kho và nhét vào Map<BookId, Số lượng>
            Map<Long, Integer> stockMap = inventoryBalanceRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                    ib -> ib.getBook().getId(),
                    ib -> ib.getQuantityOnHand()
                ));

            // Lấy Lịch sử bán hàng: Lưu vào mảng chứa 3 Map cho 3 tháng
            List<Map<Long, Integer>> salesDataLast3Months = new ArrayList<>();
            LocalDate today = LocalDate.now();

            for (int i = 3; i >= 1; i--) {
                YearMonth targetMonth = YearMonth.from(today).minusMonths(i);//lùi tháng
                Instant start = targetMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();//atStartOfDay ý là 00:00 của ngày đầu tiên tháng
                Instant end = targetMonth.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();

                //Lấy danh sách nhóm theo tháng
                List<Object[]> monthlySalesData = salesOrderLineRepository.sumQuantityAllBooksInDateRange(start, end);

                // Chuyển mảng Object[] của Hibernate thành Map<BookId, Doanh số>
                Map<Long, Integer> monthMap = new HashMap<>();
                for (Object[] row : monthlySalesData) {
                    Long bookId = (Long) row[0];
                    // Hibernate hàm SUM() thường trả về Long, ép về int qua Number
                    Integer totalSales = ((Number) row[1]).intValue();
                    monthMap.put(bookId, totalSales);
                }
                salesDataLast3Months.add(monthMap);
            }

            for (Book book : books) {
                try {
                    Long bId = book.getId();

                    // Lấy tồn kho O(1)
                    Integer currentStock = stockMap.getOrDefault(bId, 0);

                    // Lấy doanh số 3 tháng O(1)
                    List<Integer> historySales = new ArrayList<>();
                    for (Map<Long, Integer> monthMap : salesDataLast3Months) {
                        historySales.add(monthMap.getOrDefault(bId, 0));
                    }

                    // Bỏ qua nếu 3 tháng không bán được cuốn nào
                    if (historySales.stream().mapToInt(Integer::intValue).sum() == 0) {
                        continue;
                    }

                    // GỌI AI
                    PredictRequestDTO request = new PredictRequestDTO();
                    request.setBookId(bId);
                    request.setHistorySales(historySales);
                    request.setCurrentStock(currentStock);

                    PredictResponseDTO response = aiService.getPredictionFromAI(request);

                    if (response != null && response.getRecommendRestock() > 0) {
                        String title = "AI CẢNH BÁO: Sách " + book.getTitle() + " chuẩn bị cháy hàng!";
                        String content = String.format(
                            "Sếp ơi, AI dự báo tháng này sách '%s' cần tiêu thụ tổng cộng %d cuốn.\n" +
                                "Kho hiện tại chỉ còn %d cuốn.\n" +
                                "Khuyên sếp nên nhập gấp %d cuốn nữa để không bị đứt gãy doanh thu!",
                            book.getTitle(), response.getPredictedSales(), currentStock, response.getRecommendRestock()
                        );

                        notificationService.createNotification(title, content, admin.getId());
                    }

                } catch (Exception e) {
                    log.error("Lỗi gửi AI cho sách ID {}: {}", book.getId(), e.getMessage());
                }
            }
        });

        log.info("[AI-JOB] Hoàn tất!");
    }
}
