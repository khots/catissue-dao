/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/catissue-dao/LICENSE.txt for details.
 */

/**
 * <p>Title: HibernateDAO Interface</p>
 * <p>Description :This interface defines methods which are specific to
 * Hibernate operations</p>
 *  * @author kalpana_thakur
 */
package edu.wustl.dao;

import java.util.Collection;

import org.hibernate.Query;

import edu.wustl.common.domain.AuditEventLog;
import edu.wustl.dao.exception.DAOException;


/** This interface defines methods which are specific to Hibernate operations .*/

public interface HibernateDAO extends DAO
{

	/**
	 * Add AuditEvent Logs.
	 * @param auditEventDetailsCollection audit Event Details Collection.
	 */
	void addAuditEventLogs(Collection<AuditEventLog> auditEventDetailsCollection);

	/**
	 * Obtain an instance of Query for a named query string defined in the mapping file.
	 * This method executes named query and returns list of objects as result.
	 * @param queryName handle to get named query.
	 * @return list of objects.
	 *@throws DAOException :Generic DAOException.
	 */
	Collection executeNamedQuery(String queryName)throws DAOException;

	/**
	 * Obtain an instance of Query for a named query string defined in the mapping file.
	 * @param queryName handle to get named query.
	 * @return Query named Query object.
	 *@throws DAOException :Generic DAOException.
	 */
	Query getNamedQuery(String queryName)throws DAOException;

	/**
	 * updates the persisted object into the database.
	 * @param obj Object to be updated in database
	 * @param oldObj old object.
	 * @throws DAOException : generic DAOException
	 */
	void update(Object obj, Object oldObj) throws DAOException;

}
