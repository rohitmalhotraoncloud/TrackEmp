package com.os.app.authentication;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Key;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.os.app.entity.SystemUser;
import com.os.app.utils.Constants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public final class TokenHandler {

	byte[] secretKey;

	public TokenHandler(byte[] secretKey) {
		this.secretKey = secretKey;
	}

	public String refreshToken(String currentToken) {
		SystemUser user = parseUserFromToken(currentToken);
		String newToken = createTokenForUser(user);
		return newToken;
	}

	public SystemUser parseUserFromToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
		return fromJSON(fromBase64(claims.getSubject()));
	}

	public String createTokenForUser(SystemUser user) {
		try {
			SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

			long nowMillis = System.currentTimeMillis();
			Date now = new Date(nowMillis);
			Key signingKey = new SecretKeySpec(secretKey, signatureAlgorithm.getJcaName());

			JwtBuilder builder = Jwts.builder().setIssuedAt(now).setSubject(toBase64(toJSON(user)))
					.signWith(signatureAlgorithm, signingKey);

			long expMillis = nowMillis + Constants.TOKEN_TIMEOUT;
			Date exp = new Date(expMillis);
			builder.setExpiration(exp);

			return builder.compact();
		} catch (Exception ex) {
			return null;
		}

	}

	private SystemUser fromJSON(final byte[] userBytes) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return mapper.readValue(new ByteArrayInputStream(userBytes), SystemUser.class);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private byte[] toJSON(SystemUser user) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return mapper.writeValueAsBytes(user);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}

	private String toBase64(byte[] content) {
		return DatatypeConverter.printBase64Binary(content);
	}

	private byte[] fromBase64(String content) {
		return DatatypeConverter.parseBase64Binary(content);
	}

}