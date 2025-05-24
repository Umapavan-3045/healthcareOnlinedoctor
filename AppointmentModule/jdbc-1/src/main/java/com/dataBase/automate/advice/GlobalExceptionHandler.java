package com.dataBase.automate.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.dataBase.automate.dto.Response;
import com.dataBase.automate.exception.AppointmentNotFoundException;
import com.dataBase.automate.exception.AvailabilityConflictException;
import com.dataBase.automate.exception.AvailabilityNotFoundException;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(Logger.class);
    @ExceptionHandler(AppointmentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Response<?>> handleAppointmentNotFoundException(AppointmentNotFoundException ex) {
        log.error("Exception: {}", ex.getMessage());
        Response<?> response = new Response<>(false, HttpStatus.NOT_FOUND, null, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AvailabilityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Response<?>> handleAvailabilityNotFoundException(AvailabilityNotFoundException ex) {
        log.error("Exception: {}", ex.getMessage());
        Response<?> response = new Response<>(false, HttpStatus.NOT_FOUND, null, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(AvailabilityConflictException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Response<?>> handleAvailabilityConflictException(AvailabilityConflictException ex) {
        log.error("Exception: {}", ex.getMessage());
        Response<?> response = new Response<>(false, HttpStatus.NOT_FOUND, null, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
   
}