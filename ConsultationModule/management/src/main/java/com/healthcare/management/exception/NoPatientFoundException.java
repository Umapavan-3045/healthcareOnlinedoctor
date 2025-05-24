package com.healthcare.management.exception;

public class NoPatientFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public NoPatientFoundException(String msg) {
		super(msg);
	}
}
