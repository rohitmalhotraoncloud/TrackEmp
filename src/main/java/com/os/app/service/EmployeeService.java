package com.os.app.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.os.app.beans.ErrorResponse;
import com.os.app.dto.EmployeeDTO;
import com.os.app.entity.Company;
import com.os.app.entity.SystemUser;
import com.os.app.enums.ErrorKeys;
import com.os.app.enums.ImageType;
import com.os.app.enums.Role;
import com.os.app.exception.EmpTrackerException;
import com.os.app.repository.SystemUserRepository;
import com.os.app.utils.AWSUtil;
import com.os.app.utils.Constants;

@Service
public class EmployeeService extends GenericService<SystemUser, Long> {

	@Autowired
	SystemUserService systemUserService;
	@Autowired
	CompanyService companyService;

	@Autowired
	SystemUserRepository systemUserRepository;

	@Autowired
	AWSUtil awsUtil;

	/**
	 * Method to retrieve image from AWS bucket. Parameter id is id of
	 * SystemUser whose image we are going to retrieve. Parameter loggedInUser
	 * is user who is accessing this method
	 */
	public byte[] retrieveImage(Long id) {
		try {
			/*
			 * Getting system user based on id
			 */
			SystemUser systemUser = systemUserService.findById(id);
			SystemUser loggedInUser = getCurrentUser();

			/*
			 * Checking if loggedIn user has role STAFF
			 */
			if (isCurrentUserAStaff()) {
				/*
				 * If current user is STAFF he can only retrieve his pic. Not
				 * any other user's pic. Otherwise it throw exception.
				 */
				if (!id.equals(loggedInUser.getId())) {
					throw new EmpTrackerException(ErrorKeys.IMAGE_NOT_AVAILABLE);
				}
			}
			/*
			 * If current user is COMPANYADMIN he can only retrieve pics of all
			 * employees of his company. Otherwise it throw exception.
			 */
			else if (isCurrentUserAManagerOrCompanyAdmin()) {
				if (systemUser.getCompany().getId() != loggedInUser.getCompany().getId()) {
					throw new EmpTrackerException(ErrorKeys.IMAGE_NOT_AVAILABLE);
				}
			}
			return IOUtils.toByteArray(awsUtil.s3Download(awsUtil.getImagePathInAWSBucket(ImageType.EMPLOYEE,
					new Long[] { systemUser.getCompany().getId(), systemUser.getId() })));
		} catch (IOException ex) {
			throw new EmpTrackerException(ErrorKeys.IMAGE_NOT_AVAILABLE);
		}
	}

	/**
	 * Method to Create new CompanyAdmin. Parameter is systemUser that we are
	 * going to save.
	 */
	public SystemUser createCompanyAdmin(SystemUser companyAdmin) {
		if (null == companyAdmin.getCompany()) {
			throw new EmpTrackerException(ErrorKeys.UNPROCESSABLE_ENTITY, "company cannot be null");
		}
		Company company = companyService.findById(companyAdmin.getCompany().getId());
		companyAdmin.setUsername(companyAdmin.getEmail());
		/*
		 * Setting role COMPANYADMIN
		 */
		companyAdmin.setRole(Role.COMPANYADMIN);
		companyAdmin.setPassword(systemUserService.encodePassword(companyAdmin.getPassword()));
		company.setAccountOwner(companyAdmin);
		companyAdmin = systemUserService.saveEntity(companyAdmin);
		companyAdmin.setCompany(company);
		return companyAdmin;
	}

