package com.healthcare.management.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * GlobalExceptionHandler is a global exception handler that handles exceptions thrown by any part of the application.
 * It provides methods to handle specific exceptions and return appropriate HTTP status codes and messages.
 * 
 * @ControllerAdvice - Indicates that this class provides global exception handling.
 */

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(NoConsultationDetailsFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleNoConsultationDetailsFoundException(NoConsultationDetailsFoundException ex) {
		return ex.getMessage();
	}
	
	
	@ExceptionHandler(ConsultationAlreadyExistsException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String ConsultationAlreadyExistsException(ConsultationAlreadyExistsException ex) {
		return ex.getMessage();
	}
	
	@ExceptionHandler(NoHistoryFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleNoHistoryFoundException(NoHistoryFoundException ex) {
		return ex.getMessage();
	}
	
	@ExceptionHandler(NoAppointmentFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleNoAppointmentFoundException(NoAppointmentFoundException ex) {
		return ex.getMessage();
	}
	

	@ExceptionHandler(NoPatientFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleNoPatientFoundException(NoPatientFoundException ex) {
		return ex.getMessage();
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
	
	
}
