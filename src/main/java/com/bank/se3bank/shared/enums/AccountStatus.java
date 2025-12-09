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
}