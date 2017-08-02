package com.os.app.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.os.app.utils.Constants;

@Entity
@Table
public class UserDevice extends BaseEntity<Long> {

	private static final long serialVersionUID = -3735353382327707883L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(targetEntity = SystemUser.class)
	@JoinColumn(name = "owner_id")
	private SystemUser owner;

	@Column(unique = true)
	private String uniqueIdentifier;

	private String name;

	private String model;

	private String platform;

	private String version;

	@Column
	private Boolean isApproved;

	private Date approvedDate;

	@ManyToOne(targetEntity = SystemUser.class)
	@JoinColumn(name = "approved_by")
	private SystemUser approvedBy;

	@PrePersist
	public void prePersist() {
		if (isApproved == null)
			isApproved = Constants.DEFAULT_FALSE;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SystemUser getOwner() {
		return owner;
	}

	public void setOwner(SystemUser owner) {
		this.owner = owner;
	}

	public String getUniqueIdentifier() {
		return uniqueIdentifier;
	}

	public void setUniqueIdentifier(String uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Boolean getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(Boolean isApproved) {
		this.isApproved = isApproved;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	public SystemUser getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(SystemUser approvedBy) {
		this.approvedBy = approvedBy;
	}
}
