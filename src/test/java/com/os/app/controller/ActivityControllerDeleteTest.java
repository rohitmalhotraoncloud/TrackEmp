package com.os.app.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.os.BaseTest;
import com.os.app.entity.SystemUser;
import com.os.app.enums.Role;

@RunWith(SpringJUnit4ClassRunner.class)
public class ActivityControllerDeleteTest extends BaseTest {

	@Test
	public void whenDeletingActivityWithoutAnyAuthorizationThenReturn401() throws Exception {
		this.mockMvc.perform(delete("/api/activity/123")).andExpect(status().isUnauthorized());
	}

	@Test
	public void whenDeletingNonExistingActivityThenReturnNotFound() throws Exception {
		SystemUser user = this.setupUser(Role.STAFF);
		assertEquals(0, this.getAllActivities().size());
		this.mockMvc.perform(delete("/api/activity/123").with(addTokenAsUser(user))).andExpect(status().isNotFound());
	}

}
