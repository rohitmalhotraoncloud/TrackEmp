package com.os.app.dto;

import com.os.app.entity.Activity;

public class ActivityDTO extends BaseDTO<Activity, Long> {
	private Long id;
	private String referenceId;
	private String metaData;
	private Long arriveLocationUpdate;
	private Long departLocationUpdate;
	Boolean isCrmActivity;
	String personOrOrganisationName;

	public ActivityDTO() {
	}

	public ActivityDTO(Activity activity) {
		this.setId(activity.getId());
		this.setReferenceId(activity.getReferenceId());
		this.setMetaData(activity.getMetaData());
		this.setIsCrmActivity(activity.getIsCrmActivity());
		this.setPersonOrOrganisationName(activity.getPersonOrOrganisationName());
		this.setCreatedTimestamp(activity.getCreatedTimestamp());
		this.setModifiedTimestamp(activity.getModifiedTimestamp());

	}

	public Activity toActivity() {
		Activity activity = new Activity();
		activity.setId(getId());
		activity.setReferenceId(getReferenceId());
		activity.setMetaData(getMetaData());
		activity.setIsCrmActivity(getIsCrmActivity());
		activity.setPersonOrOrganisationName(getPersonOrOrganisationName());
		return activity;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	public Long getArriveLocationUpdate() {
		return arriveLocationUpdate;
	}

	public void setArriveLocationUpdate(Long arriveLocationUpdate) {
		this.arriveLocationUpdate = arriveLocationUpdate;
	}

	public Long getDepartLocationUpdate() {
		return departLocationUpdate;
	}

	public void setDepartLocationUpdate(Long departLocationUpdate) {
		this.departLocationUpdate = departLocationUpdate;
	}

	public Boolean getIsCrmActivity() {
		return isCrmActivity;
	}

	public void setIsCrmActivity(Boolean isCrmActivity) {
		this.isCrmActivity = isCrmActivity;
	}

	public String getPersonOrOrganisationName() {
		return personOrOrganisationName;
	}

	public void setPersonOrOrganisationName(String personOrOrganisationName) {
		this.personOrOrganisationName = personOrOrganisationName;
	}

}