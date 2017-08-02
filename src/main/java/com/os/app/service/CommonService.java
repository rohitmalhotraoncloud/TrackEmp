package com.os.app.service;

import java.util.function.Supplier;

import javax.persistence.EntityNotFoundException;
import javax.validation.Validation;
import javax.validation.Validator;

import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.os.app.entity.Company;
import com.os.app.entity.SystemUser;
import com.os.app.enums.Role;

import ch.qos.logback.classic.Logger;

public abstract class CommonService {

	protected final Logger logger = (Logger) LoggerFactory.getLogger(getClass());

	private final static Logger _logger = (Logger) LoggerFactory.getLogger(CommonService.class);

	protected static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	public static boolean isCurrentUserSysAdmin() {
		return getCurrentUser().getRole() == Role.SYSTEMADMIN;
	}

	public static boolean isCurrentUserAManagerOrCompanyAdmin() {
		return isCompanyAdminOrManager(getCurrentUser().getRole());
	}

	public static boolean isCurrentUserCompanyAdmin() {
		return isCompanyAdmin(getCurrentUser().getRole());
	}

	protected static boolean isCurrentUserManager() {
		return isManager(getCurrentUser().getRole());
	}

	/**
	 * Method returns true if roles object contains STAFF role
	 */
	protected static boolean isStaff(Role role) {
		return Role.STAFF == role;
	}

	/**
	 * Methods returns true if roles parameter contains COMPANYADMIN
	 */
	public static boolean isCompanyAdmin(Role role) {
		return Role.COMPANYADMIN == role;
	}

	public static boolean isCompanyAdminOrManager(Role role) {
		return isCompanyAdmin(role) || isManager(role);
	}

	public static boolean isManager(Role role) {
		return Role.MANAGER == role;
	}

	public static SystemUser getCurrentUser() {
		Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
		return (SystemUser) authenticationToken.getDetails();
	}

	public static Company getCurrentCompany() {
		return getCurrentUser().getCompany();
	}

	public static boolean isCurrentUserAStaff() {
		return isStaff(getCurrentUser().getRole());
	}

	protected static boolean isNotASysAdmin() {
		return getCurrentUser().getRole() != Role.SYSTEMADMIN;
	}

	protected static <R> R iterateSafely(Supplier<R> supplier, String message) {
		try {
			return supplier.get();
		} catch (Exception exception) {
			_logger.error(message, exception);
		}
		return null;
	}

	protected static void throwIfNull(Object t) {
		if (null == t) {
			throw new EntityNotFoundException();
		}
	}

}
