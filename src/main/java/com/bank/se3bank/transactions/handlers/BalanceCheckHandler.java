package com.bank.se3bank.transactions.handlers;

import com.bank.se3bank.shared.enums.TransactionType;
import com.bank.se3bank.transactions.model.Transaction;
import lombok.extern.slf4j.Slf4j;

/**
 * معالج التحقق من الرصيد
 * أول معالج في السلسلة
 */
@Slf4j
public class BalanceCheckHandler extends TransactionHandler {
    
    public BalanceCheckHandler() {
        super("BalanceCheckHandler");
    }
    
    @Override
    public boolean handle(Transaction transaction) {
        log.info("🔍 {} يتحقق من رصيد المعاملة {}", 
                handlerName, transaction.getTransactionId());
        
        // فقط المعاملات الصادرة تحتاج للتحقق من الرصيد
        if (transaction.getFromAccount() != null && 
            transaction.getTransactionType() != TransactionType.DEPOSIT) {
            
            if (!transaction.getFromAccount().canWithdraw(transaction.getAmount())) {
                String message = String.format("رصيد غير كافي. الرصيد الحالي: %.2f, المبلغ المطلوب: %.2f",
                        transaction.getFromAccount().getBalance(),
                        transaction.getAmount());
                
                logApproval(transaction, message);
                transaction.markAsFailed("رصيد غير كافي");
                log.error("❌ {}: {}", handlerName, message);
                return false;
            }
        }
        
        logApproval(transaction, "تم التحقق من الرصيد بنجاح");
        log.info("✅ {}: تم التحقق من الرصيد", handlerName);
        
        return passToNext(transaction);
    }
}