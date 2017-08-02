package com.os.app.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.os.app.entity.Activity;
import com.os.app.service.MessageByLocaleService;

@Component
public class ActivityValidator implements Validator {

	@Autowired
	MessageByLocaleService messageService;

	@Override
	public boolean supports(Class<?> clazz) {
		return Activity.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Activity activity = (Activity) target;

		if (StringUtils.isBlank(activity.getMetaData())) {
			errors.reject(null, messageService.getMessage("notblank.activity.metadata"));
		}

	}
}