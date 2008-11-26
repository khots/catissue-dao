
package edu.wustl.dao;

import java.util.Collection;

import edu.wustl.common.util.dbmanager.DAOException;

/** This interface defines methods which are specific to Hibernate operations .*/

public interface HibernateDAO extends DAO
{

	/**
	 * Loaded Clean Object.
	 * @param sourceObjectName source Object Name
	 * @param identifier identifier.
	 * @return Object
	 * @throws DAOException Exception.
	 */
	Object loadCleanObj(String sourceObjectName, Long identifier) throws DAOException;

	/**
	 * Add AuditEvent Logs.
	 * @param auditEventDetailsCollection audit Event Details Collection.
	 */
	void addAuditEventLogs(Collection<Object> auditEventDetailsCollection);

	/**
	 * @param objectClass : Name of object class
	 * @param identifier : Object Identifier
	 * @return : It will return the Object
	 * @throws DAOException : Generic DAOException
	 */
	Object loadCleanObj(Class objectClass, Long identifier) throws DAOException;

}
