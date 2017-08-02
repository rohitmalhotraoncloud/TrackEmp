package com.os.app.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.os.app.beans.ErrorResponse;
import com.os.app.dto.TripDTO;
import com.os.app.entity.Company;
import com.os.app.entity.SystemUser;
import com.os.app.entity.Trip;
import com.os.app.enums.ErrorKeys;
import com.os.app.exception.EmpTrackerException;
import com.os.app.repository.TripRepository;
import com.os.app.utils.Constants;
import com.os.app.utils.Utils;

@Service
public class TripService extends GenericService<Trip, Long> {

	@Autowired
	TripRepository tripRepository;

	@Autowired
	CompanyService companyService;

	@Autowired
	EmployeeService employeeService;

	@Autowired
	SystemUserService systerUserService;

	/**
	 * Method to returns a trip based on TripId and companyId. Trip Entity does
	 * not contain company reference. We use Trip's Creator's company to get
	 * trip associated with company
	 */
	@Override
	public Trip findById(Long id) {
		Trip trip = null;
		if (isCurrentUserSysAdmin()) {
			trip = super.findById(id);
		} else if (isCurrentUserAManagerOrCompanyAdmin()) {
			trip = tripRepository.findTrip(id, getCurrentCompany().getId());
		} else {
			trip = tripRepository.findTripByIdAndCreator(id, getCurrentUser());
		}
		if (null == trip) {
			throw new EntityNotFoundException();
		}
		return trip;
	}

	/**
	 * Method to save trip record into database. Parameter trip is an object
	 * that we are going to save. loggedInUser is user who is accessing record
	 */
	public TripDTO save(Trip trip, SystemUser loggedInUser) {
		/*
		 * Checking whether trip has valid creator or not
		 */
		validateCreator(trip);

		/*
		 * Saving trip and trip's locationUpdate records
		 */
		trip = tripRepository.save(trip);
		return new TripDTO(trip);
	}

	/**
	 * A TripCreator is a SystemUser who manages trip. In this method, We
	 * validate TripCreator. Parameter trip is Trip class object which contains
	 * creator property. and loggedInUser is a user who is accessing this
	 * method. Role to validate creator are: (1) A STAFF cannot set a creator.
	 * If he is creating this record that means he is the creator, not anyone
	 * else.(2) A COMPANYADMIN had to specify creator reference. If he is
	 * creating this record that means he has to specify who is the creator of
	 * this trip
	 */
	private void validateCreator(Trip trip) {

		if (isCurrentUserAStaff()) {
			if (trip.getCreator() != null) {
				/*
				 * If LoggedInuser is a STAFF and he specify creator property of
				 * object, then it throws an exception
				 */
				throw new AccessDeniedException(messageService.getMessage("invalid.trip.creator"));
			} else {
				trip.setCreator(getCurrentUser());
			}
		} else if (trip.getCreator() == null) {
			/*
			 * If LoggedInuser is not a STAFF and he does not specify creator
			 * property of object, then it throws an exception
			 */
			throw new AccessDeniedException(messageService.getMessage("trip.creator.not.specified"));
		} else {
			if (systerUserService.findByCompanyIdAndId(trip.getCompanyId(), trip.getCreator().getId()) == null) {
				/*
				 * If creator is not associated with same company, it throws an
				 * exception
				 */
				throw new AccessDeniedException(messageService.getMessage("trip.creatoroutsidecompany"));
			}
		}

		if (!employeeService.findEmployeeNotAvailableForTripByCompanyMembersAndTrip(trip.getCompanyId(),
				new Long[] { trip.getCreator().getId() }, trip.getId()).isEmpty()) {
			throw new EmpTrackerException(new ErrorResponse("CREATOR_HAS_EXISTING_TRIP",
					messageService.getMessage("error.failedvalidation"), null), ErrorKeys.VALIDATION.getStatus());
		}

	}

