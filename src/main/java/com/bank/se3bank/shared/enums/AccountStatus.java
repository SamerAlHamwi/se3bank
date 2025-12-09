package com.bank.se3bank.shared.enums;

public enum AccountStatus {
    ACTIVE("نشط"),
    FROZEN("مجمد"),
    SUSPENDED("موقوف"),
    CLOSED("مغلق"),
    PENDING("قيد الانتظار");

    private final String arabicName;

    AccountStatus(String arabicName) {
        this.arabicName = arabicName;
    }

    public String getArabicName() {
        return arabicName;
    }
    
    // ========== Helper Methods ==========
    
    /**
     * التحقق إذا كانت الحالة نشطة
     */
    public boolean isActive() {
        return this == ACTIVE;
    }
    
    /**
     * التحقق إذا كانت الحالة غير نشطة
     */
    public boolean isInactive() {
        return this != ACTIVE;
    }
    
    /**
     * التحقق إذا كانت الحالة تسمح بالعمليات
     */
    public boolean allowsOperations() {
        return this == ACTIVE || this == PENDING;
    }
    
    /**
     * التحقق إذا كانت الحالة تمنع العمليات
     */
    public boolean blocksOperations() {
        return this == FROZEN || this == SUSPENDED || this == CLOSED;
    }
    
    /**
     * التحقق إذا كانت الحالة نهائية (لا يمكن تغييرها)
     */
    public boolean isTerminal() {
        return this == CLOSED;
    }
    
    /**
     * الحصول على الحالة التالية المسموح بها
     */
    public AccountStatus[] getAllowedTransitions() {
        return switch (this) {
            case ACTIVE -> new AccountStatus[]{FROZEN, SUSPENDED, CLOSED};
            case FROZEN -> new AccountStatus[]{ACTIVE, CLOSED};
            case SUSPENDED -> new AccountStatus[]{ACTIVE, CLOSED};
            case PENDING -> new AccountStatus[]{ACTIVE, CLOSED};
            case CLOSED -> new AccountStatus[]{};
        };
    }
    
    /**
     * التحقق من صحة الانتقال للحالة الجديدة
     */
    public boolean canTransitionTo(AccountStatus newStatus) {
        for (AccountStatus allowed : getAllowedTransitions()) {
            if (allowed == newStatus) {
                return true;
            }
        }
        return false;
    }
}