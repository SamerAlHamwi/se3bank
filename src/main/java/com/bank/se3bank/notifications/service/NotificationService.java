package com.bank.se3bank.notifications.service;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.notifications.model.Notification;
import com.bank.se3bank.notifications.observers.NotificationObserver;
import com.bank.se3bank.notifications.publisher.NotificationPublisher;
import com.bank.se3bank.notifications.repository.NotificationRepository;
import com.bank.se3bank.shared.dto.NotificationPreference;
import com.bank.se3bank.users.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * خدمة الإشعارات الرئيسية
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationPublisher notificationPublisher;
    private final NotificationRepository notificationRepository;
    
    // ========== Account Events ==========
    
    /**
     * إرسال إشعار إنشاء حساب
     */
    public void sendAccountCreatedNotification(User user, Account account) {
        log.info("📨 إرسال إشعار إنشاء حساب للمستخدم: {}", user.getUsername());
        
        notificationPublisher.notifyObservers(
                "ACCOUNT_CREATED", user, account, null);
    }
    
    /**
     * إرسال إشعار تحويل أموال
     */
    public void sendTransferNotification(User fromUser, User toUser, 
                                         Double amount, String transactionId) {
        log.info("📨 إرسال إشعار تحويل أموال: {} USD", amount);
        
        // إشعار للمرسل
        String[] senderData = {
                amount.toString(),
                "حسابك",
                toUser.getFullName(),
                transactionId
        };
        notificationPublisher.notifyObservers(
                "MONEY_TRANSFER", fromUser, null, senderData);
        
        // إشعار للمستلم
        String[] receiverData = {
                amount.toString(),
                fromUser.getFullName(),
                "حسابك",
                transactionId
        };
        notificationPublisher.notifyObservers(
                "MONEY_TRANSFER", toUser, null, receiverData);
    }
    
    /**
     * إرسال إشعار سحب أموال
     */
    public void sendWithdrawalNotification(User user, Double amount, 
                                          Double oldBalance, Double newBalance) {
        log.info("📨 إرسال إشعار سحب: {} USD", amount);
        
        String[] data = {
                amount.toString(),
                oldBalance.toString(),
                newBalance.toString()
        };
        notificationPublisher.notifyObservers(
                "WITHDRAWAL", user, null, data);
    }
    
    /**
     * إرسال إشعار إيداع أموال
     */
    public void sendDepositNotification(User user, Double amount, 
                                        Double oldBalance, Double newBalance) {
        log.info("📨 إرسال إشعار إيداع: {} USD", amount);
        
        String[] data = {
                amount.toString(),
                oldBalance.toString(),
                newBalance.toString()
        };
        notificationPublisher.notifyObservers(
                "DEPOSIT", user, null, data);
    }
    
    /**
     * إرسال إشعار رصيد منخفض
     */
    public void sendLowBalanceNotification(User user, Account account) {
        log.info("⚠️  إرسال إشعار رصيد منخفض للحساب: {}", account.getAccountNumber());
        
        notificationPublisher.notifyObservers(
                "LOW_BALANCE", user, account, null);
    }
    
    // ========== Security Events ==========
    
    /**
     * إرسال إشعار تسجيل دخول
     */
    public void sendLoginAlert(User user, String ipAddress, String device) {
        log.info("🔐 إرسال إشعار تسجيل دخول للمستخدم: {}", user.getUsername());
        
        String[] data = {ipAddress, device, LocalDateTime.now().toString()};
        notificationPublisher.notifyObservers(
                "LOGIN_ALERT", user, null, data);
    }
    
    /**
     * إرسال إشعار نشاط مشبوه
     */
    public void sendSuspiciousActivityAlert(User user, Account account, String activity) {
        log.warn("🚨 إرسال إشعار نشاط مشبوه: {}", activity);
        
        String[] data = {activity, LocalDateTime.now().toString()};
        notificationPublisher.notifyObservers(
                "SUSPICIOUS_ACTIVITY", user, account, data);
    }
    
    /**
     * إرسال إشعار تغيير كلمة المرور
     */
    public void sendPasswordChangedNotification(User user) {
        log.info("🔑 إرسال إشعار تغيير كلمة المرور للمستخدم: {}", user.getUsername());
        
        notificationPublisher.notifyObservers(
                "PASSWORD_CHANGED", user, null, null);
    }
    
    // ========== Banking Events ==========
    
    /**
     * إرسال إشعار إضافة فائدة
     */
    public void sendInterestAddedNotification(User user, Account account, 
                                              Double interestAmount) {
        log.info("📈 إرسال إشعار إضافة فائدة: {} USD", interestAmount);
        
        String[] data = {
                interestAmount.toString(),
                account.getBalance().toString()
        };
        notificationPublisher.notifyObservers(
                "INTEREST_ADDED", user, account, data);
    }
    
    /**
     * إرسال إشعار دفع فاتورة
     */
    public void sendBillPaymentNotification(User user, String billName, 
                                            Double amount, String reference) {
        log.info("✅ إرسال إشعار دفع فاتورة: {}", billName);
        
        String[] data = {billName, amount.toString(), reference};
        notificationPublisher.notifyObservers(
                "BILL_PAID", user, null, data);
    }
    
    /**
     * إرسال إشعار إنشاء مجموعة حسابات
     */
    public void sendGroupCreatedNotification(User user, Object group) {
        log.info("🏢 إرسال إشعار إنشاء مجموعة حسابات");
        
        notificationPublisher.notifyObservers(
                "GROUP_CREATED", user, null, group);
    }
    
    /**
     * إرسال إشعار تجديد خدمة
     */
    public void sendServiceRenewalNotification(User user, String serviceName, 
                                               Double fee) {
        log.info("🔄 إرسال إشعار تجديد خدمة: {}", serviceName);
        
        String[] data = {serviceName, fee.toString()};
        notificationPublisher.notifyObservers(
                "SERVICE_RENEWAL", user, null, data);
    }
    
    // ========== Notification Management ==========
    
    /**
     * الحصول على إشعارات المستخدم
     */
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * الحصول على إشعارات غير مقروءة
     */
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }
    
    /**
     * تعيين إشعار كمقروء
     */
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
            log.info("✅ تم تعيين الإشعار كمقروء: {}", notificationId);
        });
    }
    
    /**
     * تعيين جميع إشعارات المستخدم كمقروءة
     */
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = getUnreadNotifications(userId);
        unreadNotifications.forEach(notification -> {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
        });
        notificationRepository.saveAll(unreadNotifications);
        log.info("✅ تم تعيين جميع إشعارات المستخدم كمقروءة: {}", userId);
    }
    
    /**
     * حذف إشعار
     */
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
        log.info("🗑️ تم حذف الإشعار: {}", notificationId);
    }
    
    /**
     * حذف جميع إشعارات المستخدم
     */
    public void deleteAllUserNotifications(Long userId) {
        notificationRepository.deleteByUserId(userId);
        log.info("🗑️ تم حذف جميع إشعارات المستخدم: {}", userId);
    }
    
    /**
     * الحصول على تفضيلات الإشعارات
     */
    public NotificationPreference getNotificationPreferences(Long userId) {
        // في تطبيق حقيقي، قد يكون هذا في قاعدة بيانات منفصلة
        return NotificationPreference.builder()
                .userId(userId)
                .emailEnabled(notificationPublisher.isObserverEnabled("EMAIL"))
                .smsEnabled(notificationPublisher.isObserverEnabled("SMS"))
                .inAppEnabled(notificationPublisher.isObserverEnabled("IN_APP"))
                .lowBalanceAlert(true)
                .transferAlert(true)
                .loginAlert(true)
                .marketingEmails(false)
                .build();
    }
    
    /**
     * تحديث تفضيلات الإشعارات
     */
    public void updateNotificationPreferences(Long userId, Map<String, Boolean> preferences) {
        preferences.forEach((key, value) -> {
            if (key.startsWith("channel_")) {
                String channel = key.replace("channel_", "").toUpperCase();
                notificationPublisher.setObserverEnabled(channel, value);
            }
        });
        log.info("⚙️ تم تحديث تفضيلات الإشعارات للمستخدم: {}", userId);
    }
    
    /**
     * إرسال إشعار مخصص
     */
    public void sendCustomNotification(User user, String title, String message, 
                                       String channel, String eventType) {
        log.info("✉️ إرسال إشعار مخصص للمستخدم: {}", user.getUsername());
        
        // إنشاء الإشعار وحفظه
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(eventType != null ? eventType : "CUSTOM")
                .channel(channel != null ? channel : "IN_APP")
                .isSent(true)
                .sentAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        
        notificationRepository.save(notification);
        
        // إرساله عبر القناة المحددة
        if (channel != null) {
            notificationPublisher.notifyObserver(
                    channel.toUpperCase(), 
                    eventType != null ? eventType : "CUSTOM", 
                    user, null, message);
        } else {
            // إرساله عبر جميع القنوات
            notificationPublisher.notifyObservers(
                    eventType != null ? eventType : "CUSTOM", 
                    user, null, message);
        }
    }
    
    /**
     * إحصائيات الإشعارات
     */
    public NotificationStats getNotificationStats(Long userId) {
        long total = notificationRepository.countByUserId(userId);
        long unread = notificationRepository.countByUserIdAndIsReadFalse(userId);
        long today = notificationRepository.countByUserIdAndCreatedAtAfter(
                userId, LocalDateTime.now().minusDays(1));
        
        return NotificationStats.builder()
                .userId(userId)
                .totalNotifications(total)
                .unreadNotifications(unread)
                .todayNotifications(today)
                .build();
    }
    
    /**
     * DTO لإحصائيات الإشعارات
     */
    @lombok.Data
    @lombok.Builder
    public static class NotificationStats {
        private Long userId;
        private Long totalNotifications;
        private Long unreadNotifications;
        private Long todayNotifications;
    }
}