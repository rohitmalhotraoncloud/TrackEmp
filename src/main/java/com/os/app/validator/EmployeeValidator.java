package com.os.app.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.os.app.dto.EmployeeDTO;
import com.os.app.service.CompanyService;
import com.os.app.service.MessageByLocaleService;

@Component
public class EmployeeValidator implements Validator {

	@Autowired
	CompanyService companyService;

	@Autowired
	MessageByLocaleService messageService;

	@Override
	public boolean supports(Class<?> clazz) {
		return EmployeeDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		EmployeeDTO employee = (EmployeeDTO) target;
		/*
		 * Checking if employee object has value for jobType property
		 */
		boolean isJobTypeExistsInRequest = !StringUtils.isEmpty(employee.getJobType());
		/*
		 * If id is null that means it is new object. All validation in this if
		 * block are just for new object not for update purpose
		 */
		if (employee.getId() == null) {
			/*
			 * Invalidate object if it does not contains value for role property
			 */
			if (StringUtils.isEmpty(employee.getRole())) {
				errors.reject(null, messageService.getMessage("updateemployee.role.not.found"));
			}

			/*
			 * Invalidate object if it does not contains value for jobType
			 * property
			 */
			if (!isJobTypeExistsInRequest) {
				errors.reject(null, messageService.getMessage("updateemployee.jobtype.not.found"));
			}

			if (errors.hasErrors()) {
				return;
			}
		}
	}
}