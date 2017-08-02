package com.os.app.entity;

import java.io.Serializable;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

import com.os.app.service.CommonService;
import com.fasterxml.jackson.annotation.JsonIgnore;

@MappedSuperclass
public class CompanyAwareEntity<PK extends Serializable> extends PersistableEntity<PK> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JoinColumn(name = "company_id")
	@ManyToOne
	@JsonIgnore
	private Company company;

	public Company getCompany() {
		return company;
	}

	@Override
	@PrePersist
	public void preSave() {
		super.preSave();
		if (null == company) {
			this.company = CommonService.getCurrentCompany();
		}
	}

	@Override
	public String toString() {
		return getClass() + " [company=" + company + ", id=" + super.getId() + "]";
	}

	protected void setCompany(Company company) {
		this.company = company;
	}

	public Long getCompanyId() {
		if (null != company) {
			return this.company.getId();
		}
		return null;
	}

}
