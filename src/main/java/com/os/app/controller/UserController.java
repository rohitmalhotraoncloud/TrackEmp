package com.os.app.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.os.app.authentication.TokenAuthenticationService;
import com.os.app.authentication.UserAuthentication;
import com.os.app.dto.EmployeeDTO;
import com.os.app.entity.SystemUser;
import com.os.app.service.CompanyService;
import com.os.app.service.EmployeeService;
import com.os.app.service.SystemUserService;
import com.os.app.utils.AWSUtil;
import com.os.app.utils.Constants;
import com.os.app.validator.EmployeeValidator;
import com.os.app.validator.SystemUserUserNameValidator;

@RestController
@RequestMapping("/api")
public class UserController extends CommonController {
	@Autowired
	SystemUserService systemUserService;

	@Autowired
	EmployeeService employeeService;

	@Autowired
	EmployeeValidator employeeValidator;
	@Autowired
	CompanyService companyService;

	@Autowired
	SystemUserUserNameValidator systemUserUserNameValidator;

	@Autowired
	TokenAuthenticationService tokenAuthenticationService;

	@Autowired
	AWSUtil awsUtil;

	@RequestMapping(value = { "/register" }, method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> register(@RequestBody SystemUser user, Errors errors) throws IOException {
		user.setUsername(user.getEmail());
		user.setSubdomain(Constants.SYSTEMADMIN_DOMAIN);
		// systemUserPasswordValidator.validate(user, errors);
		// systemUserUserNameValidator.validate(user, errors);
		if (errors.hasErrors()) {
			return errorMessages(errors);
		}
		return new ResponseEntity<SystemUser>(systemUserService.registerAUser(user), HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('SYSTEMADMIN')")
	@RequestMapping(value = { "/register/company-admin" }, method = RequestMethod.POST)
	public ResponseEntity<?> createAdmin(@Valid @RequestBody SystemUser admin, Errors errors) {
		admin.setUsername(admin.getEmail());
		systemUserUserNameValidator.validate(admin, errors);
		if (errors.hasErrors()) {
			return errorMessages(errors);
		}
		return new ResponseEntity<SystemUser>(employeeService.createCompanyAdmin(admin), HttpStatus.OK);
	}

	/*
	 * Get All Employees Of A Company 1. This API is only accessible by
	 * COMPANYADMINs of a company 2. If SYSTEMADMIN wants to retrieve ALL users
	 * of a system, use /users api
	 */
	@PreAuthorize("hasAnyAuthority('COMPANYADMIN','MANAGER')")
	@RequestMapping(value = { "/employee{s*}" }, method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getSystemUserForCompany() {
		return new ResponseEntity<Map<String, Object>>(
				createResponse("employees", employeeService.getEmployees(getCurrentUser().getCompany().getId())),
				HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('COMPANYADMIN','MANAGER','STAFF')")
	@RequestMapping(value = { "/employee{s*}" }, method = RequestMethod.GET, params = { "availableForTrip" })
	public ResponseEntity<Map<String, Object>> getEmployeesAvailableForTrip(@RequestParam boolean availableForTrip) {
		if (availableForTrip) {
			return new ResponseEntity<Map<String, Object>>(
					createResponse("employees",
							employeeService
									.findEmployeeAvailableForTripByCompany(getCurrentUser().getCompany().getId())),
					HttpStatus.OK);
		} else {
			throw new AccessDeniedException(messageService.getMessage("error.accessdenied"));
		}
	}

	@PreAuthorize("hasAnyAuthority('COMPANYADMIN','MANAGER','SYSTEMADMIN','STAFF')")
	@RequestMapping(value = { "/employee/{id}", "/employees/{id}" }, method = RequestMethod.GET)
	public ResponseEntity<?> getSystemUserForCompany(@PathVariable Long id) {

		return new ResponseEntity<Map<String, Object>>(
				createResponse("employee", employeeService.findEmployeeById(getCurrentUser(), id)), HttpStatus.OK);

	}

	@PreAuthorize("hasAnyAuthority('COMPANYADMIN','SYSTEMADMIN')")
	@RequestMapping(value = { "/users" }, method = RequestMethod.GET)
	public ResponseEntity<List<SystemUser>> users() {
		return new ResponseEntity<List<SystemUser>>((systemUserService.filter(null)), HttpStatus.OK);
	}

	@RequestMapping(value = { "/employee{s*}" }, method = RequestMethod.GET, params = { "currentUser" })
	public ResponseEntity<Map<String, Object>> currentUser(
			@RequestParam(required = true, value = "currentUser") boolean currentUser) {
		if (currentUser) {
			return new ResponseEntity<Map<String, Object>>(
					createResponse("employee", new EmployeeDTO(getCurrentUser(), awsUtil.getCurrentUrl())),
					HttpStatus.OK);
		} else {
			throw new AccessDeniedException(messageService.getMessage("error.accessdenied"));
		}
	}

	@PreAuthorize("hasAnyAuthority('COMPANYADMIN','MANAGER','STAFF')")
	@RequestMapping(value = { "/profilePhoto{s*}" }, method = RequestMethod.POST, params = { "employee" })
	public ResponseEntity<?> updateEmployee(@RequestParam Long employee,
			@RequestBody HashMap<String, String> imageKey) {
		return new ResponseEntity<Map<String, Object>>(
				createResponse("employee", employeeService.updateEmployeePic(employee, imageKey.get("imageKey"))),
				HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('COMPANYADMIN')")
	@RequestMapping(value = { "/employee{s*}/{id}" }, method = RequestMethod.PUT)
	public ResponseEntity<?> updateEmployee(@PathVariable Long id,
			@Valid @RequestBody HashMap<String, EmployeeDTO> employee, Errors errors) {
		if (employee == null || employee.get("employee") == null) {
			throw new AccessDeniedException(messageService.getMessage("error.accessdenied"));
		}

		EmployeeDTO emp = employee.get("employee");
		emp.setCompany(getCurrentUser().getCompany().getId());
		emp.setId(id);

		employeeValidator.validate(emp, errors);
		systemUserUserNameValidator.validate(emp.toSystemUser(), errors);
		if (errors.hasErrors()) {
			return errorMessages(errors);
		}
		return new ResponseEntity<Map<String, Object>>(
				createResponse("employee", employeeService.updateEmployee(id, emp)), HttpStatus.OK);

	}

	@PreAuthorize("hasAnyAuthority('COMPANYADMIN')")
	@RequestMapping(value = { "/employee{s*}" }, method = RequestMethod.POST)
	public ResponseEntity<?> createEmployee(@Valid @RequestBody HashMap<String, EmployeeDTO> employee, Errors errors) {
		if (employee == null || employee.get("employee") == null) {
			throw new AccessDeniedException(messageService.getMessage("error.accessdenied"));
		}

		EmployeeDTO emp = employee.get("employee");
		emp.setCompany(getCurrentUser().getCompany().getId());
		employeeValidator.validate(emp, errors);
		SystemUser employeeobj = emp.toSystemUser();
		systemUserUserNameValidator.validate(employeeobj, errors);
		if (errors.hasErrors()) {
			return errorMessages(errors);
		}
		return new ResponseEntity<Map<String, Object>>(createResponse("employee", employeeService.createEmployee(emp)),
				HttpStatus.OK);
	}

	@RequestMapping(value = { "/employee/{id}/profilePhoto" }, method = RequestMethod.GET, produces = "image/jpg")
	public byte[] getLocationUpdateImage(@RequestParam("token") String token, @PathVariable Long id) {
		SystemUser user = tokenAuthenticationService.getUserFromToken(token);
		UserAuthentication userAuthentication = new UserAuthentication(user);
		SecurityContextHolder.getContext().setAuthentication(userAuthentication);
		return employeeService.retrieveImage(id);
	}

}