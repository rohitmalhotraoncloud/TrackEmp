package com.os.app.repository;

import static org.springframework.util.StringUtils.hasText;

import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.os.app.annotation.Filterable;
import com.os.app.entity.BaseEntity;
import com.os.app.entity.StaffAwareEntity;
import com.os.app.service.CommonService;

import ch.qos.logback.classic.Logger;

public class CustomRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
		implements CustomRepository<T, ID> {

	public CustomRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager em) {
		super(entityInformation, em);
		this.entityManager = em;
	}

	private EntityManager entityManager;
	private List<Field> availableFields;
	private Logger logger = (Logger) LoggerFactory.getLogger(getClass());

	private int batchSize = 100;

	/**
	 * @param t
	 *            object by which the bean has to be filtered
	 * @return the {@link Iterable} T that matches the input t
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Iterable<T> filter(T t) {

		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = (CriteriaQuery<T>) criteriaBuilder.createQuery(t.getClass());

		List<Predicate> conditions = this.getPredicatesForClass(t, criteriaBuilder, criteriaQuery);

		// With the list of conditions, come up with the final query
		criteriaQuery.where(conditions.toArray(new Predicate[] {}));

		// Execute the query and return the result
		return this.entityManager.createQuery(criteriaQuery).getResultList();
	}

	/**
	 * @param t
	 *            entity based on which filter is to created
	 * @param criteriaBuilder
	 *            {@link CriteriaBuilder} object
	 * @param criteriaQuery
	 *            {@link CriteriaQuery} Query to be used
	 * @return List<Predicate> the list of predicates based on the criteria
	 *         builder
	 */
	private List<Predicate> getPredicatesForClass(T t, CriteriaBuilder criteriaBuilder,
			CriteriaQuery<T> criteriaQuery) {

		// List to be returned
		List<Predicate> returnList = new ArrayList<Predicate>();
		// Create Root class to get field properties
		Root<? extends Object> root = criteriaQuery.from(t.getClass());
		// Setting the property to be accessed
		List<Field> fields = this.getAllFilterableFields(t);
		AccessibleObject.setAccessible(fields.toArray(new Field[] {}), true);

		Predicate condition;
		try {
			for (Field field : fields) {
				Object value;
				try {
					value = PropertyUtils.getSimpleProperty(t, field.getName());
				} catch (NoSuchMethodException noSuchMethodException) {
					this.logger.warn("Obtained " + noSuchMethodException.getMessage() + " when trying to get value for "
							+ field.getName() + " . Ignoring the property");
					continue;
				}

				// If the value is null or the field is not filter-able, do not
				// create the condition
				if (isFilterable(field) && null != value && hasText(value.toString())) {
					// Add the condition based no the field name and the value
					Expression<String> expression = root.get(field.getName());

					if (field.getType() == String.class) {
						expression = criteriaBuilder.lower(expression);
						// create the condition
						condition = criteriaBuilder.equal(expression, value.toString().toLowerCase());
					} else {
						condition = criteriaBuilder.equal(expression, value);
					}

					// Add the condition to the list
					returnList.add(condition);
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			// This condition should never occur ideally
			throw new RuntimeException(e);
		}
		if (t instanceof StaffAwareEntity) {
			if (!CommonService.isCurrentUserSysAdmin()) {
				if (CommonService.isCurrentUserAManagerOrCompanyAdmin()) {
					condition = criteriaBuilder.equal(root.get("staffUser").get("company").get("id"),
							CommonService.getCurrentCompany().getId());
				} else {
					condition = criteriaBuilder.equal(root.get("staffUser").get("id"),
							CommonService.getCurrentUser().getId());
				}
				returnList.add(condition);
			}
		}
		return returnList;
	}

	/**
	 * 
	 * @param t
	 *            entity who has to be scanned
	 * @return the list of fields that can be filtered
	 */
	private List<Field> getAllFilterableFields(T t) {
		if (this.availableFields != null) {
			return this.availableFields;
		}
		this.availableFields = new ArrayList<Field>();
		if (t instanceof BaseEntity) {
			this.availableFields.addAll(Arrays.asList(BaseEntity.class.getDeclaredFields()));
		}

		this.availableFields.addAll(Arrays.asList(t.getClass().getDeclaredFields()));

		Iterator<Field> fieldIterator = this.availableFields.iterator();
		Field field;
		while (fieldIterator.hasNext()) {
			field = fieldIterator.next();
			if (!isAcceptableFieldModifiers(field.getModifiers())) {
				fieldIterator.remove();
			}
		}
		this.logger.info("Obtained " + this.availableFields.size() + " to be fileted on for the class " + t.getClass());
		return this.availableFields;
	}

	/**
	 * @param field
	 *            {@link Field} that has to be checked
	 * @return boolean if the field is annotated with {@link Filterable}
	 *         annotation
	 */
	private static boolean isFilterable(Field field) {
		return field.getAnnotation(Filterable.class) != null;
	}

	/**
	 * @param fieldModifier
	 *            int value stating the field modifier
	 * @return true if neither static nor final
	 */
	private static boolean isAcceptableFieldModifiers(int fieldModifier) {
		// These fields are final or static is not owned by the object (might be
		// owned by the CLASS)
		return !(Modifier.isStatic(fieldModifier) || Modifier.isFinal(fieldModifier));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ge.predix.common.repository.PredixCustomRepository#batchCreate(java.
	 * lang.Iterable)
	 */
	@Override
	public List<T> batchCreate(List<T> entities, int batchSize) {
		final List<T> savedEntities = new ArrayList<T>(entities.size());
		int i = 0;

		for (T t : entities) {
			savedEntities.add(this.persistOrMerge(t));
			i++;
			if (i % batchSize == 0) {
				// Flush a batch of inserts and release memory.
				this.entityManager.flush();
				this.entityManager.clear();
			}
		}
		return savedEntities;
	}

	/**
	 * 
	 * @param t
	 *            entity to be merged
	 * @return the saved entity
	 */
	@SuppressWarnings("unchecked")
	private T persistOrMerge(T t) {
		if (((Persistable<ID>) t).isNew()) {
			this.entityManager.persist(t);
			return t;
		}
		return this.entityManager.merge(t);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ge.predix.common.repository.PredixCustomRepository#batchDeleteAll()
	 */
	@Override
	public void deleteAllInBatch() {
		super.deleteAllInBatch();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ge.predix.common.repository.PredixCustomRepository#batchCreate(java.
	 * util.List)
	 */
	@Override
	public Iterable<T> batchCreate(List<T> ts) {
		return this.batchCreate(ts, this.batchSize);
	}

}
