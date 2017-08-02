package com.os.app.entity;

import java.io.Serializable;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonView;
import com.os.app.views.BasicView;

@MappedSuperclass
public class StaffAwareEntity<ID extends Serializable> extends PersistableEntity<ID> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */

	@JoinColumn(name = "staff_user", nullable = false, updatable = false)
	@ManyToOne
	@JsonView(BasicView.Summary.class)
	private SystemUser staffUser;

	public SystemUser getStaffUser() {
		return staffUser;
	}

	public void setStaffUser(SystemUser staffUser) {
		this.staffUser = staffUser;
	}

}
