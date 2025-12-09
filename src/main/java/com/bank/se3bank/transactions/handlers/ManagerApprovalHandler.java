package com.bank.se3bank.transactions.handlers;

import com.bank.se3bank.shared.enums.TransactionStatus;
import com.bank.se3bank.transactions.model.Transaction;
import lombok.extern.slf4j.Slf4j;

/**
 * معالج اعتماد المدير
 */
@Slf4j
public class ManagerApprovalHandler extends TransactionHandler {
    
    public ManagerApprovalHandler() {
        super("ManagerApprovalHandler");
    }
    
    @Override
    public boolean handle(Transaction transaction) {
        log.info("👔 {} يعالج المعاملة {}", handlerName, transaction.getTransactionId());
        
        // إذا كانت المعاملة تتطلب اعتماد مدير
        if (transaction.requiresApproval()) {
            logApproval(transaction, "بانتظار اعتماد المدير");
            log.info("⏳ {}: بانتظار اعتماد المدير", handlerName);
            
            // في تطبيق حقيقي، هنا سننتظر اعتماد المدير
            // لكن للاختبار، سنعتمدها تلقائياً بعد فترة
            
            return true; // ما زالت معلقة
        }
        
        // إذا كانت المعاملة كبيرة ولكن لم يتم وضعها كمعلقة
        if (transaction.getAmount() > 10000.0 && 
            transaction.getStatus() == TransactionStatus.PENDING) {
            transaction.markAsPendingApproval();
            logApproval(transaction, "معاملة كبيرة - وضعت بانتظار اعتماد المدير");
            log.info("⚠️ {}: معاملة كبيرة - تنتظر اعتماد مدير", handlerName);
            return true;
        }
        
        logApproval(transaction, "تمت المعالجة");
        transaction.markAsCompleted();
        log.info("✅ {}: تمت المعالجة بنجاح", handlerName);
        return true;
    }
    
    /**
     * اعتماد معاملة بواسطة المدير
     */
    public boolean approveTransaction(Transaction transaction, Long managerId) {
        if (transaction.requiresApproval()) {
            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction.setApprovedBy(managerId);
            transaction.markAsCompleted();
            logApproval(transaction, 
                    String.format("تم الاعتماد بواسطة المدير #%d", managerId));
            log.info("✅ {}: تم الاعتماد بواسطة المدير {}", handlerName, managerId);
            return true;
        }
        return false;
    }
    
    /**
     * رفض معاملة بواسطة المدير
     */
    public boolean rejectTransaction(Transaction transaction, Long managerId, String reason) {
        if (transaction.requiresApproval()) {
            transaction.markAsFailed("مرفوض بواسطة المدير: " + reason);
            transaction.setApprovedBy(managerId);
            logApproval(transaction, 
                    String.format("مرفوض بواسطة المدير #%d: %s", managerId, reason));
            log.info("❌ {}: مرفوض بواسطة المدير {}", handlerName, managerId);
            return true;
        }
        return false;
    }
}