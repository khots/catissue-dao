/**
 * <p>Title: DAO Interface>
 * <p>Description:	DAO provides methods to manipulate the domain objects.
 * It provides methods like insert ,update etc .</p>
 * @version 1.0
 * @author kalpana_thakur
 */

package edu.wustl.dao.newdao;

import java.io.Serializable;
import java.util.List;

import edu.wustl.dao.QueryWhereClause;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

/**
 * @author kalpana_thakur
 * Handles database operations like insertion, updation, deletion and retrieval of data.
 */
public interface DAO<T, ID extends Serializable>
{

	/**
	 * Insert the Object to the database.
	 * @param obj Object to be inserted in database
	 * @throws DAOException generic DAOException
	 */
	void insert(Object obj) throws DAOException;//,boolean isAuditable)

	/**
	 * updates the object into the database.
	 * @param obj Object to be updated in database
	 * @throws DAOException : generic DAOException
	 */
	void update(Object obj) throws DAOException;

	/**h 
	 * 
	 * @param currentObj
	 * @param previousObj
	 * @throws DAOException
	 */
	void update(Object currentObj, Object previousObj) throws DAOException;

	/**
	 * Deletes the persistent object from the database.
	 * @param obj The object to be deleted.
	 * @throws DAOException generic DAOException.
	 */
	void delete(Object obj) throws DAOException;

	/**
	 * 
	 * @return
	 * @throws DAOException
	 */
	List<T> findAll() throws DAOException;

	/**
	 * Retrieve and returns the source object for given id.
	 * @param sourceObjectName Source object in the Database.
	 * @param identifier identifier of source object.
	 * @return object
	 * @throws DAOException generic DAOException.
	 */
	T findById(ID id) throws DAOException;

	List executeQuery(String query, Integer startIndex, Integer maxRecords,
			List<ColumnValueBean> columnValueBeans) throws DAOException;

	List executeNamedQuery(String queryName,List<ColumnValueBean> columnValueBeans) throws DAOException;
	
	List retrieve(String sourceObjectName, String[] selectColumnName,
			QueryWhereClause queryWhereClause, boolean onlyDistinctRows) throws DAOException;
	

	/**
	 * Retrieve and returns the list of all source objects that satisfy the
	 * for given conditions on a various columns.
	 * @param sourceObjectName Source object's name to be retrieved from database.
	 * @param selectColumnName Column names in SELECT clause of the query.
	 * @param queryWhereClause : This will hold following:
	 * 1.whereColumnName Array of column name to be included in where clause.
	 * 2.whereColumnCondition condition to be satisfy between column and its value.
	 * e.g. "=", "<", ">", "=<", ">=" etc
	 * 3. whereColumnValue Value of the column name that included in where clause.
	 * 4.joinCondition join condition between two columns. (AND, OR)
	 * @return the list of all source objects that satisfy the search conditions.
	 * @throws DAOException generic DAOException.
	 * @deprecated : Avoid using these methods in case of JDBC, these are specific to Hibernate.
	 * Will move them in Hibernate DAO in next release.
	 */

	List retrieve(String sourceObjectName,
			String[] selectColumnName,QueryWhereClause queryWhereClause) throws DAOException;

	/**
	 * Retrieve and returns the list of all source objects for given
	 * condition on a single column. The condition value
	 * @param sourceObjectName Source object's name to be retrieved from database.
	 * @param whereColumnName Column name to be included in where clause.
	 * @param whereColumnValue Value of the Column name that included in where clause.
	 * @return the list of all source objects for given condition on a single column.
	 * @throws DAOException generic DAOException.
	 * @deprecated : Avoid using these methods. Use List retrieve(String sourceObjectName,
	 * List<ColumnValueBean> columnValueBeans) pass column name
	 * and column value column value bean
	 */
	List retrieve(String sourceObjectName, String whereColumnName,
			Object whereColumnValue) throws DAOException;

	/**
	 * Returns the ResultSet containing all the rows from the table represented in sourceObjectName
	 * according to the where clause.It will create the where condition clause which holds where column name,
	 * value and conditions applied.
	 * @param sourceObjectName The table name.
	 * @param columnValueBean columnValueBean
	 * @return The ResultSet containing all the rows from the table represented
	 * in sourceObjectName which satisfies the where condition
	 * @throws DAOException : DAOException
	 */
	List retrieve(String sourceObjectName,ColumnValueBean columnValueBean)
			throws DAOException;

	/**
	 * Returns the list of all source objects available in database.
	 * @param sourceObjectName Source object's name to be retrieved from database.
	 * @return the list of all source objects available in database.
	 * @deprecated 
	 * @throws DAOException generic DAOException.
	 */
	List retrieve(String sourceObjectName) throws DAOException;

	/**
	 * Returns the list of all objects with the select columns specified.
	 * @param sourceObjectName Source object in the Database.
	 * @param selectColumnName column names in the select clause.
	 * @return the list of all objects with the select columns specified.
	 * @throws DAOException generic DAOException.
	 * @deprecated 
	 */
	List retrieve(String sourceObjectName, String[] selectColumnName)
			throws DAOException;
}