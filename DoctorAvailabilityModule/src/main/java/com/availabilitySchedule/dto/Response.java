package com.availabilitySchedule.dto;

import org.springframework.http.HttpStatus;

/**
 * Generic Response class for API responses.
 * 
 * @param <T> the type of the response data
 * 
 * @author Swapnil Rajesh
 * @since 18/02/2025
 */
public class Response<T> {
	private Boolean success;
	private HttpStatus status;
	private T data;
	private String errorMessage = "";

	public Response(Boolean success, HttpStatus status, T data) {
		this.success = success;
		this.status = status;
		this.data = data;
		this.errorMessage = null;
	}

	public Response(Boolean success, HttpStatus status, String errorMessage) {
		this.success = success;
		this.status = status;
		this.data = null;
		this.errorMessage = errorMessage;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}