	/**
	 * Method to create Employee. Here we are passing two parameters. (1)
	 * SystemUser, user who is accessing this method.(2) CompanyEmployee, that
	 * we are going to save
	 */
	public EmployeeDTO createCompanyEmployee(SystemUser loggedInUser, SystemUser companyEmployee) {
		/*
		 * Because only company admin can create company employee. So he should
		 * have companyid. If logged in user does not have company id, that
		 * means he cannot create an Employee
		 */
		if (loggedInUser.getCompany() == null) {
			throw new AccessDeniedException(messageService.getMessage("error.accessdenied"));
		}

		companyEmployee.setCompany(loggedInUser.getCompany());
		companyEmployee.setUsername(companyEmployee.getEmail());
		/*
		 * Setting STAFF role for employee
		 */
		companyEmployee.setRole(Role.STAFF);
		companyEmployee.setPassword(systemUserService.encodePassword(companyEmployee.getPassword()));

		validatePic(companyEmployee.getProfilePhoto());
		companyEmployee = systemUserService.saveEntity(companyEmployee);
		/*
		 * Saving employee pic after saving employee record
		 */
		savePic(companyEmployee);

		return new EmployeeDTO(systemUserService.saveEntity(companyEmployee), awsUtil.getCurrentUrl());
	}

	/**
	 * Method to save Pic on AWS bucket. Here employee parameter is SystemUser
	 * object whom pic we are going to store in AWS. In this class we have to
	 * savePic methods. This method is called when creating new employee
	 */
	private void savePic(SystemUser employee) {
		/*
		 * Checking is employee has profilePic
		 */
		if (employee.getProfilePhoto() != null) {
			awsUtil.saveImage(ImageType.EMPLOYEE, new Long[] { employee.getCompany().getId(), employee.getId() },
					employee.getProfilePhoto());
			/*
			 * Setting profilePhoto property with custom url
			 */
			employee.setProfilePhoto(
					String.format(Constants.SYSTEMUSER_IMAGE_PATH, awsUtil.getCurrentUrl(), employee.getId()));
		}

	}

	/**
	 * Method to save Pic on AWS bucket. Here employee parameter is SystemUser
	 * object whose pic we are going to store in AWS. parameter employeeEntity
	 * is an existing record of employee. In this class we have to savePic
	 * methods. This method is called when updating an employee.
	 */
	private void savePic(SystemUser employeeEntity, SystemUser employee) {
		if (employee.getProfilePhoto() != null) {
			awsUtil.saveImage(ImageType.EMPLOYEE,
					new Long[] { employeeEntity.getCompany().getId(), employeeEntity.getId() },
					employee.getProfilePhoto());
			employeeEntity.setProfilePhoto(
					String.format(Constants.SYSTEMUSER_IMAGE_PATH, awsUtil.getCurrentUrl(), employeeEntity.getId()));
		}
	}

	/**
	 * return List of EmployeeDTO for specific companyId.
	 */
	public List<EmployeeDTO> getEmployees(Long companyId) {
		List<SystemUser> users = systemUserService.findByCompanyId(companyId);
		List<EmployeeDTO> employees = new ArrayList<EmployeeDTO>();
		String currentUrl = awsUtil.getCurrentUrl();

		for (SystemUser u : users) {
			employees.add(new EmployeeDTO(u, currentUrl));
		}
		return employees;
	}

	/**
	 * return EmployeeDTO based on id. here loggedInUser is user who is
	 * accessing this method.
	 */
	public EmployeeDTO findEmployeeById(SystemUser loggedInUser, Long id) {
		SystemUser user = findById(id);
		return new EmployeeDTO(user, awsUtil.getCurrentUrl());
	}

	@Override
	public SystemUser findById(Long id) {
		SystemUser user = null;
		SystemUser currentUser = getCurrentUser();

		if (isCurrentUserSysAdmin()) {
			user = systemUserService.findById(id);
		} else if (isCurrentUserAManagerOrCompanyAdmin()) {
			user = systemUserService.findByCompanyIdAndId(currentUser.getCompany().getId(), id);
		} else if (currentUser.getId().equals(id)) {
			user = this.findById(id);
		}
		if (user == null) {
			throw new EntityNotFoundException();
		}
		return user;
	}

