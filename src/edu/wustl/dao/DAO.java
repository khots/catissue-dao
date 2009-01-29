/**
 * <p>Title: DAO Interface>
 * <p>Description:	DAO provides methods to manipulate the domain objects.
 * It provides methods like insert ,update etc .</p>
 * @version 1.0
 * @author kalpana_thakur
 */

package edu.wustl.dao;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.domain.AuditEventLog;
import edu.wustl.dao.connectionmanager.IConnectionManager;
import edu.wustl.dao.exception.DAOException;


/**
 * @author kapil_kaveeshwar
 *
 * This interface defines methods for insertion, updation, deletion and retrieval of data.
 */
public interface DAO
{
	/**
	 * Insert the Object in the database.
	 * @param obj Object to be inserted in database
	 * @param isAuditable is Auditable.
	 * @throws DAOException generic DAOException
	 */
	void insert(Object obj,boolean isAuditable) throws DAOException;

	/**
	 * updates the persisted object in the database.
	 * @param obj Object to be updated in database
	 * @throws DAOException : generic DAOException
	 */
	void update(Object obj) throws DAOException;


	/**
	 * Deletes the persistent object from the database.
	 * @param obj The object to be deleted.
	 * @throws DAOException generic DAOException.
	 */
	void delete(Object obj) throws DAOException;

	/**
	 * Audit the object.
	 * @param obj Object to be audited.
	 * @param oldObj old Object.
	 * @param sessionDataBean session Data.
	 * @param isAuditable is Auditable.
	 * @throws DAOException generic DAOException.
	 */
	void audit(Object obj, Object oldObj, SessionDataBean sessionDataBean,
			boolean isAuditable) throws DAOException;

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
	 * Execute Query.
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
	 * @return Object.
	 * @throws DAOException generic DAOException.
	 */
	Object retrieveAttribute(Class objClass, Long identifier,
			String attributeName,String columnName) throws DAOException;
	/**
	 * This method will be used to establish the session with the database.
	 * @param sessionDataBean : This will hold the session related information.
	 * @throws DAOException : generic DAOException
	 */
	void openSession(SessionDataBean sessionDataBean) throws DAOException;

	/**
	 * This method will be used to close the session with the database.
	 * @throws DAOException :generic DAOException
	 */
	void closeSession() throws DAOException;

	/**
	 * Commit the database level changes.
	 * @throws DAOException : generic DAOException
	 */
	void commit() throws DAOException;

	/**
	 * Roll back all the changes after last commit.
	 * @throws DAOException : generic DAOException
	 */
	void rollback() throws DAOException;

	/**
	 * This method will be called to set connection Manager Object.
	 * @param connectionManager : Connection Manager.
	 */
	void setConnectionManager(IConnectionManager connectionManager);
	/**
	 *This method will be called to get connection Manager Object.
	 * @return It will return the Connection Manager Object.
	 */
	//IConnectionManager getConnectionManager();

	/**
	 * @param excp : Exception Object.
	 * @return : It will return the formated messages.
	 * @throws DAOException : DAO exception.
	 */
	String formatMessage(Exception excp)throws DAOException;


	/**
	 *This method will be called to retrieved the clean connection object.
	 *@return Connection object
	 *@throws DAOException :Generic DAOException.
	 */
	Connection getCleanConnection() throws DAOException;


	/**
	 *This method will be called to close connection.
	 *@throws DAOException :Generic DAOException.
	 */
	void closeCleanConnection() throws DAOException;


	/**
	 * This method will be called to obtain clean session.
	 * @return session object.
	 *@throws DAOException :Generic DAOException.
	 */
	Session getCleanSession() throws DAOException;

	/**
	 *This method will be called to close session.
	 *@throws DAOException :Generic DAOException.
	 */
	void closeCleanSession() throws DAOException;

	/**
	 * Add AuditEvent Logs.
	 * @param auditEventDetailsCollection audit Event Details Collection.
	 */
	void addAuditEventLogs(Collection<AuditEventLog> auditEventDetailsCollection);

	/**
	 * This method executes named query and returns list of objects as result.
	 * @param queryName handle to get named query.
	 * @return list of objects.
	 *@throws DAOException :Generic DAOException.
	 */
	Collection executeNamedQuery(String queryName)throws DAOException;

	/**
	 * This method returns named query.
	 * @param queryName handle to get named query.
	 * @return Query named Query object.
	  *@throws DAOException :Generic DAOException.
	 */
	Query getNamedQuery(String queryName)throws DAOException;

}