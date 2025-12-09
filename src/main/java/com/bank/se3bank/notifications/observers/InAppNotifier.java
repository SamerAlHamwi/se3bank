package com.bank.se3bank.notifications.observers;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.notifications.model.Notification;
import com.bank.se3bank.notifications.repository.NotificationRepository;
import com.bank.se3bank.users.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * مراقب للإشعارات داخل التطبيق
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InAppNotifier implements NotificationObserver {
    
    private final NotificationRepository notificationRepository;
    
    private boolean enabled = true;
    
    @Override
    public void update(String eventType, User user, Account account, Object data) {
        if (!isEnabled()) {
            return;
        }
        
        try {
            String title = generateTitle(eventType);
            String message = generateMessage(eventType, user, account, data);
            
            // حفظ الإشعار في قاعدة البيانات (غير مقروء)
            Notification notification = Notification.builder()
                    .user(user)
                    .title(title)
                    .message(message)
                    .type(eventType)
                    .channel("IN_APP")
                    .isSent(true)
                    .sentAt(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .isRead(false) // غير مقروء
                    .build();
            
            if (account != null) {
                notification.setTransactionId(account.getAccountNumber());
            }
            
            notificationRepository.save(notification);
            
            log.info("📱 إشعار داخل التطبيق للمستخدم: {}", user.getUsername());
            log.info("📱 العنوان: {}", title);
            log.info("📱 الرسالة: {}", message);
            
            // في تطبيق حقيقي: إرسال عبر WebSocket أو Push Notification
            
        } catch (Exception e) {
            log.error("❌ فشل إرسال إشعار داخل التطبيق: {}", e.getMessage());
        }
    }
    
    @Override
    public String getObserverType() {
        return "IN_APP";
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        log.info("تم {} إشعارات داخل التطبيق", enabled ? "تفعيل" : "تعطيل");
    }
    
    private String generateTitle(String eventType) {
        return switch (eventType) {
            case "ACCOUNT_CREATED" -> "🎉 تم إنشاء حساب جديد";
            case "MONEY_TRANSFER" -> "💸 تحويل أموال";
            case "WITHDRAWAL" -> "💰 سحب نقدي";
            case "DEPOSIT" -> "📥 إيداع ناجح";
            case "LOW_BALANCE" -> "⚠️  رصيد منخفض";
            case "INTEREST_ADDED" -> "📈 فائدة مضافة";
            case "BILL_PAID" -> "✅ فاتورة مدفوعة";
            default -> "📢 إشعار جديد";
        };
    }
    
    private String generateMessage(String eventType, User user, Account account, Object data) {
        StringBuilder message = new StringBuilder();
        
        switch (eventType) {
            case "ACCOUNT_CREATED":
                message.append("مرحباً ").append(user.getFirstName()).append("! 🎊\n");
                message.append("تم إنشاء حسابك بنجاح.\n");
                if (account != null) {
                    message.append("رقم الحساب: ").append(account.getAccountNumber()).append("\n");
                    message.append("الرصيد: ").append(account.getBalance()).append(" USD");
                }
                break;
                
            case "MONEY_TRANSFER":
                message.append("تم إجراء تحويل أموال.\n");
                if (data instanceof String[]) {
                    String[] transferData = (String[]) data;
                    message.append("المبلغ: ").append(transferData[0]).append(" USD\n");
                    message.append("رقم العملية: ").append(transferData[3]);
                }
                break;
                
            case "LOW_BALANCE":
                message.append("انتباه! ⚠️\n");
                message.append("رصيد حسابك منخفض.\n");
                if (account != null) {
                    message.append("الرصيد الحالي: ").append(account.getBalance()).append(" USD");
                }
                break;
                
            case "INTEREST_ADDED":
                message.append("تم إضافة الفائدة الشهرية! 📈\n");
                if (data instanceof String[]) {
                    String[] interestData = (String[]) data;
                    message.append("المبلغ: ").append(interestData[0]).append(" USD\n");
                    message.append("الرصيد الجديد: ").append(interestData[1]).append(" USD");
                }
                break;
                
            default:
                message.append("لديك إشعار جديد من البنك.");
        }
        
        return message.toString();
    }
}