package com.bank.se3bank.shared.enums;

public enum TransactionType {
    DEPOSIT("إيداع"),
    WITHDRAWAL("سحب"),
    TRANSFER("تحويل"),
    PAYMENT("دفع"),
    INTEREST("فائدة"),
    FEE("رسوم");

    private final String arabicName;

    TransactionType(String arabicName) {
        this.arabicName = arabicName;
    }

    public String getArabicName() {
        return arabicName;
    }
}