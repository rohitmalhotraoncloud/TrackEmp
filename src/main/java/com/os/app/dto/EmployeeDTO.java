package com.os.app.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.os.app.entity.Company;
import com.os.app.entity.SystemUser;
import com.os.app.enums.Role;
import com.os.app.utils.Constants;

public class EmployeeDTO extends BaseDTO<SystemUser, Long> {
	private Long company;
	private String userName;
	private String firstName;
	private String lastName;
	private String gender;
	private String mobileNumber;
	private String email;
	private String jobType;
	private Role role;
	@JsonIgnore
	private String password;
	private String profilePhoto;
	private Boolean isActive;
	private Boolean isDeleted;
	private Date disabledTimestamp;
	private Long disabledByUser;
	private Boolean timesheetUser;

	public EmployeeDTO() {
	}

	public EmployeeDTO(SystemUser user, String imageUrlPrefix) {
		this.setId(user.getId());
		this.company = user.getCompany() == null ? null : user.getCompany().getId();
		this.userName = user.getUsername();
		this.lastName = user.getLastName();
		this.gender = user.getGender();
		this.mobileNumber = user.getMobileNumber();
		this.email = user.getEmail();
		this.role = user.getRole();
		this.isActive = user.getIsActive() == null ? true : user.getIsActive();
		this.firstName = user.getFirstName();
		this.password = user.getPassword();
		this.isDeleted = user.getIsDeleted();
		this.disabledByUser = user.getDisabledByUser() == null ? null : user.getDisabledByUser().getId();
		this.disabledTimestamp = user.getDisabledTimestamp();

		this.setProfilePhoto(user.getProfilePhoto() == null || user.getId() == null ? null
				: String.format(Constants.SYSTEMUSER_IMAGE_PATH, imageUrlPrefix, user.getId()));

		this.setCreatedTimestamp(user.getCreatedTimestamp());
		this.setModifiedTimestamp(user.getModifiedTimestamp());
		this.setTimesheetUser(user.isTimesheetUser());
	}

	public SystemUser toSystemUser() {
		SystemUser systemUser = new SystemUser();
		systemUser.setId(getId());
		systemUser.setCompany(getCompany() == null ? null : new Company(getCompany()));
		systemUser.setUsername(getUserName());
		systemUser.setFirstName(getFirstName());
		systemUser.setLastName(getLastName());
		systemUser.setGender(getGender());
		systemUser.setMobileNumber(getMobileNumber());
		systemUser.setEmail(getEmail());
		systemUser.setPassword(getPassword());
		systemUser.setRole(this.role);
		systemUser.setIsActive(isActive());
		if (getIsDeleted() != null) {
			systemUser.setIsDeleted(getIsDeleted());
		}
		systemUser.setProfilePhoto(getProfilePhoto());
		systemUser.setTimesheetUser(this.isTimesheetUser());
		return systemUser;
	}

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	@JsonProperty
	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public Long getCompany() {
		return company;
	}

	public void setCompany(Long company) {
		this.company = company;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Boolean isActive() {
		return isActive;
	}

	public void setActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getProfilePhoto() {
		return profilePhoto;
	}

	public void setProfilePhoto(String profilePhoto) {
		this.profilePhoto = profilePhoto;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public Long getDisabledByUser() {
		return disabledByUser;
	}

	public void setDisabledByUser(Long disabledByUser) {
		this.disabledByUser = disabledByUser;
	}

	public Date getDisabledTimestamp() {
		return disabledTimestamp;
	}

	public void setDisabledTimestamp(Date disabledTimestamp) {
		this.disabledTimestamp = disabledTimestamp;
	}

	public Boolean isTimesheetUser() {
		return timesheetUser;
	}

	public void setTimesheetUser(Boolean timesheetUser) {
		this.timesheetUser = timesheetUser;
	}

}