package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.InventoryBalance;
import com.mycompany.myapp.repository.InventoryBalanceRepository;
import com.mycompany.myapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventoryScheduler {

    private final Logger log = LoggerFactory.getLogger(InventoryScheduler.class);

    private final InventoryBalanceRepository inventoryBalanceRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public InventoryScheduler(
        InventoryBalanceRepository inventoryBalanceRepository,
        NotificationService notificationService,
        UserRepository userRepository
    ) {
        this.inventoryBalanceRepository = inventoryBalanceRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    /**
     * Lịch chạy: 0 0 0 * * ? (Đúng 12h đêm mỗi ngày)
     * Ngưỡng cảnh báo: Dưới 10 cuốn
     */
    @Scheduled(cron = "0 0 0 * * ?")
//    @Scheduled(fixedRate = 60000)//1p gửi 1 lần
    public void checkLowInventoryAndNotify() {
        log.info("Bắt đầu chạy Cron Job: Kiểm tra hàng tồn kho lúc nửa đêm...");
        //Quét kho tìm sách sắp hết (dưới 10 cuốn)
        List<InventoryBalance> lowStockItems = inventoryBalanceRepository.findByQuantityOnHandLessThan(10);
        if (lowStockItems.isEmpty()) {
            log.info("Kho hàng vẫn dồi dào, không cần thông báo.");
            return;
        }
        //Gom danh sách lại thành 1 chuỗi để báo cáo cho gọn
        String bookTitles = lowStockItems.stream()
            .map(item -> "- " + item.getBook().getTitle() + " (còn " + item.getQuantityOnHand() + " cuốn)")
            .collect(Collectors.joining("\n"));

        userRepository.findOneByLogin("admin").ifPresent(admin -> {
            String title = "CẢNH BÁO TỒN KHO: " + lowStockItems.size() + " đầu sách sắp cạn!";
            String content = "Sếp ơi, hệ thống quét kho phát hiện các sách sau sắp hết hàng, sếp nhập thêm nhé:\n" + bookTitles;

            notificationService.createNotification(title, content, admin.getId());
            log.info("Đã bắn thông báo hết hàng cho Sếp thành công!");
        });
    }
}
