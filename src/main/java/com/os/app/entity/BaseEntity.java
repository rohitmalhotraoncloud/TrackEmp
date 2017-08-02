package com.os.app.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonView;
import com.os.app.views.BasicView;

@MappedSuperclass
public abstract class BaseEntity<ID extends Serializable> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonView(BasicView.Summary.class)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdTimestamp;

	@JsonView(BasicView.Summary.class)
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedTimestamp;

	public abstract ID getId();

	@PrePersist
	public void preSave() {
		this.createdTimestamp = new Date();
		this.modifiedTimestamp = this.createdTimestamp;
	}

	@Override
	public String toString() {
		return getClass() + " [getId()=" + getId() + "]";
	}

	@PreUpdate
	public void preUpdate() {
		this.modifiedTimestamp = new Date();
	}

	public Date getCreatedTimestamp() {
		return this.createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public Date getModifiedTimestamp() {
		return modifiedTimestamp;
	}

}
