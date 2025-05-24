package com.healthcare.management.exception;

public class NoAppointmentFoundException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	
	public NoAppointmentFoundException(String msg) {
		super(msg);
	}
}
