package com.bank.se3bank.shared.enums;

public enum Role {
    ROLE_CUSTOMER("عميل"),
    ROLE_TELLER("محصل"),
    ROLE_MANAGER("مدير"),
    ROLE_ADMIN("مدير النظام");

    private final String arabicName;

    Role(String arabicName) {
        this.arabicName = arabicName;
    }

    public String getArabicName() {
        return arabicName;
    }
}