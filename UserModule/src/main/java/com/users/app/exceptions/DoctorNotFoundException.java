package com.users.app.exceptions;

public class DoctorNotFoundException  extends RuntimeException{
	public DoctorNotFoundException(String message){
		 super(message);
	 }
}
