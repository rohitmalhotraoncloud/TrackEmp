package com.os.app.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.os.app.entity.Company;
import com.os.app.entity.CompanyAwareEntity;

@NoRepositoryBean
public interface CompanyAwareRepository<Entity extends CompanyAwareEntity<ID>, ID extends Serializable>
		extends CrudRepository<Entity, ID> {

	public Entity findByCompanyAndId(Company company, ID id);

	public List<Entity> findByCompany(Company company);

}
