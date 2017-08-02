package com.os.app.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.os.app.authentication.CustomDaoAuthenticationProvider;
import com.os.app.authentication.RestAuthenticationEntryPoint;
import com.os.app.authentication.RestAuthenticationFailureHandler;
import com.os.app.authentication.StatelessAuthenticationFilter;
import com.os.app.authentication.StatelessLoginFilter;
import com.os.app.authentication.TokenAuthenticationService;
import com.os.app.service.MessageByLocaleService;
import com.os.app.service.UserAuthenticationService;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserAuthenticationService userAuthService;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;

	@Autowired
	private SimpleCORSFilter simpleCORSFilter;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint());
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/auth/login", "/api/register", "/api-docs")
				.permitAll().anyRequest().authenticated();
		http.csrf().disable();

		// custom JSON based authentication by POST of
		// {"username":"<name>","password":"<password>"} which sets the token
		// header upon authentication
		StatelessLoginFilter statelessLoginFilter = new StatelessLoginFilter("/api/auth/login",
				tokenAuthenticationService, userAuthService, authenticationManagerBean(),
				new RestAuthenticationFailureHandler(), messageByLocaleService);
		http.addFilterBefore(statelessLoginFilter, UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(this.simpleCORSFilter, StatelessLoginFilter.class);

		// custom Token based authentication based on the header previously
		// given to the client

		http.addFilterBefore(new StatelessAuthenticationFilter(tokenAuthenticationService, authenticationEntryPoint()),
				UsernamePasswordAuthenticationFilter.class);

		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/api/locationUpdate{s*}/{id}/image", "/api/employee/{id}/profilePhoto",
				"/api/register", "/api/crm/**", "/api-docs/**");
	}

	@Bean
	public CustomDaoAuthenticationProvider customDaoAuthenticationProvider() {
		CustomDaoAuthenticationProvider authenticationProvider = new CustomDaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userAuthService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		List<AuthenticationProvider> authenticationProviderList = new ArrayList<AuthenticationProvider>();
		authenticationProviderList.add(customDaoAuthenticationProvider());
		AuthenticationManager authenticationManager = new ProviderManager(authenticationProviderList);
		return authenticationManager;
	}

	@Bean
	public RestAuthenticationEntryPoint authenticationEntryPoint() {
		return new RestAuthenticationEntryPoint();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userAuthService).passwordEncoder(passwordEncoder());
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
