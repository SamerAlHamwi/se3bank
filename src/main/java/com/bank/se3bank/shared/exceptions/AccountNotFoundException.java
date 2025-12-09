package com.bank.se3bank.shared.exceptions;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String accountNumber) {
        super("الحساب غير موجود: " + accountNumber);
    }
    
    public AccountNotFoundException(Long accountId) {
        super("الحساب غير موجود بالمعرف: " + accountId);
    }
}