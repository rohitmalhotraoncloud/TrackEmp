package com.os.app.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.os.app.utils.Constants;

@Entity
@Table
public class Company extends BaseEntity<Long> {

	private static final long serialVersionUID = 6982056471860768412L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(unique = true, nullable = false)
	@NotNull
	@Size(min = 1)
	private String companyName;

	@JsonIgnore
	@OneToOne(targetEntity = SystemUser.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "account_owner_id")
	private SystemUser accountOwner;

	private String address;

	private String postalCode;

	private String country;

	@Pattern(regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", message = "{Email.company.email}")
	private String email;

	private String phone;
	private String fax;
	private String website;

	@Column(unique = true)
	@NotNull
	@Size(min = 1)
	String subDomainName;

	String timezone = Constants.DEFAULT_TIMEZONE;

	private Boolean isForcePhototakingCheckIn;
	private Boolean isForcePhototakingCheckOut;
	private Boolean isForcePhototakingEndTrip;
	private Boolean isForcePhototakingTravelling;

	private Boolean hasCrmAddon;

	@Column(name = "has_timesheet_addon")
	private boolean hasTimesheetAddon;

	private Date tripsDailyCutoffTime;
	private Integer maxRadiusLocationUpdate;

	@Column(name = "is_automated_checkout_outs_on")
	private boolean isAutomatedCheckOutsOn;

	@Column(name = "automated_check_out_radius_metres")
	private long automatedCheckOutRadiusMetres;

	@PrePersist
	public void prePersist() {
		if (isForcePhototakingCheckIn == null)
			isForcePhototakingCheckIn = Constants.DEFAULT_FALSE;
		if (isForcePhototakingCheckOut == null)
			isForcePhototakingCheckOut = Constants.DEFAULT_FALSE;

		if (isForcePhototakingEndTrip == null)
			isForcePhototakingEndTrip = Constants.DEFAULT_FALSE;

		if (isForcePhototakingTravelling == null)
			isForcePhototakingTravelling = Constants.DEFAULT_FALSE;

		if (hasCrmAddon == null)
			hasCrmAddon = Constants.DEFAULT_FALSE;

		if (maxRadiusLocationUpdate == null)
			maxRadiusLocationUpdate = 0;
	}

	public Company() {
		// TODO Auto-generated constructor stub
	}

	public Company(Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getHasCrmAddon() {
		return hasCrmAddon;
	}

	public void setHasCrmAddon(Boolean hasCrmAddon) {
		this.hasCrmAddon = hasCrmAddon;
	}

	public Date getTripsDailyCutoffTime() {
		return tripsDailyCutoffTime;
	}

	public void setTripsDailyCutoffTime(Date tripsDailyCutoffTime) {
		this.tripsDailyCutoffTime = tripsDailyCutoffTime;
	}

	public String getSubDomainName() {
		return subDomainName;
	}

	public void setSubDomainName(String subDomainName) {
		this.subDomainName = subDomainName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public SystemUser getAccountOwner() {
		return accountOwner;
	}

	public void setAccountOwner(SystemUser accountOwner) {
		this.accountOwner = accountOwner;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public Boolean getIsForcePhototakingCheckIn() {
		return isForcePhototakingCheckIn;
	}

	public void setIsForcePhototakingCheckIn(Boolean isForcePhototakingCheckIn) {
		this.isForcePhototakingCheckIn = isForcePhototakingCheckIn;
	}

	public Boolean getIsForcePhototakingCheckOut() {
		return isForcePhototakingCheckOut;
	}

	public void setIsForcePhototakingCheckOut(Boolean isForcePhototakingCheckOut) {
		this.isForcePhototakingCheckOut = isForcePhototakingCheckOut;
	}

	public Boolean getIsForcePhototakingEndTrip() {
		return isForcePhototakingEndTrip;
	}

	public void setIsForcePhototakingEndTrip(Boolean isForcePhototakingEndTrip) {
		this.isForcePhototakingEndTrip = isForcePhototakingEndTrip;
	}

	public Boolean getIsForcePhototakingTravelling() {
		return isForcePhototakingTravelling;
	}

	public void setIsForcePhototakingTravelling(Boolean isForcePhototakingTravelling) {
		this.isForcePhototakingTravelling = isForcePhototakingTravelling;
	}

	public Integer getMaxRadiusLocationUpdate() {
		return maxRadiusLocationUpdate;
	}

	public void setMaxRadiusLocationUpdate(Integer maxRadiusLocationUpdate) {
		this.maxRadiusLocationUpdate = maxRadiusLocationUpdate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subDomainName == null) ? 0 : subDomainName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Company other = (Company) obj;
		if (subDomainName == null) {
			if (other.subDomainName != null)
				return false;
		} else if (!subDomainName.equals(other.subDomainName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Company [id=" + id + ", companyName=" + companyName + ", subDomainName=" + subDomainName + "]";
	}

	public boolean isHasTimesheetAddon() {
		return hasTimesheetAddon;
	}

	public void setHasTimesheetAddon(boolean hasTimesheetAddon) {
		this.hasTimesheetAddon = hasTimesheetAddon;
	}

	public boolean isAutomatedCheckOutsOn() {
		return isAutomatedCheckOutsOn;
	}

	public void setAutomatedCheckOutsOn(boolean isAutomatedCheckOutsOn) {
		this.isAutomatedCheckOutsOn = isAutomatedCheckOutsOn;
	}

	public long getAutomatedCheckOutRadiusMetres() {
		return automatedCheckOutRadiusMetres;
	}

	public void setAutomatedCheckOutRadiusMetres(long automatedCheckOutRadiusMetres) {
		this.automatedCheckOutRadiusMetres = automatedCheckOutRadiusMetres;
	}
}
