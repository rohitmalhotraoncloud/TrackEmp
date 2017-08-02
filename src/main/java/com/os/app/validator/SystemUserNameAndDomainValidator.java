package com.os.app.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.os.app.entity.SystemUser;
import com.os.app.service.MessageByLocaleService;

@Component
public class SystemUserNameAndDomainValidator implements Validator {

	@Autowired
	MessageByLocaleService messageService;

	@Override
	public boolean supports(Class<?> clazz) {
		return SystemUser.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		SystemUser user = (SystemUser) target;
		if (StringUtils.isBlank(user.getUsername())) {
			errors.reject(null, messageService.getMessage("notblank.email.username"));
		}

		if (StringUtils.isBlank(user.getSubdomain())) {
			errors.reject(null, messageService.getMessage("notBlank.company.subDomainName"));
			return;
		}

	}
}