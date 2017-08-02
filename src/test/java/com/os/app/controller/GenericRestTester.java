package com.os.app.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.Serializable;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import com.google.common.collect.Lists;
import com.os.BaseTest;
import com.os.app.entity.BaseEntity;
import com.os.app.entity.SystemUser;
import com.os.app.enums.Role;

public abstract class GenericRestTester<T extends BaseEntity<ID>, ID extends Serializable> extends BaseTest {

	@Autowired
	public CrudRepository<T, ID> repository;

	@Override
	@Before
	public void before() {
		super.before();
		this.repository.deleteAll();
	}

	@Override
	@After
	public void after() {
		this.repository.deleteAll();
		super.after();
	}

	protected List<T> getAllEntriesFromDB() {
		return Lists.newArrayList(this.repository.findAll());
	}

	@Test
	public void whenRequestingWithoutSecurityThenReturnNotAuthorised() throws Exception {
		this.mockMvc.perform(get("/api/" + this.getRequestApiKey() + "/123")).andExpect(status().isUnauthorized());
	}

	public abstract String getRequestApiKey();

	@Test
	public void whenGettingNonExistingEntityThenReturnNotFound() throws Exception {
		SystemUser companyAdmin = this.setupUser(Role.COMPANYADMIN);
		this.mockMvc.perform(get("/api/" + this.getRequestApiKey() + "/123").with(this.addTokenAsUser(companyAdmin)))
				.andExpect(status().isNotFound());
	}

}
