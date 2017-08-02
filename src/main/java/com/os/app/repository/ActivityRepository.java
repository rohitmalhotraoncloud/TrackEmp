package com.os.app.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.os.app.entity.Activity;
import com.os.app.entity.Company;
import com.os.app.entity.SystemUser;
import com.os.app.enums.GeofenceStatus;
import com.os.app.enums.TimesheetActivityStatus;

public interface ActivityRepository extends CrudRepository<Activity, Long> {

	public Activity findOneByArriveLocationUpdateIdAndArriveLocationUpdate_actor_company(Long id, Company company);

	public Activity findOneByDepartLocationUpdateIdAndDepartLocationUpdate_actor_company(Long id, Company company);

	public Activity findOneByIdAndArriveLocationUpdate_actor_company(Long id, Company company);

	@Modifying
	@Transactional
	@Query("update Activity set timesheetStatus = ?2 where id= ?1")
	public void updateTimesheetStatusForActivity(Long activityId, TimesheetActivityStatus status);

	public Activity findOneByArriveLocationUpdate_actorAndId(SystemUser currentUser, Long id);

	@Query("select distinct id from Activity where arriveLocationUpdate.actor=?1 and geofenceStatus=?2")
	public Long findByArriveLocationUpdate_actorAndGeofenceStatus(SystemUser currentUser,
			GeofenceStatus geofenceStatus);

	public List<Activity> findByIdIn(List<Long> activitieIds);

	@Modifying
	@Transactional
	@Query(value = "update Activity set geofenceStatus = 'OFF' where id = ?1 ")
	public void turnOffAllActivityGeoFenceStatusForUser(Long activityWithGeoFenceOn);

	@Query(value = "Select a.arriveLocationUpdate.trip.id from Activity a where a.id=?1")
	public Long findTripIdForActivity(Long activityId);

}
