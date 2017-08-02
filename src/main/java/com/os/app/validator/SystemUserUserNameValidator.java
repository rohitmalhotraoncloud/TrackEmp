package com.os.app.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.os.app.entity.Company;
import com.os.app.entity.SystemUser;
import com.os.app.service.CompanyService;
import com.os.app.service.MessageByLocaleService;
import com.os.app.service.SystemUserService;

/**
 * Most used validator to Validate SustemUser while creating/updating
 * operations. Main job of this validator is to ensure that username, email,
 * subdomain properties are valid or not
 */
@Component
public class SystemUserUserNameValidator implements Validator {

	@Autowired
	SystemUserService systemUserService;

	@Autowired
	CompanyService companyService;

	@Autowired
	MessageByLocaleService messageService;

	private Pattern pattern;
	private Matcher matcher;

	@Override
	public boolean supports(Class<?> clazz) {
		return SystemUser.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		SystemUser user = (SystemUser) target;
		String subDomain = user.getSubdomain();

		if (user.getCompany() != null) {
			Company company = companyService.findById(user.getCompany()
					.getId());
			if (company != null) {
				subDomain = company.getSubDomainName();
			}
		}

		SystemUser systemUserByName = systemUserService
				.findUserByUsernameAndSubDomain(user.getUsername(), subDomain);
		SystemUser systemUserByEmail = systemUserService
				.getUserByEmailAndSubDomain(user.getEmail(), subDomain);

		/*
		 * Code inside if block is valid when creating new SystemUser. This code
		 * is not used when updating an existing user
		 */
		if (user.getId() == null) {
			if (StringUtils.isEmpty(subDomain)) {
				errors.reject(null, messageService.getMessage("subdomain.error"));
			}

			if (user.getEmail() == null) {
				errors.reject(null, messageService.getMessage("notblank.systemuser.email"));
				return;
			}
		}
		if (user.getEmail() != null) {
			pattern = Pattern.compile(messageService.getMessage("email.pattern"));
			matcher = pattern.matcher(user.getEmail());
			if (!matcher.matches()) {
				errors.reject(null, messageService.getMessage("invalid.email.format"));
				return;
			}
		}

		boolean isValidUser = isValid(systemUserByName, user);
		boolean isValidEmail = isValid(systemUserByEmail, user);

		if (!isValidUser) {
			errors.reject(null, messageService.getMessage("user.already.exists"));
			return;
		}

		if (!isValidEmail) {
			errors.reject(null, messageService.getMessage("email.already.exists"));
			return;
		}
	}

	private boolean isValid(SystemUser userFromDb, SystemUser newUser) {
		if (userFromDb != null) {
			if (newUser.getId() == null) {
				return false;
			} else if (!userFromDb.getId().equals(newUser.getId())) {
				return false;
			}
		}
		return true;
	}
}