package com.os.app.dto;

import java.util.Date;

import com.os.app.entity.Company;
import com.os.app.entity.SystemUser;

public class CompanyDTO extends BaseDTO<Company, Long> {
	private Long id;
	private String companyName;
	private Long accountOwner;
	private String address;
	private String postalCode;
	private String country;
	private String email;
	private String phone;
	private String fax;
	private String website;
	private String subDomainName;
	private String timezone;
	private Long subscription;
	private Boolean isForcePhototakingCheckIn;
	private Boolean isForcePhototakingCheckOut;
	private Boolean isForcePhototakingEndTrip;
	private Boolean isForcePhototakingTravelling;
	private Integer maxRadiusLocationUpdate;
	private boolean hasTimesheetAddon;
	private boolean isAutomatedCheckOutsOn;

	private long automatedCheckOutRadiusMetres;

	private Date tripsDailyCutoffTime;
	private Boolean hasCrmAddon;

	public CompanyDTO() {

	}

	public CompanyDTO(Company company) {
		if (company != null) {
			this.setId(company.getId());
			this.setCompanyName(company.getCompanyName());
			this.setAccountOwner(company.getAccountOwner() == null ? null : company.getAccountOwner().getId());
			this.setAddress(company.getAddress());
			this.setPostalCode(company.getPostalCode());
			this.setCountry(company.getCountry());
			this.setEmail(company.getEmail());
			this.setPhone(company.getPhone());
			this.setFax(company.getFax());
			this.setWebsite(company.getWebsite());
			this.setSubDomainName(company.getSubDomainName());
			this.setTimezone(company.getTimezone());
			this.setIsForcePhototakingCheckIn(company.getIsForcePhototakingCheckIn());
			this.setIsForcePhototakingCheckOut(company.getIsForcePhototakingCheckOut());
			this.setIsForcePhototakingEndTrip(company.getIsForcePhototakingEndTrip());
			this.setIsForcePhototakingTravelling(company.getIsForcePhototakingTravelling());
			this.setMaxRadiusLocationUpdate(company.getMaxRadiusLocationUpdate());
			this.setCreatedTimestamp(company.getCreatedTimestamp());
			this.setModifiedTimestamp(company.getModifiedTimestamp());
			this.setHasCrmAddon(company.getHasCrmAddon());
			this.setTripsDailyCutoffTime(company.getTripsDailyCutoffTime());
			this.setHasTimesheetAddon(company.isHasTimesheetAddon());
			this.setAutomatedCheckOutsOn(company.isAutomatedCheckOutsOn());
			this.setAutomatedCheckOutRadiusMetres(company.getAutomatedCheckOutRadiusMetres());
		}
	}

	public boolean isAutomatedCheckOutsOn() {
		return isAutomatedCheckOutsOn;
	}

	public void setAutomatedCheckOutsOn(boolean isAutomatedCheckOutsOn) {
		this.isAutomatedCheckOutsOn = isAutomatedCheckOutsOn;
	}

	public Company toCompany() {
		Company company = new Company();
		company.setId(getId());
		company.setCompanyName(getCompanyName());
		company.setAccountOwner(getAccountOwner() == null ? null : new SystemUser(getAccountOwner()));
		company.setAddress(getAddress());
		company.setPostalCode(getPostalCode());
		company.setCountry(getCountry());
		company.setEmail(getEmail());
		company.setPhone(getPhone());
		company.setFax(getFax());
		company.setWebsite(getWebsite());
		company.setSubDomainName(getSubDomainName());
		company.setTimezone(getTimezone());
		if (getIsForcePhototakingCheckIn() != null) {
			company.setIsForcePhototakingCheckIn(getIsForcePhototakingCheckIn());
		}
		if (getIsForcePhototakingCheckOut() != null) {
			company.setIsForcePhototakingCheckOut(getIsForcePhototakingCheckOut());
		}
		if (getIsForcePhototakingEndTrip() != null) {
			company.setIsForcePhototakingEndTrip(getIsForcePhototakingEndTrip());
		}
		if (getIsForcePhototakingTravelling() != null) {
			company.setIsForcePhototakingTravelling(getIsForcePhototakingTravelling());
		}
		if (getMaxRadiusLocationUpdate() != null) {
			company.setMaxRadiusLocationUpdate(getMaxRadiusLocationUpdate());
		}

		if (getHasCrmAddon() != null) {
			company.setHasCrmAddon(getHasCrmAddon());
		}
		company.setTripsDailyCutoffTime(getTripsDailyCutoffTime());
		company.setHasTimesheetAddon(this.isHasTimesheetAddon());
		company.setAutomatedCheckOutsOn(this.isAutomatedCheckOutsOn);
		company.setAutomatedCheckOutRadiusMetres(this.automatedCheckOutRadiusMetres);

		return company;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Date getTripsDailyCutoffTime() {
		return tripsDailyCutoffTime;
	}

	public void setTripsDailyCutoffTime(Date tripsDailyCutoffTime) {
		this.tripsDailyCutoffTime = tripsDailyCutoffTime;
	}

	public Boolean getHasCrmAddon() {
		return hasCrmAddon;
	}

	public void setHasCrmAddon(Boolean hasCrmAddon) {
		this.hasCrmAddon = hasCrmAddon;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Long getAccountOwner() {
		return accountOwner;
	}

	public void setAccountOwner(Long accountOwner) {
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

	public String getSubDomainName() {
		return subDomainName;
	}

	public void setSubDomainName(String subDomainName) {
		this.subDomainName = subDomainName;
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

	public Long getSubscription() {
		return subscription;
	}

	public void setSubscription(Long subscription) {
		this.subscription = subscription;
	}

	public boolean isHasTimesheetAddon() {
		return hasTimesheetAddon;
	}

	public void setHasTimesheetAddon(boolean hasTimesheetAddon) {
		this.hasTimesheetAddon = hasTimesheetAddon;
	}

	public long getAutomatedCheckOutRadiusMetres() {
		return automatedCheckOutRadiusMetres;
	}

	public void setAutomatedCheckOutRadiusMetres(long automatedCheckOutRadiusMetres) {
		this.automatedCheckOutRadiusMetres = automatedCheckOutRadiusMetres;
	}

}