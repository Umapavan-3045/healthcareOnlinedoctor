package com.availabilitySchedule.exception;

/**
 * Exception thrown when a doctor is not found.
 * 
 * @author Swapnil Rajesh
 * @since 18/02/2025
 */

public class DoctorNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DoctorNotFoundException(String message) {
		super(message);
	}
}
