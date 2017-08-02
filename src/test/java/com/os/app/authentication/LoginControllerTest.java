package com.os.app.authentication;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;

import com.os.Application;
import com.os.BaseTest;
import com.os.app.enums.Role;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestPropertySource(inheritLocations = true, locations = "classpath:/test.properties")
@SuppressWarnings("javadoc")
public class LoginControllerTest extends BaseTest {

	@Test
	public void whenLoggingInUsingIncorrectCredentialsThenReturnUnauthorized()
			throws Exception {
		this.setupUser(Role.SYSTEMADMIN);
		Assert.assertEquals(1, this.getAllSystemUsers().size());
		this.mockMvc
				.perform(
						post("/api/auth/login")
								.content(
										"{   \"username\": \"username\",   \"password\": \"password\",   \"subdomain\": \""
												+ SUB_DOMAIN + "\" }"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void whenLoggingInAsAdminThenReturnTheTokenWithEncodedData()
			throws Exception {
		this.setupUser(Role.SYSTEMADMIN);
		Assert.assertEquals(1, this.getAllSystemUsers().size());
		MvcResult mvcResult = this.mockMvc
				.perform(
						post("/api/auth/login").content(
								"{   \"username\": \"" + USER_EMAIL
										+ "\",   \"password\": \"" + PASSWORD
										+ "\",   \"subdomain\": \""
										+ SUB_DOMAIN + "\" }"))
				.andExpect(status().isOk()).andReturn();
		String headerValue = mvcResult.getResponse().getHeader(
				"Access-Control-Allow-Origin");
		assertEquals("*", headerValue);
	}
}
