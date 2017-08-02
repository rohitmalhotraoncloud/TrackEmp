package com.os.app.service;

import java.io.Serializable;

import com.os.app.entity.CompanyAwareEntity;
import com.os.app.repository.CompanyAwareRepository;

public class CompanyAwareService<Entity extends CompanyAwareEntity<ID>, ID extends Serializable>
		extends GenericService<Entity, ID> {

	@Override
	public Entity findById(ID id) {
		if (isCurrentUserSysAdmin()) {
			return super.findById(id);
		}
		Entity entity = this.getRepository().findByCompanyAndId(getCurrentCompany(), id);
		return throwEntityNotFoundIfNull(entity);
	}

	@Override
	public CompanyAwareRepository<Entity, ID> getRepository() {
		return (CompanyAwareRepository<Entity, ID>) this.repository;
	}

}