	/**
	 * Method to update trip record into database. Parameter trip is an object
	 * that we are going to update. loggedInUser is user who is accessing record
	 */
	public TripDTO update(Trip trip, SystemUser loggedInUser) {
		Trip tripEntity = tripRepository.findOne(trip.getId());
		if (tripEntity == null) {
			/*
			 * Throws an exception if record with specific id does not exists
			 */
			throw new EntityNotFoundException();
		}
		if (isCurrentUserAStaff()) {
			if (!(tripEntity.getCreator().equals(loggedInUser)
					|| Arrays.asList(tripEntity.getMembersId()).contains(loggedInUser.getId()))) {
				throw new AccessDeniedException(messageService.getMessage("trip.update.staff.restriction"));
			}
		} else if (isCurrentUserAManagerOrCompanyAdmin()) {
			if (!(tripEntity.getCreator().getCompany().equals(loggedInUser.getCompany())
					|| Arrays.asList(tripEntity.getMembersId()).contains(loggedInUser.getId()))) {
				throw new AccessDeniedException(messageService.getMessage("trip.update.companyadmin.restriction"));
			}
		}

		if (trip.getEndTime() != null) {
			tripEntity.setEndTime(trip.getEndTime());
		}
		if (trip.getExpectedEndTime() != null) {
			tripEntity.setExpectedEndTime(trip.getExpectedEndTime());
		}
		/*
		 * Removing and recreating trip members
		 */
		if (trip.getMembers() != null) {
			tripEntity.getMembers().clear();
			tripEntity.getMembers().addAll(trip.getMembers());
		}

		if (tripEntity.getStartTime() != null) {
			tripEntity.setStartTime(trip.getStartTime());
		}

		return new TripDTO(tripRepository.save(tripEntity));
	}

	/**
	 * This method returns trip based on these filter parameters. (1)
	 * CompanyId-->Trips those creator belongs to specific company (2)
	 * mine-->Trips where loggedInUser (user, who is accessing this method) is a
	 * teammember of trips or creator of trips (3) onlyEndedTrips-->Trips those
	 * endTime is less then current time (4) onlyOpenTrips-->Trips those endTime
	 * is either null or endTime is more than current time
	 */
	public List<TripDTO> findTrips(Long companyId, Long userId, boolean mine, boolean onlyEndedTrips,
			boolean onlyOpenTrips, Date fromStartDate, Date toStartDate, Date fromEndDate, Date toEndDate,
			List<Long> employees, List<Long> tripTypes, List<Long> jobTypes, Integer pageIndex, Integer records) {
		Boolean hasEmployees = null;
		Boolean hasTripTypes = null;
		Boolean hasJobTypes = null;
		if (pageIndex == null) {
			pageIndex = 1;
		}

		PageRequest pageRequest = null;
		if (records != null) {
			pageRequest = new PageRequest(pageIndex - 1, records);
		}
		if (employees != null) {
			hasEmployees = true;
		}
		if (tripTypes != null) {
			hasTripTypes = true;
		}

		if (jobTypes != null) {
			hasJobTypes = true;
		}

		Company company = companyService.findById(companyId);
		String companyTimeZone = company.getTimezone();
		if (companyTimeZone != null) {
			if (fromStartDate != null) {
				fromStartDate = Utils.changeTimeZone(fromStartDate, TimeZone.getTimeZone(companyTimeZone),
						TimeZone.getTimeZone(Constants.SERVER_TIMEZONE));
			}
			if (toStartDate != null) {
				toStartDate = Utils.changeTimeZone(toStartDate, TimeZone.getTimeZone(companyTimeZone),
						TimeZone.getTimeZone(Constants.SERVER_TIMEZONE));
			}
			if (fromEndDate != null) {
				fromEndDate = Utils.changeTimeZone(fromEndDate, TimeZone.getTimeZone(companyTimeZone),
						TimeZone.getTimeZone(Constants.SERVER_TIMEZONE));
			}
			if (toEndDate != null) {
				toEndDate = Utils.changeTimeZone(toEndDate, TimeZone.getTimeZone(companyTimeZone),
						TimeZone.getTimeZone(Constants.SERVER_TIMEZONE));
			}
		}

		List<Trip> trips = tripRepository.findTrips(companyId, userId, mine, onlyEndedTrips, onlyOpenTrips,
				fromStartDate, toStartDate, fromEndDate, toEndDate, hasEmployees, hasTripTypes, hasJobTypes, employees,
				tripTypes, jobTypes, pageRequest);
		List<TripDTO> tripsDto = new ArrayList<TripDTO>();
		for (Trip trip : trips) {
			tripsDto.add(new TripDTO(trip));
		}
		return tripsDto;
	}

	/**
	 * Returns trip based on id. Here loggedInUser is user who is accessing this
	 * method.
	 */

	public Trip findTripById(long id) {
		Trip trip;
		if (isCurrentUserSysAdmin()) {
			trip = this.tripRepository.findOne(id);
		} else {
			trip = this.tripRepository.findByCreator_companyAndId(getCurrentCompany(), id);
		}

		if (null == trip) {
			throw new EntityNotFoundException();
		}
		return trip;
	}

	/**
	 * A simple method to return whether particular SystemUser is a teammember
	 * of trip
	 */
	public boolean isMemberParticipateInTrip(Long tripId, Long memberid) {
		return !tripRepository.isMemberParticipateInTrip(tripId, memberid).isEmpty();
	}
}