	/**
	 * Method to create an employee. Parameter loggedInUser is user who is
	 * accessing this method. Parameter emp is an object of EmployeeDTO.
	 * EmployeeDTO is a wrapper object of employee, that we are going to save
	 * into database
	 */
	public EmployeeDTO createEmployee(EmployeeDTO emp) {

		SystemUser loggedInUser = getCurrentUser();

		SystemUser employeeToSave = emp.toSystemUser();

		if (!isCurrentUserCompanyAdmin()) {
			throw new AccessDeniedException(messageService.getMessage("error.accessdenied"));
		}
		/*
		 * Encoding password
		 */
		employeeToSave.setPassword(systemUserService.encodePassword(employeeToSave.getPassword()));
		employeeToSave.setCompany(loggedInUser.getCompany());
		employeeToSave.setRole(emp.getRole());

		employeeToSave.setCompany(loggedInUser.getCompany());

		employeeToSave.setId(null);
		/*
		 * Validating profilePhoto property of this object
		 */
		validatePic(employeeToSave.getProfilePhoto());
		SystemUser empentity = systemUserService.saveEntity(employeeToSave);
		/*
		 * Saving pic into AWS bucket
		 */
		savePic(empentity);
		return new EmployeeDTO(empentity, awsUtil.getCurrentUrl());
	}

	public EmployeeDTO enableEmployee(Long id) {
		SystemUser existingUser = systemUserService.findById(id);
		existingUser.setIsActive(true);
		existingUser.setDisabledByUser(null);
		existingUser.setDisabledTimestamp(null);
		return new EmployeeDTO(systemUserService.saveEntity(existingUser), awsUtil.getCurrentUrl());

	}

	public EmployeeDTO updateEmployeePic(Long id, String imageKey) {
		SystemUser existingUser = systemUserService.findById(id);

		if (isCurrentUserAStaff() || isCurrentUserAManagerOrCompanyAdmin() && id.equals(getCurrentUser().getId())) {

			validatePic(imageKey);
			SystemUser tempEmployee = new SystemUser();
			tempEmployee.setProfilePhoto(imageKey);
			savePic(existingUser, tempEmployee);

			return new EmployeeDTO(systemUserService.saveEntity(existingUser), awsUtil.getCurrentUrl());
		} else {
			throw new AccessDeniedException("You don't have authority to update this employee");
		}
	}

