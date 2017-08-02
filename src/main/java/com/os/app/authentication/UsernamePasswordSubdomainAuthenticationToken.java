package com.os.app.authentication;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class UsernamePasswordSubdomainAuthenticationToken extends UsernamePasswordAuthenticationToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String subDomainName;

	public UsernamePasswordSubdomainAuthenticationToken(Object principal, Object credentials, String subDomainName) {
		super(principal, credentials);
		this.subDomainName = subDomainName;
	}

	public UsernamePasswordSubdomainAuthenticationToken(Object principal, Object credentials, String subDomainName,
			Collection<? extends GrantedAuthority> authoritiesList) {
		super(principal, credentials, authoritiesList);
		this.subDomainName = subDomainName;
	}

	public String getSubDomainName() {
		return subDomainName;
	}

	public void setSubDomainName(String subDomainName) {
		this.subDomainName = subDomainName;
	}
}