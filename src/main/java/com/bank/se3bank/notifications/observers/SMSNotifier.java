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
 * مراقب لإرسال الإشعارات عبر الرسائل النصية
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SMSNotifier implements NotificationObserver {
    
    private final NotificationRepository notificationRepository;
    
    private boolean enabled = true;
    
    @Override
    public void update(String eventType, User user, Account account, Object data) {
        if (!isEnabled() || user.getPhoneNumber() == null) {
            return;
        }
        
        try {
            String message = generateSMSMessage(eventType, user, account, data);
            
            // حفظ الإشعار في قاعدة البيانات
            Notification notification = Notification.builder()
                    .user(user)
                    .title("إشعار SMS")
                    .message(message)
                    .type(eventType)
                    .channel("SMS")
                    .isSent(true)
                    .sentAt(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .build();
            
            notificationRepository.save(notification);
            
            // محاكاة إرسال SMS
            log.info("📱 إرسال SMS إلى: {}", user.getPhoneNumber());
            log.info("📱 الرسالة: {}", message);
            
            // في تطبيق حقيقي: استدعاء خدمة إرسال SMS
            // smsService.send(user.getPhoneNumber(), message);
            
        } catch (Exception e) {
            log.error("❌ فشل إرسال SMS: {}", e.getMessage());
        }
    }
    
    @Override
    public String getObserverType() {
        return "SMS";
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        log.info("تم {} إشعارات SMS", enabled ? "تفعيل" : "تعطيل");
    }
    
    private String generateSMSMessage(String eventType, User user, Account account, Object data) {
        StringBuilder message = new StringBuilder();
        
        switch (eventType) {
            case "MONEY_TRANSFER":
                message.append("SE3Bank: تم تحويل ");
                if (data instanceof String[]) {
                    String[] transferData = (String[]) data;
                    message.append(transferData[0]).append("USD");
                }
                message.append(". تحقق من حسابك.");
                break;
                
            case "WITHDRAWAL":
                message.append("SE3Bank: تم سحب ");
                if (data instanceof String[]) {
                    String[] withdrawalData = (String[]) data;
                    message.append(withdrawalData[0]).append("USD");
                }
                message.append(". رصيدك الآن ");
                if (account != null) {
                    message.append(account.getBalance()).append("USD");
                }
                break;
                
            case "LOW_BALANCE":
                message.append("SE3Bank: تحذير! رصيدك منخفض. ");
                if (account != null) {
                    message.append("رصيدك: ").append(account.getBalance()).append("USD");
                }
                break;
                
            case "SUSPICIOUS_ACTIVITY":
                message.append("SE3Bank: نشاط مشبوه على حسابك. اتصل بالدعم.");
                break;
                
            default:
                message.append("SE3Bank: لديك إشعار جديد. تحقق من تطبيق البنك.");
        }
        
        // تقصير الرسالة لتتناسب مع SMS
        if (message.length() > 160) {
            message = new StringBuilder(message.substring(0, 157) + "...");
        }
        
        return message.toString();
    }
}