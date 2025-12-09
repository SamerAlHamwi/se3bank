package com.bank.se3bank.notifications.observers;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.users.model.User;

/**
 * تطبيق Observer Pattern
 * واجهة المراقب للإشعارات
 */
public interface NotificationObserver {
    
    /**
     * تحديث المراقب بحدث جديد
     * @param eventType نوع الحدث
     * @param user المستخدم المعني
     * @param account الحساب المعني (قد يكون null)
     * @param data بيانات إضافية
     */
    void update(String eventType, User user, Account account, Object data);
    
    /**
     * نوع المراقب (EMAIL, SMS, IN_APP, PUSH)
     */
    String getObserverType();
    
    /**
     * هل المراقب مفعّل؟
     */
    boolean isEnabled();
    
    /**
     * تفعيل/تعطيل المراقب
     */
    void setEnabled(boolean enabled);
}