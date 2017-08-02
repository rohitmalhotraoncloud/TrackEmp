package com.os.app.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.os.app.beans.ErrorResponse;
import com.os.app.enums.ErrorKeys;
import com.os.app.exception.EmpTrackerException;

import ch.qos.logback.classic.Logger;

@ControllerAdvice
@ResponseStatus(HttpStatus.OK)
public class RestErrorHandlerController {

	private static Logger logger = (Logger) LoggerFactory.getLogger(RestErrorHandlerController.class);

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorResponse> catchHttpRequestMethodNotSupportedException(
			HttpRequestMethodNotSupportedException e) {
		return new ResponseEntity<ErrorResponse>(new ErrorResponse(ErrorKeys.NOT_IMPLEMENTED.name()),
				HttpStatus.NOT_IMPLEMENTED);
	}

	@ExceptionHandler(HttpMessageConversionException.class)
	public ResponseEntity<ErrorResponse> catchHttpMessageConversionException(
			HttpMessageConversionException httpMessageNotReadableException, HttpServletRequest request) {
		logger.warn("Got the error " + httpMessageNotReadableException.getMessage() + " while processing request "
				+ request.getRequestURI(), httpMessageNotReadableException);
		return new ResponseEntity<ErrorResponse>(new ErrorResponse(ErrorKeys.UNPROCESSABLE_ENTITY.name()),
				HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(ServletRequestBindingException.class)
	public ResponseEntity<ErrorResponse> catchServletRequestBindingException() {
		return new ResponseEntity<ErrorResponse>(new ErrorResponse(ErrorKeys.MISSING_PARAMETERS.name()),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> catchDataIntegrityViolationException(DataIntegrityViolationException ex,
			HttpServletRequest request) {
		logger.error("Obtained the error while processing the request " + request.getRequestURI(), ex);
		if (ex.getCause() instanceof ConstraintViolationException) {
			ConstraintViolationException exceptionDetail = (ConstraintViolationException) ex.getCause();
			return new ResponseEntity<ErrorResponse>(
					new ErrorResponse(ErrorKeys.DUPLICATE_RECORD_EXIST.name(), exceptionDetail.getConstraintName()),
					HttpStatus.UNPROCESSABLE_ENTITY);
		}
		return new ResponseEntity<ErrorResponse>(new ErrorResponse(ErrorKeys.INVALID_ARGUMENT.name(), null),
				HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> catchEntityNotFoundException(HttpServletRequest request,
			EntityNotFoundException ex) {
		logger.error("Obtained the error while processing the request " + request.getRequestURI());
		return new ResponseEntity<ErrorResponse>(
				new ErrorResponse(ErrorKeys.ENTITY_NOT_FOUND.name(), ex.getMessage(), null), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> catchMethodArgumentNotValidException(
			MethodArgumentNotValidException methodArgumentNotValidException, HttpServletRequest request) {
		logger.warn("Obtained the error while processing the request " + request.getRequestURI(),
				methodArgumentNotValidException);
		Map<String, List<String>> errors = new HashMap<String, List<String>>();
		for (FieldError error : methodArgumentNotValidException.getBindingResult().getFieldErrors()) {
			List<String> messages = errors.get(error.getDefaultMessage());
			if (null == messages) {
				messages = new ArrayList<>();
			}
			messages.add(error.getField());
			errors.put(error.getDefaultMessage(), messages);
		}
		return new ResponseEntity<ErrorResponse>(new ErrorResponse(ErrorKeys.MISSING_PARAMETERS.name(), errors),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex,
			HttpServletRequest request) {
		return new ResponseEntity<ErrorResponse>(new ErrorResponse(ErrorKeys.ACCESS_DENIED.name(), ex.getMessage()),
				HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleAnyException(Exception ex, HttpServletRequest request) {
		logger.error("Error handling the request " + request.getRequestURI(), ex);
		return new ResponseEntity<ErrorResponse>(new ErrorResponse(ErrorKeys.SERVER_ERROR.name()),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
	public void handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException ex,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		logger.error("Error handling the request " + request.getRequestURI(), ex);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		String strResponse = "{\"auth_error\":\"@response\"}";
		response.getWriter().write(strResponse.replace("@response", ex.getMessage()));
	}

	@ExceptionHandler(EmpTrackerException.class)
	public ResponseEntity<ErrorResponse> handleCoreTradesysException(EmpTrackerException ex,
			HttpServletRequest request) {
		logger.error("Error handling the request " + request.getRequestURI(), ex);
		return new ResponseEntity<ErrorResponse>(ex.getErrorResponse(), ex.getStatusCode());
	}
}