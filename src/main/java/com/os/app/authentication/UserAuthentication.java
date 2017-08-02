package com.os.app.authentication;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.os.app.entity.SystemUser;

public class UserAuthentication implements Authentication {
	
	private static final long serialVersionUID = 8289075016743808493L;
	
	private final SystemUser user;
	
	private boolean authenticated = true;

	public UserAuthentication(SystemUser user) {
		this.user = user;
	}

	@Override
	public String getName() {
		return user.getEmail();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return user.getAuthorities();
	}

	@Override
	public Object getCredentials() {
		return user.getPassword();
	}

	@Override
	public SystemUser getDetails() {
		return user;
	}

	@Override
	public Object getPrincipal() {
		return user.getEmail();
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}
}
