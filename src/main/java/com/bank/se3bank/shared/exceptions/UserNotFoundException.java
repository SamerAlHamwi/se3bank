package com.bank.se3bank.shared.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String userNumber) {
        super("المستخدم غير موجود: " + userNumber);
    }
    
    public UserNotFoundException(Long userId) {
        super("المستخدم غير موجود بالمعرف: " + userId);
    }
}