package com.os.app.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.os.app.entity.SystemUser;
import com.os.app.entity.Trip;
import com.os.app.utils.CustomDateDeserializer;
import com.os.app.utils.CustomDateSerializer;

public class TripDTO extends BaseDTO<Trip, Long> {
	private Long id;
	private Long[] members;
	private Long creator;
	private Long companyId;
	private Date startTime;
	private Date endTime;
	private Date expectedEndTime;
	private String tripType;
	private Long[] locationUpdates;

	public TripDTO() {
		// TODO Auto-generated constructor stub
	}

	public TripDTO(Trip trip) {
		this.setId(trip.getId());
		this.setMembers(trip.getMembersId());
		this.setCreator(trip.getCreator() == null ? null : trip.getCreator().getId());
		this.setStartTime(trip.getStartTime());
		this.setEndTime(trip.getEndTime());
		this.setExpectedEndTime(trip.getExpectedEndTime());
		this.setCompanyId(trip.getCompanyId());
		this.setCreatedTimestamp(trip.getCreatedTimestamp());
		this.setModifiedTimestamp(trip.getModifiedTimestamp());
	}

	public Trip toTrip() {
		Trip trip = new Trip();
		trip.setId(getId());
		trip.setMembersId(getMembers());
		trip.setCreator(getCreator() == null ? null : new SystemUser(getCreator()));
		trip.setStartTime(getStartTime());
		trip.setEndTime(getEndTime());
		trip.setExpectedEndTime(getExpectedEndTime());
		trip.setCompanyId(getCompanyId());
		return trip;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@JsonIgnore
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long[] getMembers() {
		return members;
	}

	public void setMembers(Long[] members) {
		this.members = members;
	}

	public Long getCreator() {
		return creator;
	}

	public void setCreator(Long creator) {
		this.creator = creator;
	}

	@JsonSerialize(using = CustomDateSerializer.class)
	public Date getStartTime() {
		return startTime;
	}

	@JsonSerialize(using = CustomDateDeserializer.class)
	public void setStartTime(Date startTime) {
		if (startTime == null) {
			startTime = new Date();
		}
		this.startTime = startTime;
	}

	@JsonSerialize(using = CustomDateSerializer.class)
	public Date getEndTime() {
		return endTime;
	}

	@JsonSerialize(using = CustomDateDeserializer.class)
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@JsonSerialize(using = CustomDateSerializer.class)
	public Date getExpectedEndTime() {
		return expectedEndTime;
	}

	@JsonSerialize(using = CustomDateDeserializer.class)
	public void setExpectedEndTime(Date expectedEndTime) {
		this.expectedEndTime = expectedEndTime;
	}

	public String getTripType() {
		return tripType;
	}

	public void setTripType(String tripType) {
		this.tripType = tripType;
	}

	public Long[] getLocationUpdates() {
		return locationUpdates;
	}

	public void setLocationUpdates(Long[] locationUpdates) {
		this.locationUpdates = locationUpdates;
	}

}