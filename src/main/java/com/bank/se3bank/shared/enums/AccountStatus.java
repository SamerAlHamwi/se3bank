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
}