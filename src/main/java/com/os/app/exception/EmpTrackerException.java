package com.os.app.exception;

import org.springframework.http.HttpStatus;

import com.os.app.beans.ErrorResponse;
import com.os.app.enums.ErrorKeys;

public class EmpTrackerException extends RuntimeException {

	private static final long serialVersionUID = 5260411617349191291L;
	private HttpStatus statusCode;
	private ErrorResponse errorResponse;

	public EmpTrackerException(ErrorKeys errorKeys) {
		this(errorKeys, errorKeys.name());
	}

	public EmpTrackerException(String message, HttpStatus status) {
		super(message);
		errorResponse = new ErrorResponse(null, null, message);
		this.statusCode = status;
	}

	public EmpTrackerException(ErrorResponse errorResponse, HttpStatus status) {
		super(errorResponse.getMessage() == null ? null : errorResponse.getMessage().toString());
		this.statusCode = status;
		this.errorResponse = errorResponse;
	}

	public EmpTrackerException(ErrorKeys errorKeys, String message) {
		super(message);
		errorResponse = new ErrorResponse(getMessage(), null, null);
		this.statusCode = errorKeys.getStatus();

	}

	public HttpStatus getStatusCode() {
		return statusCode;
	}

	public ErrorResponse getErrorResponse() {
		return errorResponse;
	}

	public void setErrorResponse(ErrorResponse errorResponse) {
		this.errorResponse = errorResponse;
	}
}
