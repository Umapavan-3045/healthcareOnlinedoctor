package com.healthcare.management.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoConsultationDetailsFoundException extends RuntimeException{
	
	private static final Logger log = LoggerFactory.getLogger(Logger.class);
	
	private static final long serialVersionUID = 1L;
	
	public NoConsultationDetailsFoundException(String msg) {
		super(msg);
		log.debug("No consultation details found..");
	}
}
