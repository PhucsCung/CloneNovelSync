package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Notification;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.NotificationRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.NotificationService;
import com.mycompany.myapp.service.dto.NotificationDTO;
import com.mycompany.myapp.service.mapper.NotificationMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Notification}.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserRepository userRepository;

    public NotificationServiceImpl(
        NotificationRepository notificationRepository,
        NotificationMapper notificationMapper,
        UserRepository userRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.userRepository = userRepository;
    }

    @Override
    public NotificationDTO createNotification(String title, String message, Long recipientId) {
        log.debug("Request to save Notification : {}", title);

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setIsRead(false);
        notification.setRecipientId(recipientId);

        notification = notificationRepository.save(notification);
        return notificationMapper.toDto(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getMyUnreadNotifications() {
        log.debug("Request to get my unread Notifications");

        String currentUserLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new RuntimeException("Không tìm thấy user đăng nhập"));

        User currentUser = userRepository.findOneByLogin(currentUserLogin)
            .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        List<Notification> myNotifications = notificationRepository
            .findByRecipientIdAndIsReadFalseOrderByCreatedDateDesc(currentUser.getId());

        List<Notification> systemNotifications = notificationRepository
            .findByRecipientIdIsNullAndIsReadFalseOrderByCreatedDateDesc();

        myNotifications.addAll(systemNotifications);

        return myNotifications.stream()
            .map(notificationMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long id) {
        log.debug("Request to mark Notification as read : {}", id);
        Optional<Notification> notificationOpt = notificationRepository.findById(id);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
    }
}
