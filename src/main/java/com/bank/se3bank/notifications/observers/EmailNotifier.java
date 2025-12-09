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
 * مراقب لإرسال الإشعارات عبر البريد الإلكتروني
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotifier implements NotificationObserver {
    
    private final NotificationRepository notificationRepository;
    
    private boolean enabled = true;
    
    @Override
    public void update(String eventType, User user, Account account, Object data) {
        if (!isEnabled()) {
            return;
        }
        
        try {
            String message = generateEmailMessage(eventType, user, account, data);
            String title = generateEmailTitle(eventType);
            
            // حفظ الإشعار في قاعدة البيانات
            Notification notification = Notification.builder()
                    .user(user)
                    .title(title)
                    .message(message)
                    .type(eventType)
                    .channel("EMAIL")
                    .isSent(true)
                    .sentAt(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .build();
            
            if (account != null) {
                notification.setTransactionId(account.getAccountNumber());
            }
            
            notificationRepository.save(notification);
            
            // محاكاة إرسال إيميل
            log.info("📧 إرسال إيميل إلى: {}", user.getEmail());
            log.info("📧 العنوان: {}", title);
            log.info("📧 الرسالة: {}", message);
            
            // في تطبيق حقيقي: استدعاء خدمة إرسال الإيميل
            // emailService.send(user.getEmail(), title, message);
            
        } catch (Exception e) {
            log.error("❌ فشل إرسال إيميل: {}", e.getMessage());
        }
    }
    
    @Override
    public String getObserverType() {
        return "EMAIL";
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        log.info("تم {} إشعارات الإيميل", enabled ? "تفعيل" : "تعطيل");
    }
    
    private String generateEmailTitle(String eventType) {
        return switch (eventType) {
            case "ACCOUNT_CREATED" -> "تم إنشاء حسابك بنجاح";
            case "MONEY_TRANSFER" -> "إشعار تحويل أموال";
            case "WITHDRAWAL" -> "إشعار سحب أموال";
            case "DEPOSIT" -> "إشعار إيداع أموال";
            case "LOW_BALANCE" -> "تحذير: رصيد منخفض";
            case "SUSPICIOUS_ACTIVITY" -> "تحذير: نشاط مشبوه";
            case "PASSWORD_CHANGED" -> "تم تغيير كلمة المرور";
            case "LOGIN_ALERT" -> "تنبيه تسجيل دخول";
            default -> "إشعار من البنك";
        };
    }
    
    private String generateEmailMessage(String eventType, User user, Account account, Object data) {
        StringBuilder message = new StringBuilder();
        
        message.append("عزيزي/عزيزتي ").append(user.getFullName()).append("،\n\n");
        
        switch (eventType) {
            case "ACCOUNT_CREATED":
                message.append("يسرنا إعلامك بأنه تم إنشاء حسابك بنجاح.\n");
                if (account != null) {
                    message.append("رقم الحساب: ").append(account.getAccountNumber()).append("\n");
                    message.append("نوع الحساب: ").append(account.getAccountType().getArabicName()).append("\n");
                    message.append("الرصيد الافتتاحي: ").append(account.getBalance()).append(" USD\n");
                }
                break;
                
            case "MONEY_TRANSFER":
                message.append("تم إجراء عملية تحويل أموال.\n");
                if (data instanceof String[]) {
                    String[] transferData = (String[]) data;
                    message.append("المبلغ: ").append(transferData[0]).append(" USD\n");
                    message.append("من حساب: ").append(transferData[1]).append("\n");
                    message.append("إلى حساب: ").append(transferData[2]).append("\n");
                    message.append("رقم العملية: ").append(transferData[3]).append("\n");
                }
                break;
                
            case "WITHDRAWAL":
                message.append("تم سحب مبلغ من حسابك.\n");
                if (data instanceof String[]) {
                    String[] withdrawalData = (String[]) data;
                    message.append("المبلغ: ").append(withdrawalData[0]).append(" USD\n");
                    message.append("الرصيد السابق: ").append(withdrawalData[1]).append(" USD\n");
                    message.append("الرصيد الحالي: ").append(withdrawalData[2]).append(" USD\n");
                }
                break;
                
            case "LOW_BALANCE":
                message.append("تحذير: رصيد حسابك منخفض.\n");
                if (account != null) {
                    message.append("رقم الحساب: ").append(account.getAccountNumber()).append("\n");
                    message.append("الرصيد الحالي: ").append(account.getBalance()).append(" USD\n");
                    message.append("الحد الأدنى الموصى به: 100 USD\n");
                }
                break;
                
            default:
                message.append("لديك إشعار جديد من البنك.\n");
        }
        
        message.append("\nمع تحيات،\nفريق SE3 Bank");
        return message.toString();
    }
}