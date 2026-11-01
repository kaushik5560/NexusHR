package com.zidio.nexushr.service;

import com.zidio.nexushr.dto.NotificationResponse;
import com.zidio.nexushr.entity.Notification;
import com.zidio.nexushr.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification createNotification(String recipientEmail, String title, String message, String type) {
        Notification notification = Notification.builder()
                .recipientEmail(recipientEmail)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .build();
        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(String email) {
        return notificationRepository.findByRecipientEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(NotificationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String email) {
        return notificationRepository.countByRecipientEmailAndIsReadFalse(email);
    }

    @Transactional
    public void markAsRead(Long id, String email) {
        notificationRepository.findById(id).ifPresent(notification -> {
            if (notification.getRecipientEmail().equalsIgnoreCase(email)) {
                notification.setIsRead(true);
                notificationRepository.save(notification);
            }
        });
    }

    @Transactional
    public void markAllAsRead(String email) {
        List<Notification> list = notificationRepository.findByRecipientEmailOrderByCreatedAtDesc(email);
        list.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(list);
    }
}
