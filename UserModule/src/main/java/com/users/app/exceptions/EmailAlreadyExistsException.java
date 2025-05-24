package com.users.app.exceptions;

public class EmailAlreadyExistsException extends RuntimeException{
	
	public EmailAlreadyExistsException(String message){
		 super(message);
	 }
}
