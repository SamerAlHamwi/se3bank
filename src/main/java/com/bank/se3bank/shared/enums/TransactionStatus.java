package com.bank.se3bank.shared.enums;

public enum TransactionStatus {
    PENDING("قيد الانتظار"),
    COMPLETED("مكتمل"),
    FAILED("فاشل"),
    CANCELLED("ملغي"),
    PENDING_APPROVAL("بانتظار الاعتماد");

    private final String arabicName;

    TransactionStatus(String arabicName) {
        this.arabicName = arabicName;
    }

    public String getArabicName() {
        return arabicName;
    }
    
    // ========== Helper Methods ==========
    
    /**
     * التحقق إذا كانت الحالة نهائية
     */
    public boolean isFinal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }
    
    /**
     * التحقق إذا كانت الحالة معلقة
     */
    public boolean isPending() {
        return this == PENDING || this == PENDING_APPROVAL;
    }
    
    /**
     * التحقق إذا كانت الحالة ناجحة
     */
    public boolean isSuccessful() {
        return this == COMPLETED;
    }
    
    /**
     * التحقق إذا كانت الحالة فاشلة
     */
    public boolean isFailed() {
        return this == FAILED || this == CANCELLED;
    }
    
    /**
     * الحصول على الحالات التالية المسموح بها
     */
    public TransactionStatus[] getAllowedTransitions() {
        return switch (this) {
            case PENDING -> new TransactionStatus[]{COMPLETED, FAILED, CANCELLED, PENDING_APPROVAL};
            case PENDING_APPROVAL -> new TransactionStatus[]{COMPLETED, FAILED, CANCELLED};
            case COMPLETED, FAILED, CANCELLED -> new TransactionStatus[]{};
        };
    }
    
    /**
     * التحقق من صحة الانتقال للحالة الجديدة
     */
    public boolean canTransitionTo(TransactionStatus newStatus) {
        for (TransactionStatus allowed : getAllowedTransitions()) {
            if (allowed == newStatus) {
                return true;
            }
        }
        return false;
    }
}