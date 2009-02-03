/**
 * <p>Title: DAO Interface>
 * <p>Description:	DAO provides methods to manipulate the domain objects.
 * It provides methods like insert ,update etc .</p>
 * @version 1.0
 * @author kalpana_thakur
 */

package edu.wustl.dao;

import java.util.List;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.dao.connectionmanager.IConnectionManager;
import edu.wustl.dao.exception.DAOException;


/**
 * @author kalpana_thakur
 * Handles database operations like insertion, updation, deletion and retrieval of data.
 */
public interface DAO
{
	/**
	 * Insert the Object to the database.
	 * @param obj Object to be inserted in database
	 * @param isAuditable is Auditable.
	 * @throws DAOException generic DAOException
	 */
	void insert(Object obj,boolean isAuditable) throws DAOException;

	/**
	 * updates the persisted object into the database.
	 * @param obj Object to be updated in database
	 * @param oldObj old object.
	 * @throws DAOException : generic DAOException
	 */
	void update(Object obj, Object oldObj) throws DAOException;


	/**
	 * Deletes the persistent object from the database.
	 * @param obj The object to be deleted.
	 * @throws DAOException generic DAOException.
	 */
	void delete(Object obj) throws DAOException;

	/**
	 * Retrieve and returns the list of all source objects that satisfy the
	 * for given conditions on a various columns.
	 * @param sourceObjectName Source object's name to be retrieved from database.
	 * @param selectColumnName Column names in SELECT clause of the query.
	 * @param queryWhereClause : This will hold following:
	 * 1.whereColumnName Array of column name to be included in where clause.
	 * 2.whereColumnCondition condition to be satisfy between column and its value.
	 * e.g. "=", "!=", "<", ">", "in", "null" etc
	 * 3. whereColumnValue Value of the column name that included in where clause.
	 * 4.joinCondition join condition between two columns. (AND, OR)
	 * @param onlyDistinctRows true if only distinct rows should be selected.
	 * @return the list of all source objects that satisfy the search conditions.
	 * @throws DAOException generic DAOException.
	 */

	List<Object> retrieve(String sourceObjectName,String[] selectColumnName,
			QueryWhereClause queryWhereClause,boolean onlyDistinctRows) throws DAOException;


	/**
	 * Retrieve and returns the source object for given id.
	 * @param sourceObjectName Source object in the Database.
	 * @param identifier identifier of source object.
	 * @return object
	 * @throws DAOException generic DAOException.
	 */
	Object retrieveById(String sourceObjectName, Long identifier) throws DAOException;

	/**
	 * Create a new instance of Query for the given HQL query string.
	 * Execute the Query and returns the list of data.
	 * @param query query
	 * @return List.
	 * @throws DAOException generic DAOException.
	 */
	List executeQuery(String query)
		throws DAOException;

	/**
	 * Retrieves attribute value for given class name and identifier.
	 * @param objClass source Class object
	 * @param identifier identifier of the source object
	 * @param attributeName attribute to be retrieved
	 * @param columnName : where clause column field.
	 * @return List.
	 * @throws DAOException generic DAOException.
	 */
	List retrieveAttribute(Class objClass, Long identifier,
			String attributeName,String columnName) throws DAOException;

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
	 */
	List retrieve(String sourceObjectName, String whereColumnName,
			Object whereColumnValue) throws DAOException;

	/**
	 * Returns the list of all source objects available in database.
	 * @param sourceObjectName Source object's name to be retrieved from database.
	 * @return the list of all source objects available in database.
	 * @throws DAOException generic DAOException.
	 */
	List retrieve(String sourceObjectName) throws DAOException;

	/**
	 * Returns the list of all objects with the select columns specified.
	 * @param sourceObjectName Source object in the Database.
	 * @param selectColumnName column names in the select clause.
	 * @return the list of all objects with the select columns specified.
	 * @throws DAOException generic DAOException.
	 */
	List retrieve(String sourceObjectName, String[] selectColumnName)
			throws DAOException;
	/**
	 * Create database connection having auto-commit mode as disabled.
	 * Open a Session on the given connection.
	 * mandatory to call commit to update the changes.
	 * @param sessionDataBean : This will hold the session related information.
	 * @throws DAOException : generic DAOException
	 */
	void openSession(SessionDataBean sessionDataBean) throws DAOException;

	/**
	 * End the session by releasing the JDBC connection and cleaning up.
	 * @throws DAOException :generic DAOException
	 */
	void closeSession() throws DAOException;

	/**
	 * Makes all changes made since the previous
     * commit/rollback.
     * This method should be used only when auto-commit mode has been disabled.
	 * @throws DAOException : generic DAOException
	 */
	void commit() throws DAOException;

	/**
	 * Undoes all changes made in the current transaction
	 * This method should be used only when auto-commit mode has been disabled.
	 * @throws DAOException : generic DAOException
	 */
	void rollback() throws DAOException;

	/**
	 * This method will be called to set connection Manager Object.
	 * @param connectionManager : Connection Manager.
	 */
	void setConnectionManager(IConnectionManager connectionManager);

	/**
	 *This method will be called to retrieved the clean connection object.
	 *connections will be in auto-commit mode.
	 *@return Connection object
	 *@throws DAOException :Generic DAOException.
	 *@deprecated
	 *//*
	Connection getCleanConnection() throws DAOException;


	*//**
	 *Releases this Connection object's database and JDBC resources.
	 *@throws DAOException :Generic DAOException
	 *@deprecated
	 *//*
	void closeCleanConnection() throws DAOException;*/


	/**
	 * Format thrown SQL exception to user readable form.
	 * @param excp : Exception Object.
	 * @return : It will return the formated messages.
	 * @throws DAOException : DAO exception.
	 */
	//String formatMessage(Exception excp)throws DAOException;

/**
	 * Create database connection and open the new session on the given connection.
	 * connections will be in auto-commit mode.
	 * @return session object.
	 *@throws DAOException :Generic DAOException.
	 *//*
	Session getCleanSession() throws DAOException;

	*//**
	 *End the session by releasing the JDBC connection and cleaning up.
	 *@throws DAOException :Generic DAOException.
	 *//*
	void closeCleanSession() throws DAOException;*/

	/**
	 * Audit the object.
	 * @param obj Object to be audited.
	 * @param oldObj old Object.
	 * @param sessionDataBean session Data.
	 * @param isAuditable is Auditable.
	 * @throws DAOException generic DAOException.
	 */
	//void audit(Object obj, Object oldObj, SessionDataBean sessionDataBean,
		//	boolean isAuditable) throws DAOException;


}