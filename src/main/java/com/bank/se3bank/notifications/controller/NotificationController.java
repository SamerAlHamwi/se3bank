package com.bank.se3bank.notifications.controller;

import com.bank.se3bank.notifications.model.Notification;
import com.bank.se3bank.notifications.service.NotificationService;
import com.bank.se3bank.shared.dto.NotificationPreference;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "إدارة الإشعارات", description = "عمليات إدارة الإشعارات والإعدادات (Observer Pattern)")
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "إشعارات المستخدم", description = "الحصول على جميع إشعارات المستخدم")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/user/{userId}/unread")
    @Operation(summary = "الإشعارات غير المقروءة", description = "الحصول على الإشعارات غير المقروءة فقط")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }
    
    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "تعيين كمقروء", description = "تعيين إشعار معين كمقروء")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/user/{userId}/read-all")
    @Operation(summary = "تعيين الكل كمقروء", description = "تعيين جميع إشعارات المستخدم كمقروءة")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{notificationId}")
    @Operation(summary = "حذف إشعار", description = "حذف إشعار معين")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/user/{userId}")
    @Operation(summary = "حذف جميع الإشعارات", description = "حذف جميع إشعارات المستخدم")
    public ResponseEntity<Void> deleteAllNotifications(@PathVariable Long userId) {
        notificationService.deleteAllUserNotifications(userId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/user/{userId}/preferences")
    @Operation(summary = "تفضيلات الإشعارات", description = "الحصول على إعدادات تفضيلات الإشعارات")
    public ResponseEntity<NotificationPreference> getPreferences(@PathVariable Long userId) {
        NotificationPreference preferences = notificationService.getNotificationPreferences(userId);
        return ResponseEntity.ok(preferences);
    }
    
    @PatchMapping("/user/{userId}/preferences")
    @Operation(summary = "تحديث التفضيلات", description = "تحديث إعدادات تفضيلات الإشعارات")
    public ResponseEntity<Void> updatePreferences(
            @PathVariable Long userId,
            @RequestBody Map<String, Boolean> preferences) {
        notificationService.updateNotificationPreferences(userId, preferences);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/user/{userId}/stats")
    @Operation(summary = "إحصائيات الإشعارات", description = "الحصول على إحصائيات الإشعارات")
    public ResponseEntity<NotificationService.NotificationStats> getStats(@PathVariable Long userId) {
        NotificationService.NotificationStats stats = notificationService.getNotificationStats(userId);
        return ResponseEntity.ok(stats);
    }
}