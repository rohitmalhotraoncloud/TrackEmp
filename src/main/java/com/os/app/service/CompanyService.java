package com.os.app.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.os.app.beans.ErrorResponse;
import com.os.app.dto.CompanyDTO;
import com.os.app.dto.EmployeeCountDTO;
import com.os.app.entity.Company;
import com.os.app.entity.SystemUser;
import com.os.app.enums.ErrorKeys;
import com.os.app.exception.EmpTrackerException;
import com.os.app.repository.CompanyRepository;
import com.os.app.utils.Constants;

@Service
public class CompanyService extends GenericService<Company, Long> {

	@Autowired
	EmployeeService employeeService;
	@Autowired
	CompanyRepository companyRepository;

	/**
	 * Code to save Company object into database
	 */
	public Company saveCompany(Company company) {
		if (Constants.SYSTEMADMIN_DOMAIN.equalsIgnoreCase(company.getSubDomainName())) {
			throw new EmpTrackerException(ErrorKeys.UNPROCESSABLE_ENTITY);
		}
		return companyRepository.save(company);
	}

	@Override
	public Company findById(Long id) {
		if (isCurrentUserSysAdmin()) {
			return super.findById(id);
		}
		if (id.equals(getCurrentCompany().getId())) {
			return super.findById(id);
		}
		throw new EntityNotFoundException();
	}

	@Override
	public Company patch(Long id, Company company) {
		Company entity = findById(id);

		SystemUser loggedInUser = getCurrentUser();
		if (!id.equals(loggedInUser.getCompany().getId())) {
			throw new AccessDeniedException(messageService.getMessage("error.accessdenied"));
		}

		if (company.getAccountOwner() != null) {
			entity.setAccountOwner(company.getAccountOwner());
		}

		if (company.getCompanyName() != null) {
			entity.setCompanyName(company.getCompanyName());
		}
		if (company.getAddress() != null) {
			entity.setAddress(company.getAddress());
		}
		if (company.getPostalCode() != null) {
			entity.setPostalCode(company.getPostalCode());
		}
		if (company.getCountry() != null) {
			entity.setCountry(company.getCountry());
		}
		if (company.getEmail() != null) {
			entity.setEmail(company.getEmail());
		}
		if (company.getPhone() != null) {
			entity.setPhone(company.getPhone());
		}
		if (company.getFax() != null) {
			entity.setFax(company.getFax());
		}
		if (company.getWebsite() != null) {
			entity.setWebsite(company.getWebsite());
		}
		if (company.getSubDomainName() != null) {
			entity.setSubDomainName(company.getSubDomainName());
		}
		if (company.getTimezone() != null) {
			entity.setTimezone(company.getTimezone());
		}
		if (company.getIsForcePhototakingCheckIn() != null) {
			entity.setIsForcePhototakingCheckIn(company.getIsForcePhototakingCheckIn());
		}
		if (company.getIsForcePhototakingCheckOut() != null) {
			entity.setIsForcePhototakingCheckOut(company.getIsForcePhototakingCheckOut());
		}
		if (company.getIsForcePhototakingEndTrip() != null) {
			entity.setIsForcePhototakingEndTrip(company.getIsForcePhototakingEndTrip());
		}
		if (company.getIsForcePhototakingTravelling() != null) {
			entity.setIsForcePhototakingTravelling(company.getIsForcePhototakingTravelling());
		}
		if (company.getMaxRadiusLocationUpdate() != null) {
			entity.setMaxRadiusLocationUpdate(company.getMaxRadiusLocationUpdate());
		}

		if (company.getHasCrmAddon() != null) {
			entity.setHasCrmAddon(company.getHasCrmAddon());
		}
		if (company.getMaxRadiusLocationUpdate() != null) {
			entity.setMaxRadiusLocationUpdate(company.getMaxRadiusLocationUpdate());
		}
		return companyRepository.save(entity);
	}

	/**
	 * Retuns Company record based on id. here loggedInUser is user who access
	 * this method. If current User is SYSTEMADMIN, he can access any company
	 * record. But if he is an employee(COMPANYADMIN,STAFF,MANAGER), he can
	 * access only his company record
	 */
	public Company findCompanyByIdAndAccountOwnerId(Long id, SystemUser loggedInUser) {
		return companyRepository.findByCompanyIdAndAccountOwnerId(id, loggedInUser.getId());
	}

	/**
	 * Method returns all companies. This method is only accessible for
	 * SYSTEMADMIN
	 */
	public List<CompanyDTO> findCompaniesDto() {
		List<CompanyDTO> companiesDto = new ArrayList<CompanyDTO>();
		Iterable<Company> companies = companyRepository.findAll();
		for (Company company : companies) {
			companiesDto.add(new CompanyDTO(company));
		}
		return companiesDto;
	}

	/**
	 * Function returns a boolean true/false based on subDomain parameter. It
	 * checks given subdomain in Company, if exists returns true otherwise false
	 */
	public boolean subDomainCheck(String subDomain) {
		return companyRepository.findRecordBySubdomain(subDomain) == null ? false : true;
	}

	public Long getUserCount(Long companyId) {
		return companyRepository.findEmployeeCount(companyId);
	}

	public EmployeeCountDTO getCompanyUserCount(SystemUser loggedInUser, Long companyId) {
		if (isCurrentUserSysAdmin()
				|| (isCurrentUserAManagerOrCompanyAdmin() && loggedInUser.getCompany().getId().equals(companyId))) {
			if (!companyRepository.exists(companyId)) {
				throw new EntityNotFoundException(
						String.format(messageService.getMessage("invalid.company.code"), companyId));
			}
			Object[][] records = companyRepository.findCompanyUserCount(companyId);
			return new EmployeeCountDTO((Long) records[0][0], (Long) records[0][1], (Long) records[0][2]);
		} else {
			throw new AccessDeniedException(messageService.getMessage("error.accessdenied"));
		}
	}

	@Override
	protected CompanyRepository getRepository() {
		return (CompanyRepository) this.repository;
	}

	public boolean findIsAutomatedCheckOutsOn() {
		return this.getRepository().findIsAutomatedCheckOutsOn(getCurrentCompany().getId());
	}
}
