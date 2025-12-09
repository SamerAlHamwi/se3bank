package com.bank.se3bank.shared.exceptions;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(Double balance, Double amount) {
        super("الرصيد غير كافي. الرصيد الحالي: " + balance + ", المبلغ المطلوب: " + amount);
    }
}