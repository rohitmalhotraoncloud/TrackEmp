package com.os.app.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;
import com.os.app.entity.SystemUser;
import com.os.app.enums.Role;
import com.os.app.repository.SystemUserRepository;
import com.os.app.utils.AWSUtil;
import com.os.app.utils.Constants;

/**
 * Service class contains all methods required to create/retrieve/update/delete
 * SystemUser
 */
@Service
public class SystemUserService extends GenericService<SystemUser, Long> {

	@Autowired
	private SystemUserRepository userRepository;

	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Autowired
	AWSUtil awsUtil;

	/**
	 * Method to create a new SYSTEMADMIN. Argument user is SystemUser object
	 * that we are going to save into database
	 */
	public SystemUser registerAUser(SystemUser user) {
		user.setRole(Role.SYSTEMADMIN);
		user.setPassword(encodePassword(user.getPassword()));
		return saveEntity(user);
	}

	/**
	 * Method to encode password. We are using here BCryptPasswordEncoder to
	 * encode password. You can see bean in
	 * com.ctms.app.config.WebSecurityConfig class
	 */
	public String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	/**
	 * Method returns all users for specific company. Argument id is CompanyId
	 */
	public List<SystemUser> findByCompanyId(Long id) {
		return userRepository.findByCompany_Id(id);
	}

	/**
	 * Simple method to save user Entity into database
	 */
	public SystemUser saveEntity(SystemUser user) {
		return userRepository.save(user);
	}

	/**
	 * Method to return all SystemUsers. This method is only accessible by
	 * SYSTEMADMIN
	 */
	@Override
	public List<SystemUser> filter(SystemUser user) {
		return Lists.newArrayList(userRepository.findAll());
	}

	/**
	 * Returns a user based on id and companyId
	 */
	public SystemUser findByCompanyIdAndId(Long companyid, Long id) {
		return userRepository.findByCompanyIdAndId(companyid, id);
	}

	/**
	 * Returns a user based on username. This method is deprecated. As
	 * SystemUser table can contains multiple users with same username but for
	 * different companies. A new method is added in this class which returns
	 * SystemUser based on username and domainname
	 */
	public SystemUser getUserByUserName(String username) {
		List<SystemUser> userList = userRepository.findByUsername(username);
		if (userList.size() == 0) {
			return null;
		}
		return userList.get(0);
	}

	/**
	 * Method returns a SystemUser object based on email and subdomain. Here
	 * Constants.SYSTEMADMIN_DOMAIN is fixed domain name for SYSTEMADMIN. A
	 * SYSTEMADMIN is not associated with any company. So when Searching record
	 * for Constants.SYSTEMADMIN_DOMAIN, We need to search only records those
	 * are not associated with any company
	 */
	public SystemUser getUserByEmailAndSubDomain(String email, String subDomain) {
		if (subDomain.equals(Constants.SYSTEMADMIN_DOMAIN)) {
			SystemUser user = userRepository.findOneByEmailAndNullCompanyId(email);
			if (user == null) {
				return null;
			}
			user.setSubdomain(Constants.SYSTEMADMIN_DOMAIN);
			return user;
		} else {
			return userRepository.findOneByEmailAndSubDomain(email, subDomain);
		}
	}

	/**
	 * Method returns a SystemUser object based on username and subdomain. Here
	 * Constants.SYSTEMADMIN_DOMAIN is fixed domain name for SYSTEMADMIN. A
	 * SYSTEMADMIN is not associated with any company. So when Searching record
	 * for SYSTEMADMIN, We need to search only records those are not associated
	 * with any company
	 */
	public SystemUser findUserByUsernameAndSubDomain(String userName, String subDomain) {
		if (Constants.SYSTEMADMIN_DOMAIN.equals(subDomain)) {
			SystemUser user = userRepository.findOneByUsernameAndNullCompanyId(userName);
			if (user == null) {
				return null;
			}
			user.setSubdomain(Constants.SYSTEMADMIN_DOMAIN);
			return user;
		} else {
			SystemUser user = userRepository.findOneByUsernameAndSubDomain(userName, subDomain);
			if (user != null) {
				user.setSubdomain(subDomain);
			}
			return user;
		}
	}

	public List<SystemUser> findSystemUserAvailableForTripByCompany(Long companyId) {
		return userRepository.findAvailableForTripByCompany(companyId);
	}

	public List<Long> findSystemUserAvailableForTripByCompanySystemUsersAndTrip(Long companyId, Long[] empIds,
			Long tripId) {
		return userRepository.findAvailableForTripByCompanySystemUsersAndTrip(companyId, empIds, tripId);
	}

	public List<Long> findSystemUserIdsBelongToCompany(Long companyId, Long[] ids) {
		return userRepository.findIdsBelongToCompany(companyId, ids);
	}

}