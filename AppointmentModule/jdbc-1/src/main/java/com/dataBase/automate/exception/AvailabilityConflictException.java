package com.dataBase.automate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
**
* Exception when Availability is in conflict.
* 
* @Author Sanjay R
* @Since 2025-03-18
*/
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AvailabilityConflictException extends RuntimeException {
	public AvailabilityConflictException(String msg) {
		super(msg);
	}

}
