package com.os.app.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.os.app.beans.ErrorResponse;
import com.os.app.entity.SystemUser;
import com.os.app.enums.ErrorKeys;
import com.os.app.exception.UserSubscriptionExpireException;

@Service
public class UserAuthenticationService implements UserDetailsService {

	@Autowired
	private SystemUserService userService;

	@Autowired
	MessageByLocaleService messageService;

	/**
	 * Returns a user based on username. This method is deprecated. As
	 * SystemUser table can contains multiple users with same username but for
	 * different companies. A new method is added in this class which returns
	 * SystemUser based on username and domainname
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return loadUserByUsernameAndSubdomain(username, null);
	}

	/**
	 * Method returns a SystemUser object based on username and subdomain. It
	 * throws an exception when no SystemUser object found based on username and
	 * subdomain.
	 */

	public UserDetails loadUserByUsernameAndSubdomain(String username, String domain) throws UsernameNotFoundException {
		SystemUser userDetail = userService.findUserByUsernameAndSubDomain(username, domain);

		if (null == userDetail) {
			throw new UsernameNotFoundException(ErrorKeys.INVALID_CREDENTIALS.name());
		}

		return userDetail;
	}
}
