package com.os.app.dto;

import javax.validation.constraints.NotNull;

import com.os.app.annotation.FieldMatch;

@FieldMatch.List({
		@FieldMatch(first = "password", second = "repeatPassword", message = "The password fields must match") })
public class PasswordResetTokenDTO {

	@NotNull
	private String token;

	@NotNull
	private String password;

	private String repeatPassword;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRepeatPassword() {
		return repeatPassword;
	}

	public void setRepeatPassword(String repeatPassword) {
		this.repeatPassword = repeatPassword;
	}

}
