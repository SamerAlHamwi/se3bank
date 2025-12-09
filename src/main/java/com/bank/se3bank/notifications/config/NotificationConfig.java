package com.bank.se3bank.notifications.config;

import com.bank.se3bank.notifications.observers.EmailNotifier;
import com.bank.se3bank.notifications.observers.InAppNotifier;
import com.bank.se3bank.notifications.observers.SMSNotifier;
import com.bank.se3bank.notifications.publisher.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * تكوين Observer Pattern
 * ربط المراقبين بالناشر عند بدء التطبيق
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class NotificationConfig {
    
    private final NotificationPublisher notificationPublisher;
    private final EmailNotifier emailNotifier;
    private final SMSNotifier smsNotifier;
    private final InAppNotifier inAppNotifier;
    
    @PostConstruct
    public void init() {
        log.info("🔔 تهيئة نظام الإشعارات (Observer Pattern)...");
        
        // إضافة جميع المراقبين للناشر
        notificationPublisher.subscribe(emailNotifier);
        notificationPublisher.subscribe(smsNotifier);
        notificationPublisher.subscribe(inAppNotifier);
        
        log.info("✅ تم تسجيل {} مراقب للإشعارات", 
                notificationPublisher.getObservers().size());
        
        // تفعيل/تعطيل قنوات معينة
        notificationPublisher.setObserverEnabled("SMS", false); // تعطيل SMS افتراضياً
        
        log.info("📢 قنوات الإشعارات المفعلة:");
        notificationPublisher.getEnabledObservers().forEach(observer -> 
                log.info("   - {}", observer.getObserverType()));
    }
}