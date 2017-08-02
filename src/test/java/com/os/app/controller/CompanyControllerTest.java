package com.os.app.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.os.BaseTest;
import com.os.app.entity.Company;
import com.os.app.entity.SystemUser;
import com.os.app.enums.Role;

@RunWith(SpringJUnit4ClassRunner.class)
public class CompanyControllerTest extends BaseTest {

	@Test
	public void whenGettingCompanyThenReturnWithStatusOK() throws Exception {
		SystemUser systemAdmin = this.setupUser(Role.SYSTEMADMIN);

		this.mockMvc.perform(get("/api/company").with(this.addTokenAsUser(systemAdmin))).andExpect(status().isOk())
				.andExpect(jsonPath("$").exists()).andExpect(jsonPath("$.companies").isArray())
				.andExpect(jsonPath("$.companies[0]").exists())
				.andExpect(jsonPath("$.companies[0].subDomainName").value(SUB_DOMAIN))
				.andExpect(jsonPath("$.companies[0].hasCrmAddon").value(true))
				.andExpect(jsonPath("$.companies[0].tripsDailyCutoffTime").exists());
	}

	@Test
	public void whenCreatingCompanyWithInCompleteDataThenReturn422() throws Exception {
		SystemUser systemAdmin = this.setupUser(Role.SYSTEMADMIN);

		int initialCountOfCompanies = this.getAllCompanies().size();
		String subDomainName = "subDomainName";
		String content = "{\"subDomainName\":\"" + subDomainName + "\", \"companyName\":\"\","
				+ "\"website\":\"www.amigolabs.sg\"," + "\"email\":\"garreth@amigolabs.sg\","
				+ "\"country\":\"Singapore\", \"fax\":\"123123123\", \"postalCode\":\"11122233\", \"phone\":\"123123\", \"address\":\"6 Vista Terrace\" }";
		this.mockMvc.perform(post("/api/company").with(this.addTokenAsUser(systemAdmin)).content(content))
				.andExpect(status().isBadRequest());

		// With Blank Subdomain
		content = "{\"subDomainName\":\"\", \"companyName\":\"companyName\"," + "\"website\":\"www.amigolabs.sg\","
				+ "\"email\":\"garreth@amigolabs.sg\","
				+ "\"country\":\"Singapore\", \"fax\":\"123123123\", \"postalCode\":\"11122233\", \"phone\":\"123123\", \"address\":\"6 Vista Terrace\" }";
		this.mockMvc.perform(post("/api/company").with(this.addTokenAsUser(systemAdmin)).content(content))
				.andExpect(status().isBadRequest());

		// With sysadmin Subdomain
		content = "{\"subDomainName\":\"sysadmin\", \"companyName\":\"companyName\","
				+ "\"website\":\"www.amigolabs.sg\"," + "\"email\":\"garreth@amigolabs.sg\","
				+ "\"country\":\"Singapore\", \"fax\":\"123123123\", \"postalCode\":\"11122233\", \"phone\":\"123123\", \"address\":\"6 Vista Terrace\" }";
		this.mockMvc.perform(post("/api/company").with(this.addTokenAsUser(systemAdmin)).content(content))
				.andExpect(status().isUnprocessableEntity());

		assertEquals(initialCountOfCompanies, this.getAllCompanies().size());
	}

	@Test
	public void whenCreatingCompanyThenReturn200() throws Exception {
		SystemUser systemAdmin = this.setupUser(Role.SYSTEMADMIN);
		assertEquals(1, this.getAllCompanies().size());

		String subDomainName = "subDomainName";
		String content = "{\"subDomainName\":\"" + subDomainName + "\", \"companyName\":\"TestCompany1\","
				+ "\"website\":\"www.amigolabs.sg\"," + "\"email\":\"garreth@amigolabs.sg\","
				+ "\"country\":\"Singapore\", \"fax\":\"123123123\", \"postalCode\":\"11122233\", \"phone\":\"123123\", \"address\":\"6 Vista Terrace\" }";
		this.mockMvc.perform(post("/api/company").with(this.addTokenAsUser(systemAdmin)).content(content))
				.andExpect(status().isOk()).andExpect(jsonPath("$.company.id").exists())
				.andExpect(jsonPath("$.company.companyName").value("TestCompany1"))
				.andExpect(jsonPath("$.company.email").value("garreth@amigolabs.sg"));
		assertEquals(2, this.getAllCompanies().size());
		Company company = this.companyRepository.findBySubDomainNameIgnoreCase(subDomainName);
		assertNotNull(company);
		assertEquals("TestCompany1", company.getCompanyName());
	}

	@Test
	public void whenUpdatingTheCompanyAsACompanyAdminThenReturnOK() throws Exception {
		SystemUser user = this.setupUser("SOME_ADMIN", Role.COMPANYADMIN, "SOMECOMPANY");
		Company company = this.companyRepository.findOne(user.getCompany().getId());
		company.setAccountOwner(user);
		this.companyRepository.save(company);

		this.mockMvc.perform(put("/api/company/" + user.getCompany().getId()).with(this.addTokenAsUser(user))
				.content("{\"company\": {\"companyName\": \"newName\"}}")).andExpect(status().isOk());
		company = this.companyRepository.findOne(user.getCompany().getId());
		assertEquals("newName", company.getCompanyName());
	}

}
