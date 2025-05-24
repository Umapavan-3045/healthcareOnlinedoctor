package com.users.app.exceptions;

public class PasswordMismatchException extends RuntimeException{
	
	public PasswordMismatchException(String message) {
		super(message);
	}
}
