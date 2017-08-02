package com.os.app.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.os.app.dto.ActivityDTO;
import com.os.app.entity.Activity;
import com.os.app.entity.SystemUser;
import com.os.app.enums.GeofenceStatus;
import com.os.app.enums.TimesheetActivityStatus;
import com.os.app.repository.ActivityRepository;

@Service
public class ActivityService extends GenericService<Activity, Long> {

	@Autowired
	EmployeeService employeeService;

	@Autowired
	SystemUserService systemUserService;

	public ActivityDTO saveActivity(Activity activity) {
		return new ActivityDTO(this.repository.save(activity));
	}

	public ActivityDTO updateActivity(Long id, Activity activity) {
		Activity entity = this.patch(id, activity);

		return new ActivityDTO(repository.save(entity));
	}

	public ActivityDTO findActivity(Long activityId) {
		Activity activity = findById(activityId);
		return new ActivityDTO(activity);
	}

	@Override
	public Activity findById(Long id) {
		Activity activity = null;
		if (isCurrentUserAManagerOrCompanyAdmin()) {
			activity = this.getRepository().findOneByIdAndArriveLocationUpdate_actor_company(id, getCurrentCompany());
		} else {
			activity = this.getRepository().findOneByArriveLocationUpdate_actorAndId(getCurrentUser(), id);
		}
		return throwEntityNotFoundIfNull(activity);
	}

	public ActivityDTO findActivityByArriveLocationUpdate(Long locationUpdateId) {
		Activity activity = getRepository().findOneByArriveLocationUpdateIdAndArriveLocationUpdate_actor_company(
				locationUpdateId, getCurrentUser().getCompany());
		validateIfUserHasAccessToActivity(activity, getCurrentUser());

		return new ActivityDTO(activity);
	}

	protected static void validateIfUserHasAccessToActivity(Activity activity, SystemUser currentUser) {
		if (activity == null) {
			throw new EntityNotFoundException();
		}
	}

	@Override
	protected ActivityRepository getRepository() {
		return (ActivityRepository) this.repository;
	}

	public List<ActivityDTO> getStaffCurrentActivity() {
		List<ActivityDTO> returnList = new ArrayList<ActivityDTO>();
		return returnList;
	}

	public ActivityDTO findActivityByDepartLocationUpdate(Long locationUpdateId) {
		Activity activity = getRepository().findOneByDepartLocationUpdateIdAndDepartLocationUpdate_actor_company(
				locationUpdateId, getCurrentUser().getCompany());
		validateIfUserHasAccessToActivity(activity, getCurrentUser());
		return new ActivityDTO(activity);
	}

	public void deleteById(Long id) {
		Activity activity = findById(id);
		this.getRepository().delete(activity);

	}

	public Collection<Activity> getActivity() {
		return null;
	}

	public void updateTimesheetStatusForActivity(Activity activity, TimesheetActivityStatus status) {
		logger.info("Updating status of " + activity + " to status " + status);
		this.getRepository().updateTimesheetStatusForActivity(activity.getId(), status);
	}

	public void setGetfenceOn(long id) {

		Activity activity = this.findById(id);
		// Set the status of all other activity as OFF
		Long activityWithGeoFenceOn = this.getRepository()
				.findByArriveLocationUpdate_actorAndGeofenceStatus(getCurrentUser(), GeofenceStatus.ON);
		if (null != activityWithGeoFenceOn) {
			this.getRepository().turnOffAllActivityGeoFenceStatusForUser(activityWithGeoFenceOn);
		}

		activity.setGeofenceStatus(GeofenceStatus.ON);
		this.repository.save(activity);
	}

	@Async
	public void setGeofenceOff(long id) {
		Activity activity = this.findById(id);
		if (GeofenceStatus.OFF == activity.getGeofenceStatus()) {
			return;
		}
		activity.setGeofenceStatus(GeofenceStatus.OFF);
		this.repository.save(activity);
	}

	public Map<String, Long> getCurrentTrackerState() {
		Long activityId = this.getRepository().findByArriveLocationUpdate_actorAndGeofenceStatus(getCurrentUser(),
				GeofenceStatus.ON);
		if (null == activityId) {
			throw new EntityNotFoundException();
		}
		Long tripId = this.getRepository().findTripIdForActivity(activityId);
		Map<String, Long> returnVal = new HashMap<>();
		returnVal.put("activityId", activityId);
		returnVal.put("trip", tripId);
		return returnVal;

	}

}