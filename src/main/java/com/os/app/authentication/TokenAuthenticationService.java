package com.os.app.authentication;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.os.app.beans.ErrorResponse;
import com.os.app.beans.Token;
import com.os.app.entity.SystemUser;
import com.os.app.enums.ErrorKeys;
import com.os.app.service.MessageByLocaleService;

@Service
public class TokenAuthenticationService {

	private TokenHandler tokenHandler;

	public static final ObjectMapper mapper = new ObjectMapper();

	@Autowired
	public TokenAuthenticationService(TokenHandler tokenHandler) {
		this.tokenHandler = tokenHandler;
	}

	@Autowired
	MessageByLocaleService messageService;

	public void addAuthentication(HttpServletResponse response, UserAuthentication authentication)
			throws JsonProcessingException, IOException {

		final SystemUser user = authentication.getDetails();

		this.setUserValues(response, tokenHandler.createTokenForUser(user));

	}

	public Authentication getAuthentication(HttpServletRequest request) {

		String token = request.getHeader("Authorization");
		if (token != null) {
			token = token.length() > 7 ? token.substring(7) : token;
			SystemUser user = getUserFromToken(token);
			if (user != null) {
				return new UserAuthentication(user);
			}
		} else {
			throw new AuthenticationServiceException(new ErrorResponse(ErrorKeys.TOKEN_MISSING.name(),
					messageService.getMessage("token.notfound.message"), null).toString());
		}

		return null;
	}

	public SystemUser getUserFromToken(String token) {
		try {
			return tokenHandler.parseUserFromToken(token);
		} catch (Exception ex) {
			throw new AuthenticationServiceException(new ErrorResponse(ErrorKeys.TOKEN_INVALID_OR_EXPIRED.name(),
					messageService.getMessage("token.error.message"), null).toString());

		}
	}

	public void setUserValues(HttpServletResponse response, String token) throws JsonProcessingException, IOException {
		String responseAsString = new Token(token).toString();
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setContentLength(responseAsString.getBytes().length);
		response.getOutputStream().write(responseAsString.getBytes());
	}

}
