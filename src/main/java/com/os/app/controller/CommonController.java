package com.os.app.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import com.os.app.beans.ErrorResponse;
import com.os.app.entity.SystemUser;
import com.os.app.enums.ErrorKeys;
import com.os.app.service.MessageByLocaleService;
import com.os.app.service.SystemUserService;

import ch.qos.logback.classic.Logger;

public class CommonController {

	protected final static String API = "/api";

	protected Logger logger = (Logger) LoggerFactory.getLogger(getClass());

	@Autowired
	SystemUserService systemUserService;

	@Autowired
	MessageByLocaleService messageService;

	public ResponseEntity<ErrorResponse> errorMessages(ErrorKeys errorKeys, Errors errors) {
		List<String> errormessages = new ArrayList<String>();
		for (ObjectError error : errors.getAllErrors()) {
			errormessages.add(error.getDefaultMessage());
		}

		return new ResponseEntity<ErrorResponse>(
				new ErrorResponse(errorKeys.name(), messageService.getMessage("error.validation"), errormessages),
				errorKeys.getStatus());
	}

	public ResponseEntity<ErrorResponse> errorMessages(Errors errors) {
		return errorMessages(ErrorKeys.VALIDATION, errors);
	}

	public static <X> Map<String, X> createResponse(String key, X obj) {
		Map<String, X> response = new HashMap<String, X>();
		response.put(key, obj);
		return response;
	}

	public SystemUser getCurrentUser() {
		SystemUser user = (SystemUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		return systemUserService.findUserByUsernameAndSubDomain(user.getUsername(), user.getSubdomain());
	}
}