package com.os.app.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.os.app.dto.TripDTO;
import com.os.app.editor.SqlTimestampPropertyEditor;
import com.os.app.entity.Company;
import com.os.app.entity.SystemUser;
import com.os.app.entity.Trip;
import com.os.app.service.TripService;
import com.os.app.validator.TripValidator;

@RestController
@RequestMapping(CommonController.API)
public class TripController extends CommonController {

	private static final String TRIP = "trip";
	private static final String TRIPS = TRIP + "s";
	@Autowired
	TripService tripService;

	@Autowired
	TripValidator tripValidator;

	@PreAuthorize("hasAnyAuthority('COMPANYADMIN','STAFF')")
	@RequestMapping(value = { TRIP, TRIPS }, method = RequestMethod.GET)
	public ResponseEntity<?> trips(@RequestParam(required = false, defaultValue = "false") boolean mine,
			@RequestParam(required = false, defaultValue = "false") boolean onlyEndedTrips,
			@RequestParam(required = false, defaultValue = "false") boolean onlyOpenTrips,

			@RequestParam(required = false) Date fromStartDate, @RequestParam(required = false) Date toStartDate,
			@RequestParam(required = false) Date fromEndDate, @RequestParam(required = false) Date toEndDate,
			@RequestParam(value = "employees", required = false) List<Long> employees,
			@RequestParam(value = "tripTypes", required = false) List<Long> tripTypes,
			@RequestParam(value = "jobTypes", required = false) List<Long> jobTypes,
			@RequestParam(required = false) Integer pageIndex, @RequestParam(required = false) Integer records) {

		SystemUser loggedInUser = getCurrentUser();

		return new ResponseEntity<Map<String, Object>>(createResponse("trips",
				tripService.findTrips(loggedInUser.getCompany().getId(), loggedInUser.getId(), mine, onlyEndedTrips,
						onlyOpenTrips, fromStartDate, toStartDate, fromEndDate, toEndDate, employees, tripTypes,
						jobTypes, pageIndex, records)),
				HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('COMPANYADMIN','STAFF')")
	@RequestMapping(value = { TRIP, TRIPS }, method = RequestMethod.POST)
	public ResponseEntity<?> createTrip(@Valid @RequestBody Map<String, TripDTO> tripObj, Errors errors) {
		TripDTO tripDTO = tripObj.get(TRIP);
		if (tripDTO == null) {
			throw new EntityNotFoundException();
		}
		SystemUser loggedInUser = getCurrentUser();
		Company loggedInUserCompany = loggedInUser.getCompany();
		if (loggedInUserCompany == null) {
			throw new AccessDeniedException(messageService.getMessage("error.accessdenied"));
		}

		Trip trip = tripDTO.toTrip();
		trip.setCompanyId(loggedInUserCompany.getId());
		trip.setId(null);
		tripValidator.validate(trip, errors);
		if (errors.hasErrors()) {
			return errorMessages(errors);
		}

		return new ResponseEntity<Map<String, Object>>(createResponse(TRIP, tripService.save(trip, loggedInUser)),
				HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('COMPANYADMIN','STAFF')")
	@RequestMapping(value = { TRIP + "/{id}", TRIPS + "/{id}" }, method = RequestMethod.PUT)
	public ResponseEntity<?> updateTrip(@PathVariable Long id, @Valid @RequestBody HashMap<String, TripDTO> tripObj,
			Errors errors) {
		TripDTO tripDTO = tripObj.get(TRIP);
		if (tripDTO == null) {
			throw new EntityNotFoundException();
		}
		SystemUser loggedInUser = getCurrentUser();
		Company loggedInUserCompany = loggedInUser.getCompany();
		if (loggedInUserCompany == null) {
			throw new AccessDeniedException(messageService.getMessage("error.accessdenied"));
		}

		Trip trip = tripDTO.toTrip();
		trip.setCompanyId(loggedInUserCompany.getId());
		trip.setId(id);
		tripValidator.validate(trip, errors);
		if (errors.hasErrors()) {
			return errorMessages(errors);
		}

		return new ResponseEntity<Map<String, Object>>(createResponse(TRIP, tripService.update(trip, loggedInUser)),
				HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('SYSTEMADMIN','COMPANYADMIN','STAFF')")
	@RequestMapping(value = { TRIP + "/{id}", TRIPS + "/{id}" }, method = RequestMethod.GET)
	public ResponseEntity<?> getTrip(@PathVariable long id) {

		TripDTO tripDTO = new TripDTO(tripService.findTripById(id));
		return new ResponseEntity<Map<String, Object>>(createResponse(TRIP, tripDTO), HttpStatus.OK);

	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new SqlTimestampPropertyEditor());
	}
}