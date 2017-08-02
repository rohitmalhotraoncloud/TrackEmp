package com.os.app.enums;

import org.springframework.http.HttpStatus;

public enum ErrorKeys {

	SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
	//
	MISSING_PARAMETERS(HttpStatus.BAD_REQUEST),
	//
	INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED),
	//
	EMAIL_ALREADY_EXIST(HttpStatus.UNPROCESSABLE_ENTITY),
	//
	DUPLICATE_RECORD_EXIST(HttpStatus.UNPROCESSABLE_ENTITY),
	//
	UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY),
	//
	RECORD_DOES_NOT_EXIST(HttpStatus.NOT_FOUND),
	//
	ACCESS_DENIED(HttpStatus.UNAUTHORIZED),
	//
	TOKEN_INVALID_OR_EXPIRED(HttpStatus.UNAUTHORIZED),
	//
	TOKEN_MISSING(HttpStatus.UNAUTHORIZED),
	//
	ERROR_READING_FILE(HttpStatus.UNPROCESSABLE_ENTITY),
	//
	ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND),
	//
	REQUEST_ABORTED(HttpStatus.GONE),
	// In case of any validation errors
	VALIDATION(HttpStatus.UNPROCESSABLE_ENTITY),
	// bad request
	INVALID_ARGUMENT(HttpStatus.BAD_REQUEST),
	//
	IMAGE_NOT_AVAILABLE(HttpStatus.BAD_REQUEST),
	//
	INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST),
	//
	INVALID_IMAGE(HttpStatus.BAD_REQUEST),
	//
	SUBCRIPTION_LIMIT_EXCEEDED(HttpStatus.FORBIDDEN),
	//
	SUBSCRIPTION_EXISTS(HttpStatus.FORBIDDEN),
	//
	ACCOUNT_DISABLED(HttpStatus.BAD_REQUEST),
	//
	MULTIPLE_RECORDS_EXIST(HttpStatus.BAD_REQUEST),
	//
	DUPLICATE_TYPENAME_EXISTS_BUT_IS_DELETED(HttpStatus.BAD_REQUEST),
	//
	DUPLICATE_TYPENAME_EXISTS(HttpStatus.BAD_REQUEST),
	//
	ONLY_STAFF_USERS_HAVE_STATS(HttpStatus.UNAUTHORIZED),
	// Thrown in case the entity already exists
	ENTITY_ALREADY_EXISTS(HttpStatus.CONFLICT),
	// File not found
	FILE_NOT_FOUND(HttpStatus.NOT_FOUND),
	// Error while the mail sending failed
	ERROR_SENDING_MAIL(HttpStatus.INTERNAL_SERVER_ERROR),
	// Error while deleting older files
	ERROR_DELETING_FILE(HttpStatus.INTERNAL_SERVER_ERROR),

	/**
	 * Thrown in case the specific method is not implemented
	 */
	NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED),
	/**
	 * Timesheet is locked
	 */
	TIMESHEET_LOCKED(HttpStatus.LOCKED),
	/**
	 * No timesheet settings available though the flag was set
	 */
	NO_TIMESHEET_SETTINGS_AVAILABLE(HttpStatus.UNPROCESSABLE_ENTITY),
	/**
	 * Timesheet calculation should not happen today
	 */
	START_DATE_NOT_TODAY(HttpStatus.FAILED_DEPENDENCY),
	/**
	 * the user is not a timesheet user
	 */
	NOT_TIMESHEET_USER(HttpStatus.FAILED_DEPENDENCY),
	/**
	 * Timesheet is supposed to start later
	 */
	TIMESHEET_PERIOD_NOT_YET_STARTED(HttpStatus.FAILED_DEPENDENCY),
	/**
	 * Incase the timesheet generation is not yet done
	 */
	PERIOD_TYPE_NOT_IMPLEMENTED(HttpStatus.FAILED_DEPENDENCY),
	/**
	 * This mean that there was no checkin operation prior
	 */
	NOT_CHECKED_IN(HttpStatus.FAILED_DEPENDENCY);

	ErrorKeys(HttpStatus httpStatus) {
		this.status = httpStatus;
	}

	private HttpStatus status;

	public HttpStatus getStatus() {
		return status;
	}

}