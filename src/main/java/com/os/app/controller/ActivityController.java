package com.os.app.controller;

import static org.springframework.util.StringUtils.hasText;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.os.app.dto.ActivityDTO;
import com.os.app.entity.Activity;
import com.os.app.service.ActivityService;
import com.os.app.validator.ActivityValidator;
import com.os.app.views.BasicView;

@RestController
@RequestMapping(CommonController.API + ActivityController.ACTIVITY_S)
public class ActivityController extends CommonController {

	protected static final String ACTIVITY_S = "/activity{s*}";
	protected static final String ACTIVITY = "activity";

	@Autowired
	ActivityService activityService;

	@Autowired
	ActivityValidator activityValidator;

	@PreAuthorize("hasAnyAuthority('COMPANYADMIN','MANAGER','STAFF')")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> createActivity(@Valid @RequestBody HashMap<String, ActivityDTO> activityObj,
			Errors errors) {
		ActivityDTO activityDTo = activityObj.get(ACTIVITY);
		Activity activity = activityDTo.toActivity();
		activity.setCompanyId(getCurrentUser().getCompany().getId());
		activityValidator.validate(activity, errors);
		if (errors.hasErrors()) {
			return errorMessages(errors);
		}
		return new ResponseEntity<Map<String, Object>>(createResponse(ACTIVITY, activityService.saveActivity(activity)),
				HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('COMPANYADMIN','MANAGER','STAFF')")
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.PUT)
	public ResponseEntity<?> updateActivity(@PathVariable Long id,
			@Valid @RequestBody HashMap<String, ActivityDTO> activityObj, Errors errors) {
		ActivityDTO activityDTo = activityObj.get(ACTIVITY);
		Activity activity = activityDTo.toActivity();
		activity.setCompanyId(getCurrentUser().getCompany().getId());
		return new ResponseEntity<Map<String, Object>>(
				createResponse(ACTIVITY, activityService.updateActivity(id, activity)), HttpStatus.OK);
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.GET)
	public ResponseEntity<?> getActivity(@PathVariable Long id) {

		return new ResponseEntity<Map<String, Object>>(createResponse(ACTIVITY, activityService.findActivity(id)),
				HttpStatus.OK);
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.GET, params = { "departLocationUpdate" })
	public ResponseEntity<?> getActivityByDepartLocationUpdate(@RequestParam Long departLocationUpdate) {

		return new ResponseEntity<Map<String, Object>>(
				createResponse(ACTIVITY, activityService.findActivityByDepartLocationUpdate(departLocationUpdate)),
				HttpStatus.OK);
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.GET, params = { "arriveLocationUpdate" })
	public ResponseEntity<?> getActivityByArriveLocationUpdate(@RequestParam Long arriveLocationUpdate) {
		logger.info("Getting the Activity with the arriveLocationUpdate : " + arriveLocationUpdate);

		return new ResponseEntity<Map<String, Object>>(
				createResponse(ACTIVITY, activityService.findActivityByArriveLocationUpdate(arriveLocationUpdate)),
				HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('COMPANYADMIN','MANAGER','STAFF')")
	@RequestMapping(method = RequestMethod.GET)
	@JsonView(BasicView.ActivityByIdView.class)
	public ResponseEntity<Map<String, Object>> getStaffCurrentActivity(@RequestParam(required = false) String current) {
		if (hasText(current)) {
			return new ResponseEntity<Map<String, Object>>(
					createResponse(ACTIVITY, activityService.getStaffCurrentActivity()), HttpStatus.OK);
		}
		return new ResponseEntity<Map<String, Object>>(createResponse(ACTIVITY, activityService.getActivity()),
				HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('COMPANYADMIN','MANAGER','STAFF')")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void deleteActivityById(@PathVariable Long id) {
		this.activityService.deleteById(id);
	}

	@RequestMapping(value = "/{id}/setGeofenceOn")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void setGeoFenceOn(@PathVariable long id) {
		this.activityService.setGetfenceOn(id);
	}

	@RequestMapping(value = "/{id}/setGeofenceOff")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void setGeofenceOff(@PathVariable long id) {
		this.activityService.setGeofenceOff(id);
	}

}