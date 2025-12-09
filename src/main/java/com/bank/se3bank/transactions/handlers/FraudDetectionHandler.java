package com.bank.se3bank.transactions.handlers;

import com.bank.se3bank.shared.enums.TransactionStatus;
import com.bank.se3bank.transactions.model.Transaction;
import com.bank.se3bank.transactions.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * معالج اكتشاف الاحتيال
 */
@Component
@Slf4j
public class FraudDetectionHandler extends TransactionHandler {
    
    private final TransactionRepository transactionRepository;
    
    public FraudDetectionHandler(TransactionRepository transactionRepository) {
        super("FraudDetectionHandler");
        this.transactionRepository = transactionRepository;
    }
    
    @Override
    public boolean handle(Transaction transaction) {
        log.info("🕵️ {} يفحص المعاملة {} لاكتشاف الاحتيال", 
                handlerName, transaction.getTransactionId());
        
        // 1. التحقق من عدد المعاملات الكبير في فترة قصيرة
        if (isHighFrequencyTransaction(transaction)) {
            logApproval(transaction, "تم اكتشاف عدد كبير من المعاملات في فترة قصيرة");
            transaction.markAsPendingApproval();
            log.warn("⚠️ {}: تتطلب اعتماداً إضافياً (تردد عالي)", handlerName);
            return true; // لا نرفض، بل نطلب اعتماد
        }
        
        // 2. التحقق من المبالغ الكبيرة
        if (isLargeAmountTransaction(transaction)) {
            logApproval(transaction, "معاملة بمبلغ كبير تتطلب اعتماداً إضافياً");
            transaction.markAsPendingApproval();
            log.warn("⚠️ {}: تتطلب اعتماداً إضافياً (مبلغ كبير)", handlerName);
            return true;
        }
        
        // 3. التحقق من المعاملات في أوقات غير اعتيادية
        if (isUnusualTimeTransaction(transaction)) {
            logApproval(transaction, "معاملة في وقت غير اعتيادي");
            log.info("⚠️ {}: معاملة في وقت غير اعتيادي", handlerName);
        }
        
        logApproval(transaction, "لا توجد مؤشرات احتيال");
        log.info("✅ {}: لا توجد مؤشرات احتيال", handlerName);
        
        return passToNext(transaction);
    }
    
    private boolean isHighFrequencyTransaction(Transaction transaction) {
        if (transaction.getFromAccount() == null) return false;
        
        Long count = transactionRepository.countCompletedTransactionsSince(
                transaction.getFromAccount().getId(),
                TransactionStatus.COMPLETED,
                LocalDateTime.now().minusHours(1)
        );
        
        return count != null && count >= 10; // أكثر من 10 معاملات في ساعة
    }
    
    private boolean isLargeAmountTransaction(Transaction transaction) {
        return transaction.getAmount() > 10000.0; // أكثر من 10,000
    }
    
    private boolean isUnusualTimeTransaction(Transaction transaction) {
        int hour = LocalDateTime.now().getHour();
        return hour < 6 || hour > 22; // بين 10 مساءً و6 صباحاً
    }
}