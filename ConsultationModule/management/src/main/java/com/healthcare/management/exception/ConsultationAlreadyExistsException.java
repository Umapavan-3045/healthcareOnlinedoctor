package com.healthcare.management.exception;

public class ConsultationAlreadyExistsException extends RuntimeException {
    public ConsultationAlreadyExistsException(String message) {
        super(message);
    }
}