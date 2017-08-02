package com.os.app.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.os.app.annotation.PartialUpdatable;
import com.os.app.enums.GeofenceStatus;
import com.os.app.enums.TimesheetActivityStatus;
import com.os.app.utils.Constants;
import com.os.app.views.BasicView;

@Entity
@Table
public class Activity extends BaseEntity<Long> {

	private static final long serialVersionUID = 1552866336273343533L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(BasicView.Summary.class)
	private Long id;

	@JsonView(BasicView.Summary.class)
	private String referenceId;

	@NotNull
	@Size(min = 1)
	@JsonView(BasicView.Summary.class)
	@Lob
	private String metaData;

	@Transient
	private Long companyId;

	@PartialUpdatable
	@Column(name = "is_crm_activity")
	@JsonView(BasicView.Summary.class)
	private Boolean isCrmActivity;

	@PartialUpdatable
	@JsonView(BasicView.Summary.class)
	private String personOrOrganisationName;

	@Column(name = "timesheet_status")
	@Enumerated(EnumType.STRING)
	private TimesheetActivityStatus timesheetStatus;

	@Column(name = "geofence_status")
	@Enumerated(EnumType.STRING)
	private GeofenceStatus geofenceStatus = GeofenceStatus.OFF;

	public Activity() {
	}

	@PrePersist
	public void prePersist() {
		if (isCrmActivity == null)
			isCrmActivity = Constants.DEFAULT_FALSE;
	}

	public Activity(Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

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

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
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

	@Override
	public String toString() {
		return "Activity [id=" + id + "]";
	}

	public TimesheetActivityStatus getTimesheetStatus() {
		return timesheetStatus;
	}

	public void setTimesheetStatus(TimesheetActivityStatus timesheetStatus) {
		this.timesheetStatus = timesheetStatus;
	}

	public GeofenceStatus getGeofenceStatus() {
		return geofenceStatus;
	}

	public void setGeofenceStatus(GeofenceStatus geofenceStatus) {
		this.geofenceStatus = geofenceStatus;
	}
}