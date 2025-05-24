package com.dataBase.automate.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AvailabilityNotFoundException extends RuntimeException {
    public AvailabilityNotFoundException(String message) {
        super(message);
    }
}
