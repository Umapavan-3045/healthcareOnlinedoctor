package com.availabilitySchedule.exception;

/**
 * Exception thrown when there is a database error.
 * 
 * @author Swapnil Rajesh
 * @since 18/02/2025
 */
public class DatabaseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DatabaseException(String message, Throwable cause) {
		super(message, cause);
	}
}