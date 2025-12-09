package com.bank.se3bank.notifications.publisher;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.notifications.observers.NotificationObserver;
import com.bank.se3bank.users.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * تطبيق Observer Pattern
 * الناشر الذي يدير المراقبين ويرسل الإشعارات لهم
 */
@Component
@Slf4j
public class NotificationPublisher {
    
    private final List<NotificationObserver> observers = new ArrayList<>();
    
    /**
     * إضافة مراقب جديد
     */
    public void subscribe(NotificationObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            log.info("✅ تم إضافة مراقب: {}", observer.getObserverType());
        }
    }
    
    /**
     * إزالة مراقب
     */
    public void unsubscribe(NotificationObserver observer) {
        observers.remove(observer);
        log.info("🗑️ تم إزالة مراقب: {}", observer.getObserverType());
    }
    
    /**
     * إرسال إشعار لجميع المراقبين
     */
    public void notifyObservers(String eventType, User user, Account account, Object data) {
        log.info("🔔 إرسال إشعار {} للمستخدم {}", eventType, user.getUsername());
        
        for (NotificationObserver observer : observers) {
            if (observer.isEnabled()) {
                try {
                    observer.update(eventType, user, account, data);
                } catch (Exception e) {
                    log.error("❌ فشل إرسال إشعار عبر {}: {}", 
                            observer.getObserverType(), e.getMessage());
                }
            }
        }
    }
    
    /**
     * إرسال إشعار لمراقب معين فقط
     */
    public void notifyObserver(String observerType, String eventType, 
                              User user, Account account, Object data) {
        for (NotificationObserver observer : observers) {
            if (observer.getObserverType().equals(observerType) && observer.isEnabled()) {
                observer.update(eventType, user, account, data);
                break;
            }
        }
    }
    
    /**
     * تفعيل/تعطيل نوع معين من المراقبين
     */
    public void setObserverEnabled(String observerType, boolean enabled) {
        for (NotificationObserver observer : observers) {
            if (observer.getObserverType().equals(observerType)) {
                observer.setEnabled(enabled);
                break;
            }
        }
    }
    
    /**
     * الحصول على جميع المراقبين
     */
    public List<NotificationObserver> getObservers() {
        return new ArrayList<>(observers);
    }
    
    /**
     * الحصول على المراقبين المفعلين فقط
     */
    public List<NotificationObserver> getEnabledObservers() {
        return observers.stream()
                .filter(NotificationObserver::isEnabled)
                .toList();
    }
    
    /**
     * التحقق إذا كان نوع معين من المراقبين مفعلاً
     */
    public boolean isObserverEnabled(String observerType) {
        return observers.stream()
                .filter(o -> o.getObserverType().equals(observerType))
                .findFirst()
                .map(NotificationObserver::isEnabled)
                .orElse(false);
    }
}