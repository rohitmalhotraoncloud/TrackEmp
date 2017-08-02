package com.os.app.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

	SYSTEMADMIN, COMPANYADMIN, MANAGER, STAFF;

	@Override
	public String getAuthority() {
		return this.name();
	}
}
