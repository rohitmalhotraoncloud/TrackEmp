package com.os.app.entity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
//import jdk.nashorn.internal.ir.annotations.Ignore;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonView;
import com.os.app.annotation.PartialUpdatable;
import com.os.app.enums.Role;
import com.os.app.views.BasicView;
import com.os.app.views.BasicView.TimesheetWithSummary;

@Entity
@Table
public class SystemUser extends BaseEntity<Long> implements UserDetails {

	private static final long serialVersionUID = -5066443817826662034L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(targetEntity = Company.class)
	@JoinColumn(name = "company_id")
	@JsonIgnore
	private Company company;

	private String username;

	// @NotBlank(message = "{NotBlank.systemUser.firstName}")
	@JsonView(BasicView.Summary.class)
	private String firstName;

	// @NotBlank(message = "{NotBlank.systemUser.lastName}")
	@JsonView(BasicView.Summary.class)
	private String lastName;

	// @NotBlank(message = "{NotBlank.systemUser.password}")
	private String password;

	private String gender;

	private String mobileNumber;

	@JsonView(TimesheetWithSummary.class)
	private String profilePhoto;

	@PartialUpdatable
	@Column(name = "is_timesheet_user")
	private Boolean timesheetUser;

	@Transient
	private String subdomain;

	@Email
	@NotNull
	@Column(name = "email", nullable = false)
	private String email;

	private Boolean isActive;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private Role role;

	private Boolean isDeleted;

	private Date disabledTimestamp;

	@ManyToOne(targetEntity = SystemUser.class)
	@JoinColumn(name = "disabled_by_user")
	private SystemUser disabledByUser;

	@PrePersist
	public void prePersist() {
		if (isActive == null) {
			this.isActive = true;
		}
		if (isDeleted == null) {
			isDeleted = false;
		}
	}

	@JsonGetter("company")
	public long getCompanyId() {
		if (null != company) {
			return company.getId();
		}
		return 0;
	}

	public SystemUser() {
	}

	public SystemUser(Long id) {
		this.id = id;
	}

	public SystemUser(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	@JsonIgnore
	public String getPassword() {
		return password;
	}

	@JsonSetter
	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Arrays.asList(new Role[] { this.role });
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public Company getCompany() {
		return company;
	}

	@JsonSetter("company")
	public void setCompany(Company company) {
		this.company = company;
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

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getSubdomain() {
		return subdomain;
	}

	public void setSubdomain(String subdomain) {
		this.subdomain = subdomain;
	}

	public String getProfilePhoto() {
		return profilePhoto;
	}

	public void setProfilePhoto(String profilePhoto) {
		this.profilePhoto = profilePhoto;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public SystemUser getDisabledByUser() {
		return disabledByUser;
	}

	public void setDisabledByUser(SystemUser disabledByUser) {
		this.disabledByUser = disabledByUser;
	}

	public Date getDisabledTimestamp() {
		return disabledTimestamp;
	}

	public void setDisabledTimestamp(Date disabledTimestamp) {
		this.disabledTimestamp = disabledTimestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		SystemUser other = (SystemUser) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SystemUser [id=" + id + ", company=" + company + ", username=" + username + "]";
	}

	public Boolean isTimesheetUser() {
		return null != timesheetUser && timesheetUser;
	}

	public void setTimesheetUser(Boolean timesheetUser) {
		this.timesheetUser = timesheetUser;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

}
