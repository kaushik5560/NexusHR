package com.zidio.nexushr.controller;

import com.zidio.nexushr.dto.MessageResponse;
import com.zidio.nexushr.dto.NotificationResponse;
import com.zidio.nexushr.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 1. GET: /api/notifications - Get all notifications for current user
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getMyNotifications(Authentication authentication) {
        String email = authentication.getName();
        List<NotificationResponse> list = notificationService.getMyNotifications(email);
        long unread = notificationService.getUnreadCount(email);

        Map<String, Object> response = new HashMap<>();
        response.put("notifications", list);
        response.put("unreadCount", unread);
        return ResponseEntity.ok(response);
    }

    // 2. PUT: /api/notifications/{id}/read - Mark single notification as read
    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> markAsRead(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        notificationService.markAsRead(id, email);
        return ResponseEntity.ok(new MessageResponse("Notification marked as read"));
    }

    // 3. PUT: /api/notifications/read-all - Mark all notifications as read
    @PutMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> markAllAsRead(Authentication authentication) {
        String email = authentication.getName();
        notificationService.markAllAsRead(email);
        return ResponseEntity.ok(new MessageResponse("All notifications marked as read"));
    }
}
