package com.os.app.validator;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.os.app.beans.ErrorResponse;
import com.os.app.entity.Trip;
import com.os.app.enums.ErrorKeys;
import com.os.app.exception.EmpTrackerException;
import com.os.app.service.CompanyService;
import com.os.app.service.EmployeeService;
import com.os.app.service.MessageByLocaleService;
import com.os.app.service.SystemUserService;

/**
 * Validate object when creating/updating Trip
 */
@Component
public class TripValidator implements Validator {

	@Autowired
	SystemUserService systemUserService;

	@Autowired
	CompanyService companyService;

	@Autowired
	EmployeeService employeeService;

	@Autowired
	MessageByLocaleService messageService;

	@Override
	public boolean supports(Class<?> clazz) {
		return Trip.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Trip trip = (Trip) target;
		Date expectedDateTime = trip.getExpectedEndTime();

		if (expectedDateTime == null) {
			errors.reject(null, messageService.getMessage("trip.expectedtimerequired"));
		}

		if (errors.hasErrors()) {
			return;
		}

		if (trip.getMembers() != null && !trip.getMembers().isEmpty()) {
			List<Long> invalidMembers = employeeService.findEmployeeIdsNotBelongToCompany(trip.getCompanyId(),
					trip.getMembersId());
			if (invalidMembers != null && !invalidMembers.isEmpty()) {
				errors.reject(null,
						String.format(messageService.getMessage("trip.membersoutsidecompany"), invalidMembers));
			}
			List<Long> allIds = new LinkedList<Long>(Arrays.asList(trip.getMembersId()));
			allIds.removeAll(invalidMembers);

			if (!allIds.isEmpty()) {

				List<Long> membersNoValidForTrip = employeeService
						.findEmployeeNotAvailableForTripByCompanyMembersAndTrip(trip.getCompanyId(),
								allIds.toArray(new Long[allIds.size()]), trip.getId());
				if (membersNoValidForTrip != null && !membersNoValidForTrip.isEmpty()) {
					throw new EmpTrackerException(
							new ErrorResponse("MEMBER_HAS_EXISTING_TRIP",
									messageService.getMessage("error.failedvalidation"), membersNoValidForTrip),
							ErrorKeys.VALIDATION.getStatus());
				}
			}
		}

	}
}
