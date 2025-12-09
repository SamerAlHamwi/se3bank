package com.bank.se3bank.transactions.handlers;

import com.bank.se3bank.shared.enums.TransactionType;
import com.bank.se3bank.transactions.model.Transaction;
import lombok.extern.slf4j.Slf4j;

/**
 * معجار مكافحة غسيل الأموال (AML)
 */
@Slf4j
public class AMLComplianceHandler extends TransactionHandler {
    
    public AMLComplianceHandler() {
        super("AMLComplianceHandler");
    }
    
    @Override
    public boolean handle(Transaction transaction) {
        log.info("⚖️ {} يتحقق من امتثال المعاملة {} لقوانين مكافحة غسيل الأموال", 
                handlerName, transaction.getTransactionId());
        
        // 1. التحقق من المبالغ التي تتجاوز الحد
        if (transaction.getAmount() > 50000.0) {
            logApproval(transaction, "مبلغ يتجاوز 50,000 - تتطلب تسجيل خاص");
            log.info("📋 {}: مبلغ كبير يتطلب تسجيل AML", handlerName);
        }
        
        // 2. التحقق من المعاملات المشبوهة (مبالغ دقيقة)
        if (isSuspiciousAmount(transaction.getAmount())) {
            logApproval(transaction, "مبلغ مشبوه (دقيق جداً)");
            log.warn("⚠️ {}: مبلغ مشبوه - " + transaction.getAmount(), handlerName);
        }
        
        // 3. التحقق من التحويلات الدولية (محاكاة)
        if (transaction.getDescription() != null && 
            transaction.getDescription().toLowerCase().contains("international")) {
            logApproval(transaction, "تحويل دولي - تتطلب تحقق إضافي");
            log.info("🌍 {}: تحويل دولي", handlerName);
        }
        
        logApproval(transaction, "تم التحقق من الامتثال لـ AML");
        log.info("✅ {}: متوافق مع قوانين مكافحة غسيل الأموال", handlerName);
        
        return passToNext(transaction);
    }
    
    private boolean isSuspiciousAmount(Double amount) {
        // المبالغ الدقيقة جداً مثل 9999.99 قد تكون مشبوهة
        String amountStr = String.format("%.2f", amount);
        return amountStr.endsWith(".99") || amountStr.endsWith(".00");
    }
}