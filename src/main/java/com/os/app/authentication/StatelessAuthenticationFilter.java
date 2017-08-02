package com.os.app.authentication;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class StatelessAuthenticationFilter extends GenericFilterBean {

	private final TokenAuthenticationService tokenAuthenticationService;
	private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

	public StatelessAuthenticationFilter(TokenAuthenticationService taService,
			RestAuthenticationEntryPoint restAuthenticationEntryPoint) {
		this.tokenAuthenticationService = taService;
		this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) req;
		HttpServletResponse httpResponse = (HttpServletResponse) res;
		try {
			SecurityContextHolder.getContext().setAuthentication(
					tokenAuthenticationService
							.getAuthentication((HttpServletRequest) req));
			chain.doFilter(req, res); // always continue
		} catch (Exception ex) {
			SecurityContextHolder.clearContext();
			restAuthenticationEntryPoint.commence(httpRequest, httpResponse,
					new InsufficientAuthenticationException(ex.getMessage()));
		}
	}
}
