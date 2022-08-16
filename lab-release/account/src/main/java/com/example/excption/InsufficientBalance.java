package com.example.excption;

public class InsufficientBalance extends RuntimeException {
    public InsufficientBalance(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
