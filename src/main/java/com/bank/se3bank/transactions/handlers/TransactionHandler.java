package com.bank.se3bank.transactions.handlers;

import com.bank.se3bank.transactions.model.Transaction;

/**
 * تطبيق Chain of Responsibility Pattern
 * واجهة معالج المعاملات في سلسلة الاعتماد
 */
public abstract class TransactionHandler {
    
    protected TransactionHandler nextHandler;
    protected String handlerName;
    
    public TransactionHandler(String handlerName) {
        this.handlerName = handlerName;
    }
    
    /**
     * تعيين المعالج التالي في السلسلة
     */
    public void setNextHandler(TransactionHandler handler) {
        this.nextHandler = handler;
    }
    
    /**
     * معالجة المعاملة
     */
    public abstract boolean handle(Transaction transaction);
    
    /**
     * تمرير المعاملة للمعالج التالي
     */
    protected boolean passToNext(Transaction transaction) {
        if (nextHandler != null) {
            return nextHandler.handle(transaction);
        }
        return true; // وصلنا لنهاية السلسلة بنجاح
    }
    
    /**
     * تسجيل في سلسلة الاعتماد
     */
    protected void logApproval(Transaction transaction, String message) {
        transaction.addToApprovalChainLog(handlerName, message);
    }
    
    public String getHandlerName() {
        return handlerName;
    }
}