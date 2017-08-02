package com.os.app.controller;

import java.io.Serializable;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.os.app.entity.CompanyAwareEntity;

public abstract class CompanyAwareRestController<Entity extends CompanyAwareEntity<ID>, ID extends Serializable>
		extends GenericRestController<Entity, ID> {

	@Override
	@PreAuthorize("hasAnyAuthority('MANAGER', 'COMPANYADMIN')")
	public Map<String, Entity> create(@RequestBody Map<String, Entity> request) {
		return super.create(request);
	}

	@Override
	@PreAuthorize("hasAnyAuthority('MANAGER', 'COMPANYADMIN')")
	public void delete(@PathVariable ID id) {
		service.delete(id);
	}

	@Override
	@PreAuthorize("hasAnyAuthority('MANAGER', 'COMPANYADMIN')")
	public Map<String, Entity> update(@PathVariable ID id, @RequestBody Map<String, Entity> request) {
		return super.update(id, request);
	}
}
