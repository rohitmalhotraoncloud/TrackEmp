package com.os.app.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import com.os.app.exception.UserSubscriptionExpireException;
import com.os.app.service.MessageByLocaleService;
import com.os.app.service.UserAuthenticationService;

public class CustomDaoAuthenticationProvider extends DaoAuthenticationProvider {

	@Autowired
	UserAuthenticationService userAuthenticationService;

	@Autowired
	MessageByLocaleService messageService;

	@Override
	public final Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Assert.isInstanceOf(UsernamePasswordSubdomainAuthenticationToken.class, authentication,
				messages.getMessage("AbstractUserDetailsAuthenticationProvider.onlySupports",
						"Only UsernamePasswordAuthenticationToken is supported"));

		UsernamePasswordSubdomainAuthenticationToken authenticationObj = (UsernamePasswordSubdomainAuthenticationToken) authentication;
		// Determine username
		String username = (authenticationObj.getPrincipal() == null) ? "NONE_PROVIDED" : authenticationObj.getName();

		String subDomainName = (authenticationObj.getSubDomainName() == null) ? "NONE_PROVIDED"
				: authenticationObj.getSubDomainName();
		boolean cacheWasUsed = true;
		UserDetails user = getUserCache().getUserFromCache(username);

		if (user == null) {
			cacheWasUsed = false;

			try {
				user = getUser(username, subDomainName, authenticationObj);
			} catch (UsernameNotFoundException notFound) {
				logger.debug("User '" + username + "' not found");

				if (hideUserNotFoundExceptions) {
					throw new BadCredentialsException(messageService.getMessage("invalid.login.detail"));
					// throw new BadCredentialsException(
					// messages.getMessage(
					// "AbstractUserDetailsAuthenticationProvider.badCredentials",
					// "Bad credentials"));
				} else {
					throw notFound;
				}
			} catch (UserSubscriptionExpireException ex) {
				// throw new
				// BadCredentialsException(customException.getErrorResponse().getMessage());
				throw ex;
			}

			Assert.notNull(user, "retrieveUser returned null - a violation of the interface contract");
		}

		try {
			super.getPreAuthenticationChecks().check(user);
			additionalAuthenticationChecks(user, (UsernamePasswordAuthenticationToken) authentication);
		} catch (AuthenticationException exception) {
			if (cacheWasUsed) {
				// There was a problem, so try again after checking
				// we're using latest data (i.e. not from the cache)
				cacheWasUsed = false;
				user = retrieveUser(username, (UsernamePasswordAuthenticationToken) authentication);
				super.getPreAuthenticationChecks().check(user);
				additionalAuthenticationChecks(user, (UsernamePasswordAuthenticationToken) authentication);
			} else {
				if (exception instanceof BadCredentialsException) {
					throw new BadCredentialsException(messageService.getMessage("invalid.login.detail"));
				}
				throw exception;
			}
		}

		super.getPostAuthenticationChecks().check(user);

		Object principalToReturn = user;

		UsernamePasswordSubdomainAuthenticationToken result = new UsernamePasswordSubdomainAuthenticationToken(
				principalToReturn, authenticationObj.getCredentials(), authenticationObj.getSubDomainName(),
				authenticationObj.getAuthorities());
		result.setDetails(authentication.getDetails());
		return result;

	}

	protected final UserDetails getUser(String username, String subDomainName,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		UserDetails loadedUser;

		try {
			loadedUser = userAuthenticationService.loadUserByUsernameAndSubdomain(username, subDomainName);

		} catch (AuthenticationException authenticationException) {
			throw authenticationException;
		} catch (Exception repositoryProblem) {
			throw new InternalAuthenticationServiceException(repositoryProblem.getMessage(), repositoryProblem);
		}

		if (loadedUser == null) {
			throw new InternalAuthenticationServiceException(
					"UserDetailsService returned null, which is an interface contract violation");
		}

		return loadedUser;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordSubdomainAuthenticationToken.class.isAssignableFrom(authentication));
	}
}
