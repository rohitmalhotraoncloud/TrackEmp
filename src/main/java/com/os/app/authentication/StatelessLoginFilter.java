package com.os.app.authentication;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.os.app.entity.SystemUser;
import com.os.app.service.MessageByLocaleService;
import com.os.app.service.UserAuthenticationService;

public class StatelessLoginFilter extends AbstractAuthenticationProcessingFilter {

	MessageByLocaleService messageService;

	private final TokenAuthenticationService tokenAuthenticationService;

	private final UserAuthenticationService userAuthService;

	public StatelessLoginFilter(String urlMapping, TokenAuthenticationService tokenAuthenticationService,
			UserAuthenticationService userAuthService, AuthenticationManager authManager,
			RestAuthenticationFailureHandler failureHandler, MessageByLocaleService messageService) {

		super(new AntPathRequestMatcher(urlMapping));

		this.messageService = messageService;
		this.userAuthService = userAuthService;
		this.setAuthenticationFailureHandler(failureHandler);
		this.tokenAuthenticationService = tokenAuthenticationService;
		setAuthenticationManager(authManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		SystemUser user = null;
		try {
			user = TokenAuthenticationService.mapper.readValue(request.getReader(), SystemUser.class);
		} catch (Exception ex) {
			throw new AuthenticationCredentialsNotFoundException(messageService.getMessage("credential.error"));
		}

		if (user != null) {
			if (user.getPassword() == null) {
				throw new AuthenticationCredentialsNotFoundException(messageService.getMessage("password.error"));
			}
			if (user.getUsername() == null) {
				throw new AuthenticationCredentialsNotFoundException(messageService.getMessage("user.error"));
			}

			if (user.getSubdomain() == null) {
				throw new AuthenticationCredentialsNotFoundException(messageService.getMessage("subdomain.error"));
			}
		}

		final UsernamePasswordSubdomainAuthenticationToken loginToken = new UsernamePasswordSubdomainAuthenticationToken(
				user.getUsername(), user.getPassword(), user.getSubdomain());

		return getAuthenticationManager().authenticate(loginToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authentication) throws IOException, ServletException {

		// Lookup the complete User object from the database and create an
		// Authentication for it
		UsernamePasswordSubdomainAuthenticationToken authenticationObj = (UsernamePasswordSubdomainAuthenticationToken) authentication;
		final SystemUser authenticatedUser = (SystemUser) userAuthService
				.loadUserByUsernameAndSubdomain(authenticationObj.getName(), authenticationObj.getSubDomainName());

		final UserAuthentication userAuthentication = new UserAuthentication(authenticatedUser);

		// Add the custom token as HTTP header to the response
		tokenAuthenticationService.addAuthentication(response, userAuthentication);

		// Add the authentication to the Security context
		SecurityContextHolder.getContext().setAuthentication(userAuthentication);
	}
}