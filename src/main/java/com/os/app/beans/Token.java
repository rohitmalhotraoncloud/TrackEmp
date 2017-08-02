package com.os.app.beans;

import java.io.Serializable;

public class Token implements Serializable {
	private static final long serialVersionUID = 1L;

	private String token;

	public Token() {
		// TODO Auto-generated constructor stub
	}

	public Token(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "{\"token\":\"" + token + "\"}";
	}

}
