package com.os.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

import com.os.app.beans.ErrorResponse;

public class UserSubscriptionExpireException extends AuthenticationException {

	private static final long serialVersionUID = 5260411617349191291L;
	private HttpStatus statusCode;
	private ErrorResponse errorResponse;

	public UserSubscriptionExpireException(ErrorResponse errorResponse, HttpStatus status) {
		super(errorResponse.getMessage() == null ? null : errorResponse.getMessage().toString());
		this.statusCode = status;
		this.errorResponse = errorResponse;
	}
	
	public HttpStatus getStatusCode() {
		return statusCode;
	}



	public ErrorResponse getErrorResponse() {
		return errorResponse;
	}



	@Override
	public String toString() {
		return "{\"errorKey\":\"" + errorResponse.getErrorKey() + "\", \"data\":" + errorResponse.getData()
				+ ", \"message\":\"" + errorResponse.getMessage() + "\"}";
	}
}
