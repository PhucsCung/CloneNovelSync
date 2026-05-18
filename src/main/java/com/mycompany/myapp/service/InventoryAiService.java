package com.mycompany.myapp.service;

import com.mycompany.myapp.repository.BookRepository;
import com.mycompany.myapp.repository.InventoryBalanceRepository;
import com.mycompany.myapp.repository.SalesOrderLineRepository;
import com.mycompany.myapp.service.dto.PredictRequestDTO;
import com.mycompany.myapp.service.dto.PredictResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryAiService {

    private final Logger log = LoggerFactory.getLogger(InventoryAiService.class);

    private final RestTemplate restTemplate;

    private final InventoryBalanceRepository inventoryBalanceRepository;

    private final SalesOrderLineRepository salesOrderLineRepository;

    private final BookRepository bookRepository;

    // Khởi tạo RestTemplate để bắn API
    public InventoryAiService(InventoryBalanceRepository inventoryBalanceRepository,
                              SalesOrderLineRepository salesOrderLineRepository,
                              BookRepository bookRepository) {
        this.restTemplate = new RestTemplate();
        this.inventoryBalanceRepository = inventoryBalanceRepository;
        this.salesOrderLineRepository = salesOrderLineRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Hàm này sẽ gửi lịch sử bán hàng sang AI Python và lấy kết quả dự báo về
     */
    public PredictResponseDTO getPredictionFromAI(PredictRequestDTO requestDTO) {
        log.debug("Bắt đầu gửi request sang AI Server cho sách ID: {}", requestDTO.getBookId());

        // Đường dẫn API của con Python mình vừa dựng
        String aiServerUrl = "http://127.0.0.1:8000/predict";

        try {
            // Lệnh postForObject sẽ tự động:
            // 1. Ép requestDTO thành JSON gửi đi
            // 2. Chờ Python tính toán
            // 3. Ép JSON trả về thành object PredictResponseDTO cho mình xài
            PredictResponseDTO response = restTemplate.postForObject(
                aiServerUrl,
                requestDTO,
                PredictResponseDTO.class
            );

            log.debug("AI trả về kết quả thành công: {}", response);
            return response;

        } catch (Exception e) {
            log.error("Lỗi khi kết nối với AI Server! Đảm bảo con Python cổng 8000 đang bật.", e);
            throw new RuntimeException("cannot connect to server AI");
        }
    }

    public Optional<PredictResponseDTO> predictSingleBook(Long bookId) {
        log.debug("User bấm nút dự báo realtime cho sách ID: {}", bookId);
        if (!bookRepository.existsById(bookId)) {
            return Optional.empty();
        }
        //tồn kho
        Integer currentStock = inventoryBalanceRepository.findByBookId(bookId)
            .map(balance -> balance.getQuantityOnHand())
            .orElse(0);

        //lịch sử 3 tháng
        List<Integer> historySales = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 3; i >= 1; i--) {
            YearMonth targetMonth = YearMonth.from(today).minusMonths(i);
            Instant start = targetMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
            Instant end = targetMonth.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();
            Integer monthSales = salesOrderLineRepository.sumQuantityByBookAndDateRange(bookId, start, end);
            historySales.add(monthSales != null ? monthSales : 0);
        }

        //Đóng gói và gọi AI
        PredictRequestDTO request = new PredictRequestDTO();
        request.setBookId(bookId);
        request.setHistorySales(historySales);
        request.setCurrentStock(currentStock);

        PredictResponseDTO response = this.getPredictionFromAI(request);
        return Optional.ofNullable(response);
    }
}
