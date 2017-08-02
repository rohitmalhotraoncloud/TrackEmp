package com.os.app.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.os.app.views.BasicView;

@Entity
@Table
public class Trip extends BaseEntity<Long> {

	private static final long serialVersionUID = 1552866336273343533L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(BasicView.Summary.class)
	private Long id;

	@ManyToOne(targetEntity = SystemUser.class)
	@JoinColumn(name = "creator_id")
	private SystemUser creator;

	@OneToMany(mappedBy = "trip", cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	private List<TripMembers> members = new ArrayList<TripMembers>();

	@JsonView(BasicView.Summary.class)
	private Date startTime;

	@JsonView(BasicView.Summary.class)
	private Date endTime;

	@JsonView(BasicView.Summary.class)
	private Date expectedEndTime;

	@Transient
	private Long companyId;

	public Trip() {
		// TODO Auto-generated constructor stub
	}

	public Trip(Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public SystemUser getCreator() {
		return creator;
	}

	public void setCreator(SystemUser creator) {
		this.creator = creator;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getExpectedEndTime() {
		return expectedEndTime;
	}

	public void setExpectedEndTime(Date expectedEndTime) {
		this.expectedEndTime = expectedEndTime;
	}

	public List<TripMembers> getMembers() {
		return members;
	}

	public void setMembers(List<TripMembers> members) {
		this.members = members;
	}

	public Long[] getMembersId() {
		if (members == null) {
			return null;
		}
		Long[] ids = new Long[members.size()];
		int index = 0;
		for (TripMembers member : members) {
			ids[index] = member.getUser().getId();
			index++;
		}
		return ids;
	}

	public void setMembersId(Long[] ids) {
		if (ids != null) {
			if (members == null) {
				members = new ArrayList<TripMembers>();
			}
			for (Long id : ids) {
				TripMembers member = new TripMembers();
				member.setUser(new SystemUser(id));
				member.setTrip(this);
				members.add(member);
			}
		}
	}

	@Override
	public String toString() {
		return "Trip [id=" + id + ", creator=" + creator + "]";
	}

}