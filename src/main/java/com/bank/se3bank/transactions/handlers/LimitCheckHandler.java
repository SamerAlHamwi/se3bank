package com.bank.se3bank.transactions.handlers;

import com.bank.se3bank.accounts.model.SavingsAccount;
import com.bank.se3bank.shared.enums.TransactionType;
import com.bank.se3bank.transactions.model.Transaction;
import lombok.extern.slf4j.Slf4j;

/**
 * معالج التحقق من الحدود
 */
@Slf4j
public class LimitCheckHandler extends TransactionHandler {
    
    public LimitCheckHandler() {
        super("LimitCheckHandler");
    }
    
    @Override
    public boolean handle(Transaction transaction) {
        log.info("📊 {} يتحقق من حدود المعاملة {}", 
                handlerName, transaction.getTransactionId());
        
        // التحقق من حسابات التوفير وحدود السحب الشهرية
        if (transaction.getFromAccount() instanceof SavingsAccount savingsAccount) {
            if (transaction.getTransactionType() == TransactionType.WITHDRAWAL) {
                if (!savingsAccount.canWithdrawThisMonth()) {
                    String message = String.format("تم تجاوز حد السحب الشهري (%d عملية)", 
                            savingsAccount.getMonthlyWithdrawalLimit());
                    
                    logApproval(transaction, message);
                    transaction.markAsFailed("تجاوز حد السحب الشهري");
                    log.error("❌ {}: {}", handlerName, message);
                    return false;
                }
            }
        }
        
        // التحقق من الحد اليومي (محاكاة)
        if (transaction.getAmount() > 5000.0 && 
            transaction.getTransactionType() == TransactionType.WITHDRAWAL) {
            logApproval(transaction, "تجاوز الحد اليومي للسحب");
            log.info("⚠️ {}: تجاوز الحد اليومي للسحب", handlerName);
        }
        
        logApproval(transaction, "تم التحقق من الحدود بنجاح");
        log.info("✅ {}: ضمن الحدود المسموحة", handlerName);
        
        return passToNext(transaction);
    }
}