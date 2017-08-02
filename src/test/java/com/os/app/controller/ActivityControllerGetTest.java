package com.os.app.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.os.app.entity.Activity;
import com.os.app.entity.SystemUser;
import com.os.app.enums.Role;

@RunWith(SpringJUnit4ClassRunner.class)
public class ActivityControllerGetTest extends GenericRestTester<Activity, Long> {

	@Test
	public void whenSystemAdminTriesToGetActivityThenReturnUnauthorised() throws Exception {
		SystemUser admin = this.setupUser(Role.SYSTEMADMIN);
		this.mockMvc.perform(get("/api/" + this.getRequestApiKey()).with(this.addTokenAsUser(admin)))
				.andExpect(status().isUnauthorized());
	}

	@Override
	public String getRequestApiKey() {
		return "activity";
	}

}
