package com.bank.se3bank.shared.enums;

public enum AccountType {
    SAVINGS("حساب توفير"),
    CHECKING("حساب جاري"),
    LOAN("حساب قرض"),
    INVESTMENT("حساب استثمار"),
    BUSINESS("حساب تجاري");

    private final String arabicName;

    AccountType(String arabicName) {
        this.arabicName = arabicName;
    }

    public String getArabicName() {
        return arabicName;
    }
}