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
}