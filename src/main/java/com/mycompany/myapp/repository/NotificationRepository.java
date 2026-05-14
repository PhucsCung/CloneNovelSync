package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository cho Entity Notification.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    //thông báo CHƯA ĐỌC của 1 user cụ thể (Sắp xếp mới nhất lên đầu)
    List<Notification> findByRecipientIdAndIsReadFalseOrderByCreatedDateDesc(Long recipientId);

    //thông báo CHUNG toàn hệ thống (Dành cho tất cả mọi người, chưa đọc)
    List<Notification> findByRecipientIdIsNullAndIsReadFalseOrderByCreatedDateDesc();
}
