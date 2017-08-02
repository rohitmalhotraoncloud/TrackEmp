package com.os.app.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.os.BaseTest;
import com.os.app.entity.SystemUser;
import com.os.app.enums.Role;

@RunWith(SpringJUnit4ClassRunner.class)
public class ActivityControllerTest extends BaseTest {

	@Test
	public void whenGettingActivityWithWrongRolesThenReturn401() throws Exception {
		this.mockMvc.perform(get("/api/activity").param("departLocationUpdate", "123"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void whenGettingActivityByDepartLocationUpdateWithWrongRolesThenReturn401() throws Exception {
		this.mockMvc.perform(get("/api/activity").param("departLocationUpdate", "123"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void whenGettingNonExistingActivityByDepartLocationUpdateWithRightRolesThenReturnNotFound()
			throws Exception {
		SystemUser user = this.setupUser(Role.COMPANYADMIN);
		assertEquals(0, this.getAllActivities().size());
		this.mockMvc.perform(get("/api/activity").with(this.addTokenAsUser(user)).param("departLocationUpdate", "123"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void whenGettingActivityAndNoneExistsThenReturnNotFound() throws Exception {
		SystemUser companyadmin = this.setupUser(Role.COMPANYADMIN);
		assertEquals(0, this.getAllActivities().size());
		this.mockMvc.perform(
				get("/api/activity").with(this.addTokenAsUser(companyadmin)).param("arriveLocationUpdate", "123"))
				.andExpect(status().isNotFound());
	}

}
