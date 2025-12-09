package com.bank.se3bank.transactions.handlers;

import com.bank.se3bank.shared.enums.TransactionStatus;
import com.bank.se3bank.transactions.model.Transaction;
import lombok.extern.slf4j.Slf4j;

/**
 * معالج الاعتماد التلقائي للمعاملات الصغيرة
 */
@Slf4j
public class AutoApprovalHandler extends TransactionHandler {
    
    private final Double autoApproveLimit;
    
    public AutoApprovalHandler(Double autoApproveLimit) {
        super("AutoApprovalHandler");
        this.autoApproveLimit = autoApproveLimit;
    }
    
    @Override
    public boolean handle(Transaction transaction) {
        log.info("🤖 {} يعالج المعاملة {}", handlerName, transaction.getTransactionId());
        
        // إذا كانت المعاملة معلقة اعتماد
        if (transaction.requiresApproval()) {
            // تحقق إذا كانت ضمن الحد المسموح للاعتماد التلقائي
            if (transaction.getAmount() <= autoApproveLimit) {
                transaction.setStatus(TransactionStatus.COMPLETED);
                transaction.setApprovedBy(0L); // 0 يعني نظام تلقائي
                logApproval(transaction, 
                        String.format("تم الاعتماد تلقائياً (المبلغ %.2f <= %.2f)", 
                                transaction.getAmount(), autoApproveLimit));
                log.info("✅ {}: تم الاعتماد التلقائي للمعاملة", handlerName);
                return true;
            } else {
                logApproval(transaction, 
                        String.format("تتطلب اعتماد مدير (المبلغ %.2f > %.2f)", 
                                transaction.getAmount(), autoApproveLimit));
                log.info("⏳ {}: تتطلب اعتماد مدير", handlerName);
                return true; // ما زالت معلقة اعتماد مدير
            }
        }
        
        // إذا كانت المعاملة صغيرة، اعتمدها تلقائياً
        if (transaction.getAmount() <= autoApproveLimit && 
            transaction.getStatus() == TransactionStatus.PENDING) {
            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction.setApprovedBy(0L);
            logApproval(transaction, "معاملة صغيرة - تم الاعتماد تلقائياً");
            log.info("✅ {}: تم الاعتماد التلقائي", handlerName);
            return true;
        }
        
        logApproval(transaction, "تنتقل للمعالج التالي");
        return passToNext(transaction);
    }
}