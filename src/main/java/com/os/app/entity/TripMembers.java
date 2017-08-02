package com.os.app.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "trip_members")
public class TripMembers {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(targetEntity = Trip.class)
	@JoinColumn(name = "trip_id")
	private Trip trip;

	@ManyToOne(targetEntity = SystemUser.class)
	@JoinColumn(name = "user_id")
	private SystemUser user;

	public TripMembers() {
		// TODO Auto-generated constructor stub
	}

	public TripMembers(Long tripId, Long userId) {
		this.trip = new Trip(tripId);
		this.user = new SystemUser(userId);
	}

	public TripMembers(Trip trip, SystemUser user) {
		this.trip = trip;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Trip getTrip() {
		return trip;
	}

	public void setTrip(Trip trip) {
		this.trip = trip;
	}

	public SystemUser getUser() {
		return user;
	}

	public void setUser(SystemUser user) {
		this.user = user;
	}
}
