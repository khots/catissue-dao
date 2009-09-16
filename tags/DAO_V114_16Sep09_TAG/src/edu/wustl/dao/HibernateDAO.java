/**
 * <p>Title: HibernateDAO Interface</p>
 * <p>Description :This interface defines methods which are specific to
 * Hibernate operations</p>
 *  * @author kalpana_thakur
 */
package edu.wustl.dao;

import java.util.List;
import java.util.Map;

import edu.wustl.dao.exception.DAOException;
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

}
