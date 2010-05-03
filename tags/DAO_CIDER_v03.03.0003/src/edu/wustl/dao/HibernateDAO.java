/**
 * <p>Title: HibernateDAO Interface</p>
 * <p>Description :This interface defines methods which are specific to
 * Hibernate operations</p>
 *  * @author kalpana_thakur
 */
package edu.wustl.dao;

import java.util.List;
import java.util.Map;

import edu.wustl.common.domain.LoginDetails;
import edu.wustl.dao.exception.AuditException;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.HibernateMetaData;
import edu.wustl.dao.util.NamedQueryParam;


/** This interface defines methods which are specific to Hibernate operations .*/

public interface HibernateDAO extends DAO
{

	/**
	 * Obtain an instance of Query for a named query string defined in the mapping file.
	 * @param queryName : handle for named query.
	 * @param namedQueryParams : Map holding the parameter type and parameter value.
	 * @return the list of data.
	 * @throws DAOException :Generic DAOException.
	 */
	List executeNamedQuery(String queryName,Map<String, NamedQueryParam> namedQueryParams) throws DAOException;

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
			Integer maxRecords,List paramValues) throws DAOException;

	/**
	 * This method will be called to set the hibernate metadata
	 * for the application.
	 * @param hibernateMetaData the hibernateMetaData to set
	 */
	void setHibernateMetaData(HibernateMetaData hibernateMetaData);

	/**
	 * This method will be called to fetch hibernate metadata
	 * associated to the application.
	 * @return the hibernateMetaData
	 */
	HibernateMetaData getHibernateMetaData();

	/**
	 * Sets the status of LoginAttempt to loginStatus provided as an argument.
	 * @param loginStatus LoginStatus boolean value.
	 * @param loginDetails LoginDetails object.
	 * @throws AuditException AuditException
	 */
	void auditLoginEvents(boolean loginStatus,
			LoginDetails loginDetails)throws AuditException;

}
