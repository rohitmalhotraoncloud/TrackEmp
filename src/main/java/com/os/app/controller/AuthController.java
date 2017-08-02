package com.os.app.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.os.app.authentication.TokenAuthenticationService;
import com.os.app.authentication.TokenHandler;
import com.os.app.beans.ErrorResponse;
import com.os.app.beans.Token;
import com.os.app.entity.SystemUser;
import com.os.app.enums.ErrorKeys;
import com.os.app.service.EmployeeService;
import com.os.app.service.SystemUserService;

import io.jsonwebtoken.ExpiredJwtException;

@RestController
@RequestMapping(CommonController.API)
public class AuthController extends CommonController {

	@Value("${token.secret}")
	String tokenSecret;

	@Autowired
	SystemUserService systemUserService;

	@Autowired
	EmployeeService employeeService;

	@Autowired
	TokenAuthenticationService tokenAuthenticationService;

	@RequestMapping(value = { "/auth/token-refresh" }, method = RequestMethod.POST)
	@ResponseBody
	public Token tokenRefresh(@RequestBody Token token) {
		TokenHandler tokenHandler = new TokenHandler(DatatypeConverter.parseBase64Binary(tokenSecret));
		return new Token(tokenHandler.refreshToken(token.getToken()));
	}

	@PreAuthorize("hasAuthority('SYSTEMADMIN')")
	@RequestMapping(value = { "/administrator/login" }, method = RequestMethod.POST)
	public ResponseEntity<?> login(HttpServletRequest request, HttpServletResponse response) {
		SystemUser user = null;
		try {
			user = TokenAuthenticationService.mapper.readValue(request.getReader(), SystemUser.class);
		} catch (Exception ex) {
			throw new AuthenticationCredentialsNotFoundException(messageService.getMessage("credential.error"));
		}

		if (user != null) {
			if (user.getUsername() == null) {
				throw new AuthenticationCredentialsNotFoundException(messageService.getMessage("user.error"));
			}

			if (user.getSubdomain() == null) {
				throw new AuthenticationCredentialsNotFoundException(messageService.getMessage("subdomain.error"));
			}
		}

		user = systemUserService.findUserByUsernameAndSubDomain(user.getUsername(), user.getSubdomain());
		if (user == null) {
			throw new AuthenticationCredentialsNotFoundException(messageService.getMessage("invalid.login.detail"));
		}
		TokenHandler tokenHandler = new TokenHandler(DatatypeConverter.parseBase64Binary(tokenSecret));
		return new ResponseEntity<Token>(new Token(tokenHandler.createTokenForUser(user)), HttpStatus.OK);

	}

	@PreAuthorize("hasAuthority('SYSTEMADMIN')")
	@RequestMapping(value = { "/administrator/enableUser/{id}" }, method = RequestMethod.PUT)
	public ResponseEntity<?> enableEmployee(@PathVariable Long id) {
		return new ResponseEntity<Map<String, Object>>(createResponse("employee", employeeService.enableEmployee(id)),
				HttpStatus.OK);

	}

	@ResponseBody
	@ExceptionHandler({ ExpiredJwtException.class })
	public ResponseEntity<ErrorResponse> handleException() {
		return new ResponseEntity<ErrorResponse>(new ErrorResponse(ErrorKeys.TOKEN_INVALID_OR_EXPIRED.name(),
				messageService.getMessage("error.unauthorized"), messageService.getMessage("invalid.token.error")),
				ErrorKeys.TOKEN_INVALID_OR_EXPIRED.getStatus());
	}
}
