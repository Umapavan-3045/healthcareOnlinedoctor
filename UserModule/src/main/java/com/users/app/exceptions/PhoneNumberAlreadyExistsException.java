package com.users.app.exceptions;

public class PhoneNumberAlreadyExistsException extends RuntimeException{

	public PhoneNumberAlreadyExistsException(String message){
		super(message);
	}
	
}
