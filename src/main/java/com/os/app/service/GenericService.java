package com.os.app.service;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import com.os.app.entity.BaseEntity;
import com.os.app.enums.ErrorKeys;
import com.os.app.exception.EmpTrackerException;
import com.os.app.utils.NullAwareBeanUtil;

public abstract class GenericService<T extends BaseEntity<ID>, ID extends Serializable> extends CommonService {

	@Autowired
	CrudRepository<T, ID> repository;

	@Autowired
	MessageByLocaleService messageService;

	public T create(@Valid T t) {
		validateEntity(t);
		if (null != this.exists(t)) {
			throw new EmpTrackerException(ErrorKeys.ENTITY_ALREADY_EXISTS);
		}
		return repository.save(t);
	}

	public void delete(ID id) {
		this.findById(id);
		repository.delete(id);
	}

	public T exists(T t) {
		if (null != t.getId()) {
			return this.findById(t.getId());
		}
		return null;
	}

	public T findById(ID id) {
		return throwEntityNotFoundIfNull(repository.findOne(id));
	}

	protected CrudRepository<T, ID> getRepository() {
		return this.repository;
	}

	public T patch(ID id, T t) {
		T entity = this.findById(id);

		try {
			NullAwareBeanUtil.getInstance().copyProperties(entity, t);
		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.error("Error while copying the properties over", e);
			throw new EmpTrackerException(ErrorKeys.UNPROCESSABLE_ENTITY);
		}
		validateEntity(entity);
		return this.repository.save(entity);
	}

	protected T validateEntity(T entity) {
		Set<ConstraintViolation<T>> constraintViolations = validator.validate(entity);
		if (constraintViolations.size() != 0) {
			logger.warn("Validation failed for the entity " + entity + " with the following errors "
					+ constraintViolations);
			throw new EmpTrackerException(ErrorKeys.UNPROCESSABLE_ENTITY);
		}
		return entity;
	}

	public Iterable<T> filter(T t) {
		return this.repository.findAll();
	}

	protected T throwEntityNotFoundIfNull(T t) {
		throwIfNull(t);
		return t;
	}
}
