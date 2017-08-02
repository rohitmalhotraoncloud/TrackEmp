package com.os.app.controller;

import java.io.Serializable;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.os.app.entity.BaseEntity;
import com.os.app.service.GenericService;

public abstract class GenericRestController<T extends BaseEntity<ID>, ID extends Serializable>
		extends CommonController {

	@Autowired
	GenericService<T, ID> service;

	protected <X> Map<String, X> createResponse(X x) {
		return createResponse(this.getRequesKey(), x);
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(code = HttpStatus.CREATED)
	public Map<String, T> create(@RequestBody Map<String, T> request) {
		return createResponse(service.create(request.get(this.getRequesKey())));
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.PUT, value = "{id}")
	@ResponseStatus(code = HttpStatus.ACCEPTED)
	public Map<String, T> update(@PathVariable ID id, @RequestBody Map<String, T> request) {
		return createResponse(service.patch(id, request.get(this.getRequesKey())));
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.DELETE, value = "{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable ID id) {
		service.delete(id);
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.GET, value = "{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public Map<String, T> getById(@PathVariable ID id) {
		return createResponse(service.findById(id));
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseStatus(code = HttpStatus.OK)
	public Map<String, Iterable<T>> filter(T t) {
		return createResponse(service.filter(t));
	}

	public abstract String getRequesKey();

}
