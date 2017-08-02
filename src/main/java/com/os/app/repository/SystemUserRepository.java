package com.os.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.os.app.entity.Company;
import com.os.app.entity.SystemUser;

@Repository
public interface SystemUserRepository extends CrudRepository<SystemUser, Long> {

	/**
	 * Returns record where email property matches with email parameter
	 */
	@Query("SELECT u FROM SystemUser u where u.email=?1")
	SystemUser findOneByEmail(String email);

	/**
	 * Returns record where email property matches with email parameter and
	 * company is null. This query is only accessible by SYSTEMADMIN
	 */
	@Query("SELECT u FROM SystemUser u where u.email=?1 and u.company IS NULL")
	SystemUser findOneByEmailAndNullCompanyId(String email);

	/**
	 * Returns record where username property matches with username parameter
	 * and company is null. This query is only accessible by SYSTEMADMIN
	 */
	@Query("SELECT u FROM SystemUser u where u.username=?1 and u.company IS NULL")
	SystemUser findOneByUsernameAndNullCompanyId(String username);

	/**
	 * Returns record where email property and company's subDomainName property
	 * name matches with email and subdomain parameters
	 */
	@Query("SELECT u FROM SystemUser u where u.email=?1 and u.company.subDomainName =?2")
	SystemUser findOneByEmailAndSubDomain(String email, String subdomain);

	/**
	 * Returns record where username property and company's subDomainName
	 * property matches to username and subdomain parameters
	 */
	@Query("SELECT u FROM SystemUser u where u.username=?1 and u.company.subDomainName =?2")
	SystemUser findOneByUsernameAndSubDomain(String username, String subdomain);

	/**
	 * Return records where companyId property matches to id parameter
	 */
	List<SystemUser> findByCompany_Id(Long id);

	/**
	 * Returns record where id property matches to id parameter and company
	 * matches to companyId parameter
	 */
	@Query("SELECT u FROM SystemUser u where u.company.id =?1 and u.id=?2")
	SystemUser findByCompanyIdAndId(Long companyid, Long id);

	/**
	 * Return records where username property matches to username parameter
	 */
	@Query("SELECT u FROM SystemUser u where u.username=?1")
	List<SystemUser> findByUsername(String username);

	@Query("from SystemUser u where u.company.id=?1 and u.isActive=true and u.isDeleted=false and u NOT IN(select m.user from Trip t join t.members m where (t.endTime IS NULL  or t.endTime>CURRENT_TIMESTAMP) and t.creator.company.id=?1) and u NOT IN(select t.creator from Trip t where (t.endTime IS NULL or t.endTime>CURRENT_TIMESTAMP) and t.creator.company.id=?1)")
	List<SystemUser> findAvailableForTripByCompany(Long companyId);

	@Query("Select u.id from SystemUser u where u.company.id=?1 and u.isActive=true and u.isDeleted=false "
			+ "and u.id IN ?2 and "
			+ "u NOT IN(select m.user from Trip t join t.members m where (t.endTime IS NULL  or "
			+ "t.endTime>CURRENT_TIMESTAMP) and t.creator.company.id=?1 and (?3=null or t.id NOT IN  (?3))) and u NOT "
			+ "IN(select t.creator from Trip t where (t.endTime IS NULL or t.endTime>CURRENT_TIMESTAMP) "
			+ "and t.creator.company.id=?1 and (?3=null or t.id NOT IN (?3)))")
	List<Long> findAvailableForTripByCompanySystemUsersAndTrip(Long companyId, Long[] userIds, Long tripId);

	@Query("Select u.id from SystemUser u where u.company.id=?1 and u.id IN ?2")
	List<Long> findIdsBelongToCompany(Long companyId, Long[] members);

	@Query("Select u from SystemUser u where (u.username=?1 or u.email=?1)  and u.company.subDomainName =?2 and u.isActive=true")
	SystemUser findUserByUsernameEmailAndSubDomain(String username, String subDomain);

	@Query("Select count(distinct t) AS totalTrips ,count( CASE WHEN cit IS NOT NULL and lu.isDeleted = FALSE Then 1 end) AS totalCheckIns from "
			+ "Trip t left join t.members m left join m.user u left join t.locationUpdates lu "
			+ "left join lu.type cit where t.creator.id =?1 or u.id=?1")
	List<Object[]> findUserStat(Long userId);

	List<SystemUser> findByCompanyAndTimesheetUser(Company company, boolean b);

}