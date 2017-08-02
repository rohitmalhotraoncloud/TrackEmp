package com.os.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.os.app.entity.UserDevice;

public interface UserDeviceRepository extends CrudRepository<UserDevice, Long> {

	/**
	 * Return records where owner property matches with ownerId parameter
	 */
	@Query("from UserDevice u where u.owner.id =?1")
	public List<UserDevice> findUserDeviceByOwnerId(Long ownerId);

	/**
	 * Returns record where owner and uniqueIdentifier properties matches with
	 * ownerId, uniqueIdentifier parameters
	 */
	@Query("from UserDevice u where u.owner.id =?1 and u.uniqueIdentifier=?2")
	public UserDevice getUserDeviceExistsByOwnerIdAndUniqueIdentifier(
			Long ownerId, String uniqueIdentifier);

	/**
	 * Return records where owner property and owner's company's id match with
	 * ownerId and companyId parameters
	 */
	@Query("from UserDevice u where u.owner.id =?1 and u.owner.company.id=?2")
	public List<UserDevice> findUserDeviceByOwnerIdAndCompanyId(Long ownerId,
			Long companyId);
}
