package com.availabilitySchedule.exception;

/**
 * Exception thrown when availability is not found.
 * 
 * @author Swapnil Rajesh
 * @since 18/02/2025
 */
public class AvailabilityNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AvailabilityNotFoundException(String message) {
		super(message);
	}
}