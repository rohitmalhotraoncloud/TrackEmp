package com.os.app.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.os.app.exception.UserSubscriptionExpireException;

public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		if (exception.getClass().isAssignableFrom(UserSubscriptionExpireException.class)) {
			response.getWriter().write(exception.toString());
		} else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			String strResponse = "{\"auth_error\":\"@response\"}";
			response.getWriter().write(strResponse.replace("@response", exception.getMessage()));
		}
	}

}
