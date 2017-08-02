package com.os.app.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.os.app.entity.Company;
import com.os.app.entity.SystemUser;
import com.os.app.entity.Trip;

public interface TripRepository extends JpaRepository<Trip, Long> {
	/**
	 * Returns unique trip records with trip's member records based on these
	 * conditions: (1) if record's company property matched with given companyId
	 * parameter OR record is global (no company id) (2) and if userId parameter
	 * is given and mine parameter is true then returns only record where
	 * creatorid match with userId OR members records match with userid (3) and
	 * if onlyEndedTrip parameter is true then returns where endTime is less
	 * then current timestamp (4) and if onlyOpenTrips parameter is true then
	 * returns where endTime is either null or endTime is more than current
	 * timestamp
	 */
	@Query("Select distinct(t) from Trip t left join fetch t.members m left join fetch m.user u  left join t.creator cr "
			+ "where (false=?3 or (t.creator.id =?2 or u.id=?2)) and (false=?4 or ( t.endTime<CURRENT_TIMESTAMP)) "
			+ "and (false=?5 or (t.endTime IS NULL or t.endTime>CURRENT_TIMESTAMP)) and (t.creator.company.id=?1 or "
			+ "t.creator.company IS NULL) "
			+ " and ((null is ?6 or (t.startTime>=?6)) and (null is ?7 or (t.startTime<=?7)))"
			+ " and ((null is ?8 or (t.endTime>=?8)) and (null is ?9 or (t.endTime<=?9)))"
			+ " and (null is ?10 or (t.creator.id IN (?13) or u.id IN (?13)))"
			+ " and (null is ?11 or (t.tripType.id IN (?14)))" + " and (null is ?12 or (cr.jobType.id IN (?15)))"
			+ "order by t.modifiedTimestamp,t.createdTimestamp")
	public List<Trip> findTrips(Long companyId, Long userId, Boolean mine, Boolean onlyEndedTrips,
			Boolean onlyOpenTrips, Date fromStartDate, Date toStartDate, Date fromEndDate, Date toEndDate,
			Boolean hasEmployee, Boolean hasTripTypes, Boolean hasJobTypes, List<Long> employees, List<Long> tripTypes,
			List<Long> jobTypes, Pageable pageRequest);

	/**
	 * Returns record where property id matches to tripId parameter and
	 * (triptype's company property matches to companyId parameter OR triptype's
	 * company is NULL (i.e. tripType is global))
	 */
	@Query("Select t from Trip t where (t.creator.company.id=?2 or t.creator.company IS NULL) and t.id=?1")
	public Trip findTrip(Long tripid, Long companyId);

	/**
	 * Returns record where property id matches to tripId parameter and
	 * (triptype's company property matches to companyId parameter)
	 */
	@Query("Select t from Trip t where t.creator.company.id=?2 and t.id=?1")
	public Trip findTripByIdAndCompanyId(Long tripid, Long companyId);

	/**
	 * Returns 1 when id matches to tripId parameter and memberId is either the
	 * creator or the TripMember of trip. Returns NULL otherwise
	 */
	@Query("Select distinct(1) from Trip t left join t.members m left join m.user u where (t.creator.id =?2 or u.id=?2) and t.id=?1")
	public List<Long> isMemberParticipateInTrip(Long tripId, Long memberid);

	public Trip findByCreator_company(Company currentCompany);

	public Trip findByCreator_companyAndId(Company currentCompany, Long id);

	public Trip findTripByIdAndCreator(Long id, SystemUser currentUser);

}
