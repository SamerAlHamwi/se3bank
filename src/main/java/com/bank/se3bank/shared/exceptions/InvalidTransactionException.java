package com.bank.se3bank.shared.exceptions;

public class InvalidTransactionException extends RuntimeException {
    public InvalidTransactionException(String message) {
        super("معاملة غير صالحة: " + message);
    }
}