package com.os.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.os.app.entity.Company;

@Repository
public interface CompanyRepository extends CrudRepository<Company, Long> {
	/**
	 * Return records where id and companyid properties match with
	 * accountOwnerId,id parameters
	 */
	@Query("Select s.company from SystemUser s where s.id =?2 and s.company.id=?1")
	public Company findByCompanyIdAndAccountOwnerId(Long id, Long accountOwnerId);

	/**
	 * Returns only subDomainName property where subDomainName property match
	 * with subDomain parameter
	 */
	@Query("Select c.subDomainName from Company c where c.subDomainName=?1")
	public String findRecordBySubdomain(String subDomain);

	@Query("Select count(e.id) from SystemUser e where e.company.id=?1")
	public Long findEmployeeCount(Long companyId);

	@Query("Select count(s.id), count(case when s.isActive=true then s.id end), count(case when s.isActive=false then s.id end) from SystemUser s where s.company.id=?1 and s.isDeleted=false")
	public Object[][] findCompanyUserCount(Long companyId);

	public Company findBySubDomainNameIgnoreCase(String subDomain);

	public List<Company> findByHasTimesheetAddon(boolean b);

	@Query("select automatedCheckOutRadiusMetres from Company where id=?1")
	public Integer getAutomatedCheckOutRadiusMetresByCompany(Long id);

	@Query("select isAutomatedCheckOutsOn from Company where id=?1")
	public boolean findIsAutomatedCheckOutsOn(Long id);

	@Modifying
	@Transactional
	@Query("update Company set isAutomatedCheckOutsOn=true where id=?1")
	public void updateisAutomatedCheckOutsOnToTrue(Long id);

}
