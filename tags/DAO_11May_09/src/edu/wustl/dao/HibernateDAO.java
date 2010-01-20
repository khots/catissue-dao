/**
 * <p>Title: HibernateDAO Interface</p>
 * <p>Description :This interface defines methods which are specific to
 * Hibernate operations</p>
 *  * @author kalpana_thakur
 */
package edu.wustl.dao;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.wustl.common.domain.AuditEventLog;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.NamedQueryParam;


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
	 * @param queryName : handle for named query.
	 * @param namedQueryParams : Map holding the parameter type and parameter value.
	 * @return the list of data.
	 * @throws DAOException :Generic DAOException.
	 */
	List executeNamedQuery(String queryName,Map<String, NamedQueryParam> namedQueryParams) throws DAOException;
	/**
	 * updates the persisted object into the database.
	 * @param obj Object to be updated in database
	 * @param oldObj old object.
	 * @throws DAOException : generic DAOException
	 */
	void update(Object obj, Object oldObj) throws DAOException;

	/**
	 * Added method to audit.
	 * @param obj Object to be updated in database
	 * @param oldObj old object.
	 * @throws DAOException : generic DAOException
	 */
	void audit(Object obj, Object oldObj) throws DAOException;

	/**
	 * Executes the HQL query. for given startIndex and max
	 * records to retrieve
	 * @param query  HQL query to execute
	 * @param startIndex Starting index value
	 * @param maxRecords max number of records to fetch
	 * @param paramValues List of parameter values.
	 * @return List of data.
	 * @throws DAOException database exception.
	 */
	List executeQuery(String query,Integer startIndex,
			Integer maxRecords,LinkedList paramValues) throws DAOException;

}