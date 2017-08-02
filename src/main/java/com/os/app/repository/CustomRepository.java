package com.os.app.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CustomRepository<T, ID extends Serializable> extends CrudRepository<T, ID> {

	/**
	 * Filter.
	 *
	 * @param t
	 *            the object by which the table has to be filtered
	 * @return the iterable objects matching the input object
	 */
	Iterable<T> filter(T t);

	/**
	 * Takes in the batchsize default value as 100.
	 *
	 * @param ts
	 *            list of entities to be created
	 * @return the list of all entities created
	 */
	Iterable<T> batchCreate(List<T> ts);

	/**
	 * Batch create.
	 *
	 * @param ts
	 *            list of entities to be created
	 * @param batchSize
	 *            the size of batch in which it should be persisted
	 * @return the list of all entities created
	 */
	Iterable<T> batchCreate(List<T> ts, int batchSize);

	/**
	 * Delete All the entries from the table.
	 */
	void deleteAllInBatch();

}
