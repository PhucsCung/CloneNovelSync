package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.NotificationDTO;
import java.util.List;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Notification}.
 */
public interface NotificationService {

    /**
     * Tạo và lưu một thông báo mới xuống Database.
     * @param title Tiêu đề thông báo
     * @param message Nội dung
     * @param recipientId ID của người nhận (nếu null là gửi cho tất cả)
     * @return DTO của thông báo vừa tạo
     */
    NotificationDTO createNotification(String title, String message, Long recipientId);

    /**
     * Lấy danh sách các thông báo CHƯA ĐỌC của người dùng đang đăng nhập hiện tại.
     * @return Danh sách DTO
     */
    List<NotificationDTO> getMyUnreadNotifications();

    /**
     * Đánh dấu một thông báo là đã đọc (isRead = true).
     * @param id ID của thông báo
     */
    void markAsRead(Long id);
}