	/**
	 * Method to update an employee. Parameter loggedInUser is user who is
	 * accessing this method. Parameter emp is an object of EmployeeDTO.
	 * EmployeeDTO is a wrapper object of employee, that we are going to save
	 * into database. Parameter id is an id of object, that we are going to
	 * modify
	 */
	public EmployeeDTO updateEmployee(Long id, EmployeeDTO emp) {
		SystemUser existingUser = systemUserService.findById(id);
		if (existingUser == null) {
			/*
			 * if systemUser of given id does not exists, it throws an exception
			 */
			throw new AccessDeniedException(messageService.getMessage("error.accessdenied"));
		}

		SystemUser currentUser = getCurrentUser();
		if (isCurrentUserAStaff()) {
			/*
			 * If Current user is a STAFF, he cannot update following properties
			 */

			if (emp.getIsDeleted() != null) {
				throw new AccessDeniedException(messageService.getMessage("employee.isdeleted.update.restriction"));
			}

			if (!currentUser.getId().equals(id)) {
				throw new AccessDeniedException(
						messageService.getMessage("employee.update.otheremployeesrecord.restriction"));
			}

			if (emp.isActive() != null) {
				throw new AccessDeniedException(String.format(
						messageService.getMessage("employee.update.restrictedproperties.restriction"), "isActive"));
			}
			if (emp.getRole() != null) {

				throw new AccessDeniedException(String
						.format(messageService.getMessage("employee.update.restrictedproperties.restriction"), "role"));
			}
			if (emp.getJobType() != null) {
				throw new AccessDeniedException(String.format(
						messageService.getMessage("employee.update.restrictedproperties.restriction"), "jobType"));
			}
			if (emp.isTimesheetUser() != null) {
				throw new AccessDeniedException("employee.update.restrictedproperties.timesheetuser");
			}
		}

		/*
		 * If Current user is an COMPANYADMIN, he should belong to same company
		 * as employee (which is being updated) belongs
		 */
		if (isCurrentUserAManagerOrCompanyAdmin()) {
			if (!getCurrentCompany().getId().equals(existingUser.getCompany())) {
				throw new AccessDeniedException("You Cannot modify users from outside your company");
			}
		}

		/*
		 * Setting Employee's properties
		 */
		SystemUser newEmployee = emp.toSystemUser();
		if (newEmployee.getPassword() != null) {
			existingUser.setPassword(systemUserService.encodePassword(newEmployee.getPassword()));
		}
		if (newEmployee.getUsername() != null) {
			existingUser.setUsername(newEmployee.getUsername());
		}
		if (newEmployee.getFirstName() != null) {
			existingUser.setFirstName(newEmployee.getFirstName());
		}
		if (newEmployee.getLastName() != null) {
			existingUser.setLastName(newEmployee.getLastName());
		}
		if (newEmployee.getMobileNumber() != null) {
			existingUser.setMobileNumber(newEmployee.getMobileNumber());
		}
		if (newEmployee.getGender() != null) {
			existingUser.setGender(newEmployee.getGender());
		}
		if (newEmployee.getEmail() != null) {
			existingUser.setEmail(newEmployee.getEmail());
		}

		if (newEmployee.isTimesheetUser() != null) {
			existingUser.setTimesheetUser(newEmployee.isTimesheetUser());
		}

		existingUser.setRole(newEmployee.getRole());

		if (newEmployee.getIsActive() != null) {
			existingUser.setIsActive(newEmployee.getIsActive());
			if (!newEmployee.getIsActive()) {
				// disabled_by_user
				existingUser.setDisabledTimestamp(new Date());
				existingUser.setDisabledByUser(currentUser);
			}
		}

		if (newEmployee.getIsDeleted() != null) {
			existingUser.setIsDeleted(newEmployee.getIsDeleted());
		}
		existingUser.setCompany(newEmployee.getCompany());
		/*
		 * Validating Employee profilePic
		 */
		validatePic(newEmployee.getProfilePhoto());
		/*
		 * Saving profilePic to AWS bucket
		 */
		savePic(existingUser, newEmployee);

		return new EmployeeDTO(systemUserService.saveEntity(existingUser), awsUtil.getCurrentUrl());
	}

	/**
	 * Checking whether pic parameter contains valid profilePic code or not
	 */
	public void validatePic(String pic) {
		if (pic != null) {
			if (!awsUtil.isPhotoStringValid(pic)) {
				throw new EmpTrackerException(ErrorKeys.INVALID_IMAGE);
			}
		}
	}

	public List<EmployeeDTO> findEmployeeAvailableForTripByCompany(Long companyId) {
		List<SystemUser> systemUserList = systemUserService.findSystemUserAvailableForTripByCompany(companyId);
		List<EmployeeDTO> employeeDTOList = new ArrayList<EmployeeDTO>();
		for (SystemUser systemUser : systemUserList) {
			employeeDTOList.add(new EmployeeDTO(systemUser, awsUtil.getCurrentUrl()));
		}
		return employeeDTOList;
	}

	public List<Long> findEmployeeNotAvailableForTripByCompanyMembersAndTrip(Long companyId, Long[] members,
			Long tripId) {
		List<Long> systemUserIds = systemUserService
				.findSystemUserAvailableForTripByCompanySystemUsersAndTrip(companyId, members, tripId);
		List<Long> allIds = new LinkedList<Long>(Arrays.asList(members));
		allIds.removeAll(systemUserIds);
		return allIds;
	}

	public List<Long> findEmployeeIdsNotBelongToCompany(Long companyId, Long[] members) {
		List<Long> idsBelongToCompany = systemUserService.findSystemUserIdsBelongToCompany(companyId, members);
		List<Long> allIds = new LinkedList<Long>(Arrays.asList(members));
		allIds.removeAll(idsBelongToCompany);
		return allIds;
	}
}