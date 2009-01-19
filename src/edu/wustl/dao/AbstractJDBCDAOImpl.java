/**
 * <p>Title: AbstractJDBCDAOImpl Class>
 * <p>Description:	JDBCDAO is default implementation of DAO and JDBCDAO through JDBC.
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 */

package edu.wustl.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.querydatabean.QueryDataBean;
import edu.wustl.common.util.PagenatedResultData;
import edu.wustl.common.util.QueryParams;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.condition.EqualClause;
import edu.wustl.dao.connectionmanager.IConnectionManager;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.DAOConstants;
import edu.wustl.dao.util.DatabaseConnectionParams;


/**
 * @author kalpana_thakur
 *
 */
public abstract class AbstractJDBCDAOImpl implements JDBCDAO
{

	 /**
   	 * specify clean connection instance.
   	 */
     private Connection cleanConnection = null;

	/**
	 * Audit Manager.
	 */
	private AuditManager auditManager;
	/**
	 * Class Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(AbstractJDBCDAOImpl.class);
	/**
	 * Connection Manager.
	 */
	private IConnectionManager connectionManager = null ;

	/**
	 * Connection.
	 */
	private Connection connection = null ;

	/**
	 * JDBCBatchUpdate instance.
	 */
	private JDBCBatchUpdate jdbcBatchUpdate= new JDBCBatchUpdate();

	/**
	 * This method will be used to establish the session with the database.
	 * Declared in DAO class.
	 * @param sessionDataBean : holds the data associated to the session.
	 * @throws DAOException :It will throw DAOException
	 */
	public void openSession(SessionDataBean sessionDataBean)
	throws DAOException
	{
		try
		{
			logger.debug("Open the session");
			initializeAuditManager(sessionDataBean);
			connection = connectionManager.getConnection();
			connection.setAutoCommit(false);

			jdbcBatchUpdate.setConnection(connection);

		}
		catch (Exception sqlExp)
		{
			logger.error(sqlExp.getMessage(), sqlExp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,sqlExp,"AbstractJDBCDAOImpl.java :"+
					DAOConstants.OPEN_SESSION_ERROR);
		}
	}

	/**
	 * This method will be used to close the session with the database.
	 * Declared in DAO class.
	 * @throws DAOException : It will throw DAOException.
	 */
	public void closeSession() throws DAOException
	{
		try
		{
			logger.debug("Close the session");
			auditManager = null;
			connectionManager.closeConnection();

			jdbcBatchUpdate.clearBatch();
			jdbcBatchUpdate.cleanConnection();

		}
		catch(Exception dbex)
		{
			logger.error(dbex.getMessage(), dbex);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,dbex,"AbstractJDBCDAOImpl.java :"
					+DAOConstants.CLOSE_SESSION_ERROR);
		}
	}

	/**
	 * Commit the database level changes.
	 * Declared in DAO class.
	 * @throws DAOException : It will throw DAOException
	 * @throws SMException
	 */
	public void commit() throws DAOException
	{
		try
		{
			logger.debug("Session commit");
			auditManager.insert(this);
			connectionManager.commit();
		}
		catch (Exception exp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.audit.error");
			throw new DAOException(errorKey,exp,"AbstractJDBCDAOImpl.java :"+
					DAOConstants.COMMIT_DATA_ERROR);
		}
	}

	/**
	 * RollBack all the changes after last commit.
	 * Declared in DAO class.
	 * @throws DAOException : It will throw DAOException.
	 */
	public void rollback() throws DAOException
	{
		logger.debug("Session rollback");
		connectionManager.rollback();
	}


	/**
	 * This will be called to initialized the Audit Manager.
	 * @param sessionDataBean : This will holds the session data.
	 */
	private void initializeAuditManager(SessionDataBean sessionDataBean)
	{
		logger.debug("Initialize audit manager");
		auditManager = new AuditManager();
		if (sessionDataBean == null)
		{
			auditManager.setUserId(null);
		}
		else
		{
			auditManager.setUserId(sessionDataBean.getUserId());
			auditManager.setIpAddress(sessionDataBean.getIpAddress());
		}
	}

	/**
	 * This method will be called for executing a static SQL statement.
	 * @see edu.wustl.dao.JDBCDAO#executeUpdate(java.lang.String)
	 * @param query :Holds the query string.
	 * @throws DAOException : DAOException.
	 */
	public void executeUpdate(String query) throws DAOException
	{
		logger.debug("Execute query.");
		DatabaseConnectionParams databaseConnectionParams = new DatabaseConnectionParams();
		databaseConnectionParams.setConnection(connection);
		databaseConnectionParams.executeUpdate(query);
	}

	/**
	 * @see edu.wustl.common.dao.JDBCDAO#createTable(java.lang.String, java.lang.String[])
	 * This method will Create and execute a table with the name and columns specified
	 * @param tableName : Table Name
	 * @param columnNames : Columns of the table
	 * @throws DAOException DAOException
	 * */

	public void createTable(String tableName, String[] columnNames) throws DAOException
	{
		logger.debug("Create table.");
		String query = createTableQuery(tableName,columnNames);
		executeUpdate(query);
	}

	/**
	 * Creates a table with the query specified.
	 * @param query Query create table.
	 * @throws DAOException DAOException
	 */
	public void createTable(String query) throws DAOException
	{
		logger.debug("Create table.");
		executeUpdate(query);
	}

	/**
	 * Generates the Create Table Query.
	 * @param tableName Name of the table to create.
	 * @param columnNames Columns in the table.
	 * @return Create Table Query
	 * @throws DAOException : It will throw DAOException
	 */
	private String createTableQuery(String tableName, String[] columnNames) throws DAOException
	{
		logger.debug("Prepared query for create table.");
		StringBuffer query = new StringBuffer("CREATE TABLE").append(DAOConstants.TAILING_SPACES).
		append(tableName).append(" (");
		int index;

		for ( index=0; index < (columnNames.length - 1); index++)
		{

			query = query.append(columnNames[index]).append(" VARCHAR(50),");
		}
		query.append(columnNames[index]).append(" VARCHAR(50))");

		return  query.toString();
	}
	/**
	 * Returns the ResultSet containing all the rows in the table represented in sourceObjectName.
	 * @param sourceObjectName The table name.
	 * @return The ResultSet containing all the rows in the table represented in sourceObjectName.
	 * @throws ClassNotFoundException
	 * @throws DAOException generic DAOException
	 */
	public List<Object> retrieve(String sourceObjectName) throws DAOException
	{
		logger.debug("Inside retrieve method");
		return retrieve(sourceObjectName, null, null,false);
	}

	/**
	 * Returns the ResultSet containing all the rows according to the columns specified
	 * from the table represented in sourceObjectName.
	 * @param sourceObjectName The table name.
	 * @param selectColumnName The column names in select clause.
	 * @return The ResultSet containing all the rows according to the columns specified
	 * from the table represented in sourceObjectName.
	 * @throws DAOException : DAOException
	*/
	public List<Object> retrieve(String sourceObjectName, String[] selectColumnName) throws DAOException
	{
		logger.debug("Inside retrieve method");
		return retrieve(sourceObjectName, selectColumnName,null,false);
	}

	/**
	 * Returns the ResultSet containing all the rows according to the columns specified
	 * from the table represented in sourceObjectName.
	 * @param sourceObjectName The table name.
	 * @param selectColumnName The column names in select clause.
	 * @param onlyDistinctRows true if only distinct rows should be selected.
	 * @return The ResultSet containing all the rows according to the columns specified
	 * from the table represented in sourceObjectName.
	 * @throws DAOException DAOException.
	 */
	public List<Object> retrieve(String sourceObjectName, String[] selectColumnName,
			boolean onlyDistinctRows) throws DAOException
	{
		logger.debug("Inside retrieve method");
		return retrieve(sourceObjectName, selectColumnName,null,
				onlyDistinctRows);
	}

	/**
	 * Returns the ResultSet containing all the rows according to the columns specified
	 * from the table represented in sourceObjectName as per the where clause.
	 * @param sourceObjectName The table name.
	 * @param selectColumnName The column names in select clause.
	 * @param queryWhereClause The where condition clause which holds the where column name,
	 * value and conditions applied
	 * @return The ResultSet containing all the rows according to the columns specified
	 * from the table represented in sourceObjectName which satisfies the where condition
	 * @throws DAOException : DAOException
	 */
	public List<Object> retrieve(String sourceObjectName,
			String[] selectColumnName, QueryWhereClause queryWhereClause)
			throws DAOException
	{
		logger.debug("Inside retrieve method");
		return retrieve(sourceObjectName, selectColumnName,queryWhereClause,false);
	}

	/**
	 * Returns the ResultSet containing all the rows from the table represented in sourceObjectName
	 * according to the where clause.It will create the where condition clause which holds where column name,
	 * value and conditions applied.
	 * @param sourceObjectName The table name.
	 * @param whereColumnName The column names in where clause.
	 * @param whereColumnValue The column values in where clause.
	 * @return The ResultSet containing all the rows from the table represented
	 * in sourceObjectName which satisfies the where condition
	 * @throws DAOException : DAOException
	 */
	public List<Object> retrieve(String sourceObjectName, String whereColumnName, Object whereColumnValue)
			throws DAOException
	{
		logger.debug("Inside retrieve method");
		String[] selectColumnName = null;

		QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
		queryWhereClause.addCondition(new EqualClause(whereColumnName,whereColumnValue,sourceObjectName));

		return retrieve(sourceObjectName, selectColumnName,queryWhereClause,false);
	}


	/**
	 * Retrieves the records for class name in sourceObjectName according to
	 * field values passed in the passed session.
	 * @param sourceObjectName This will holds the object name.
	 * @param selectColumnName An array of field names in select clause.
	 * @param queryWhereClause This will hold the where clause.It holds following:
	 * 1.whereColumnName : An array of field names in where clause.
	 * 2.whereColumnCondition : The comparison condition for the field values.
	 * 3.whereColumnValue : An array of field values.
	 * 4.joinCondition : The join condition.
	 * @param onlyDistinctRows True if only distinct rows should be selected
	 * @return The ResultSet containing all the rows from the table represented
	 * in sourceObjectName which satisfies the where condition
	 * @throws DAOException : DAOException
	 */
	public List<Object> retrieve(String sourceObjectName, String[] selectColumnName,
			QueryWhereClause queryWhereClause,
			 boolean onlyDistinctRows) throws DAOException
	{

		logger.debug("Inside retrieve method");
		List<Object> list = null;
		try
		{
			StringBuffer queryStrBuff = getSelectPartOfQuery(selectColumnName, onlyDistinctRows);
			getFromPartOfQuery(sourceObjectName, queryStrBuff);

			if(queryWhereClause != null)
			{
				queryStrBuff.append(queryWhereClause.toWhereClause());
			}

			logger.debug("JDBC Query " + queryStrBuff);
			list = executeQuery(queryStrBuff.toString(), null, false, null);
		}
		catch (Exception exp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey, exp,"AbstractJDBCDAOImpl.java :"+
					DAOConstants.RETRIEVE_ERROR);

		}

		return list;
	}

	/**
	 * This method will return the select clause of Query.
	 * @param selectColumnName An array of field names in select clause.
	 * @param onlyDistinctRows true if only distinct rows should be selected
	 * @return It will return the select clause of Query.
	 */
	private StringBuffer getSelectPartOfQuery(String[] selectColumnName,
			boolean onlyDistinctRows)
	{
		logger.debug("Prepare select part of query");
		StringBuffer query = new StringBuffer("SELECT ");
		if ((selectColumnName != null) && (selectColumnName.length > 0))
		{
			if (onlyDistinctRows)
			{
				query.append(" DISTINCT ");
			}
			int index;
			for (index = 0; index < (selectColumnName.length - 1); index++)
			{
				query.append(selectColumnName[index]).append("  ,");
			}
			query.append(selectColumnName[index]).append("  ");
		}
		else
		{
			query.append("* ");
		}
		return query;
	}

	/**
	 * This will generate the from clause of Query.
	 * @param sourceObjectName The table name.
	 * @param queryStrBuff Query buffer
	 */
	private void getFromPartOfQuery(String sourceObjectName, StringBuffer queryStrBuff)
	{
		logger.debug("Prepare from part of the query");
		queryStrBuff.append("FROM ").append(sourceObjectName);
	}

	/**
	 * Executes the query.
	 * @param query :Query to be executed.
	 * @param sessionDataBean : Holds the data associated to the session.
	 * @param isSecureExecute Query will be executed only if isSecureExecute is true.
	 * @param queryResultObjectDataMap : queryResultObjectDataMap
	 * @return This method executed query, parses the result and returns List of rows.
	 * @throws DAOException : DAOException

	 */
	public List<Object> executeQuery(String query, SessionDataBean sessionDataBean,
			boolean isSecureExecute, Map<Object,QueryDataBean>
			queryResultObjectDataMap) throws
			DAOException
	{

		logger.debug("Inside executeQuery method");
		QueryParams queryParams = new QueryParams();
		queryParams.setQuery(query);
		queryParams.setSessionDataBean(sessionDataBean);
		queryParams.setSecureToExecute(isSecureExecute);
		queryParams.setHasConditionOnIdentifiedField(false);
		queryParams.setQueryResultObjectDataMap(queryResultObjectDataMap);
		queryParams.setStartIndex(-1);
		queryParams.setNoOfRecords(-1);
		queryParams.setConnection(connection);

		return getQueryResultList(queryParams).getResult();
	}


	/**
	 *Description: Query performance issue. Instead of saving complete query results in session,
	 *results will be fetched for each page navigation.
	 *@param queryParams : This object will hold all the Query related details.
	 *@throws DAOException : DAOException
	 *@return : It will return the pagenatedResultData.
	 * */
	public PagenatedResultData executeQuery(QueryParams  queryParams) throws DAOException
	{
		logger.debug("execute query");
		PagenatedResultData pagenatedResultData = null;
		if (!(DAOConstants.SWITCH_SECURITY && queryParams.isSecureToExecute() &&
				queryParams.getSessionDataBean() == null))
		{
		  pagenatedResultData = (PagenatedResultData)getQueryResultList(queryParams);
		}
		return pagenatedResultData;
	}

	/**
	 * This method executed query, parses the result and returns List of rows after doing security checks.
	 * for user's right to view a record/field
	 * @param queryParams : It will hold all information related to query.
	 * @return It will return the pagenatedResultData.
	 * @throws DAOException : DAOException
	 */
	public abstract PagenatedResultData getQueryResultList(QueryParams queryParams) throws DAOException;

	/**
	 * This method will be called to insert hashed data values.
	 * @param tableName :Name of the table
	 * @param columnValues :List of column values
	 * @param columnNames  :List of column names.
	 * @throws DAOException  :DAOException
	 * @throws SQLException : SQLException
	 */
	public void insertHashedValues(String tableName, List<Object> columnValues, List<String> columnNames)
	throws DAOException, SQLException
	{
		logger.debug("Insert hashed data to database");
		HashedDataHandler hashedDataHandler = new HashedDataHandler();
		hashedDataHandler.insertHashedValues(tableName, columnValues, columnNames, connection);
	}

	/**
	 * This method will be called to get connection Manager object.
	 * @return IConnectionManager: Connection Manager.
	 */
/*	protected IConnectionManager getConnectionManager()
	{
		return connectionManager;
	}
*/
	/**
	 * This method will be called to get connection Manager object.
	 * @return IConnectionManager: Connection Manager.
	 */
	protected Connection getConnection()
	{
		logger.debug("Get the connection");
		return connection;
	}

	/**
	 * This method will be called to set connection Manager object.
	 * @param connectionManager : Connection Manager.
	 */
	public void setConnectionManager(IConnectionManager connectionManager)
	{
		logger.debug("Setting the connection manager");
		this.connectionManager = connectionManager;
	}

	/**
	 *This method will be called to close current connection.
	 *@throws DAOException :Generic DAOException.
	 */
	public void closeCleanConnection() throws DAOException
	{
		logger.debug("Close clean connection");
		try
		{
			cleanConnection.close();

		}
		catch (SQLException sqlExp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,sqlExp,"DAOFactory.java :"+
					DAOConstants.CLOSE_CONN_ERR);
		}
	}



	/**
	 * Gets Query Number of given SQL.
	 * @param sql SQL query.
	 * @return queryNo.
	 * @throws DAOException generic DAOException.
	 *//*
	protected Long getSequenceNumber(String sql) throws DAOException
	{
		List<Object> list = executeQuery(sql, null, false, null);
		long queryNo = 1;
		if (!list.isEmpty())
		{

			List<Object> columnList = (List) list.get(0);
			if (!columnList.isEmpty())
			{
				String str = (String) columnList.get(0);
				if (!"".equals(str))
				{
					queryNo = Long.parseLong(str);

				}
			}
		}
		return queryNo;
	}
*/

	/**
	 * This method will be called to set the size of the batch.
	 * @param batchSize batchSize
	 * @throws DAOException : Generic database exception.
	 */
	public void setBatchSize(int batchSize) throws DAOException
	{
		logger.debug("Set the batch size");
		jdbcBatchUpdate.setBatchSize(batchSize);
	}

	/**
	 * This method will be called to set the DML object to batch.
	 * @param dmlObject :DML object
	 * @throws DAOException : Generic database exception.
	 */
	public void addDMLToBatch(String dmlObject) throws DAOException
	{
		logger.debug("Add DML to batch");
		jdbcBatchUpdate.addDMLToBatch(dmlObject);
	}

	/**
	 * This method will be called for batch update insert.
	 * @throws DAOException :Generic DAOException.
	 */
	public void batchUpdate() throws DAOException
	{
		logger.debug("Update the batch");
		jdbcBatchUpdate.batchUpdate();
	}


	/**
	 * @throws DAOException :Generic DAOException.
	 */
	public void clearBatch() throws DAOException
	{
		logger.debug("Clear the batch");
		jdbcBatchUpdate.clearBatch();
	}

	/**
	 * @param obj :
	 * @param oldObj :
	 * @param sessionDataBean :
	 * @param isAuditable :
	 * @throws DAOException :
	 */
	public void audit(Object obj, Object oldObj, SessionDataBean sessionDataBean,
			boolean isAuditable) throws DAOException
	{
		ErrorKey errorKey = ErrorKey.getErrorKey("dao.method.without.implementation");
		throw new DAOException(errorKey,new Exception(),"AbstractJDBCDAOImpl.java :");
	}

	/**
	 * @param obj :
	 * @param sessionDataBean :
	 * @param isAuditable :
	 * @param isSecureInsert :
	 * @throws DAOException :
	 */
	public void insert(Object obj, SessionDataBean sessionDataBean,
			boolean isAuditable, boolean isSecureInsert)
			throws DAOException
	{
		ErrorKey errorKey = ErrorKey.getErrorKey("dao.method.without.implementation");
		throw new DAOException(errorKey,new Exception(),"AbstractJDBCDAOImpl.java :");
	}

	/**
	 * @see edu.wustl.common.dao.DAO#retrieveAttribute(java.lang.Class, java.lang.Long, java.lang.String)
	 * @param objClass : Class name
	 * @param identifier : Identifier of object
	 * @param attributeName : Attribute Name to be fetched
	 * @param columnName : where clause column field.
	 * @return It will return the Attribute of the object having given identifier
	 * @throws DAOException : DAOException
	 */
	public Object retrieveAttribute(Class objClass, Long identifier,
			String attributeName,String columnName) throws DAOException
	{
		ErrorKey errorKey = ErrorKey.getErrorKey("dao.method.without.implementation");
		throw new DAOException(errorKey,new Exception(),"AbstractJDBCDAOImpl.java :");
	}


	/**
	 * @param obj :
	 * @throws DAOException :
	 */
	public void update(Object obj) throws DAOException
	{
		ErrorKey errorKey = ErrorKey.getErrorKey("dao.method.without.implementation");
		throw new DAOException(errorKey,new Exception(),"AbstractJDBCDAOImpl.java :");
	}

	/**
	 * @param sourceObjectName :
	 * @param identifier :
	 * @return Object :
	 * @throws DAOException :
	 */
	public Object retrieve(String sourceObjectName, Long identifier)
			throws DAOException
	{
		ErrorKey errorKey = ErrorKey.getErrorKey("dao.method.without.implementation");
		throw new DAOException(errorKey,new Exception(),"AbstractJDBCDAOImpl.java :");
	}

	/**
	 * @param excp : Exception Object. Refactoring
	 * @param applicationName : Name of the application.
	 * @return : It will return the formated messages.
	 * @throws DAOException : DAO exception.
	 */
	public String formatMessage(Exception excp, String applicationName)
	throws DAOException
	{
		ErrorKey errorKey = ErrorKey.getErrorKey("dao.method.without.implementation");
		throw new DAOException(errorKey,new Exception(),"AbstractJDBCDAOImpl.java :");
	}

	/**
	 * This method will be called to obtain clean session.
	 * @return session object.
	 *@throws DAOException :Generic DAOException.
	 */
	public Session getCleanSession() throws DAOException
	{

		ErrorKey errorKey = ErrorKey.getErrorKey("dao.method.without.implementation");
		throw new DAOException(errorKey,new Exception(),"AbstractJDBCDAOImpl.java :");

	}

	/**
	 *This method will be called to close current connection.
	 *@throws DAOException :Generic DAOException.
	 */
	public void closeCleanSession() throws DAOException
	{
		ErrorKey errorKey = ErrorKey.getErrorKey("dao.method.without.implementation");
		throw new DAOException(errorKey,new Exception(),"AbstractJDBCDAOImpl.java :");
	}


	/**
	 *This method will be called to close current connection.
	 *@param query :
	 *@throws DAOException :Generic DAOException.
	 *@return list
	 */
	public List executeQuery(String query) throws DAOException
	{
		ErrorKey errorKey = ErrorKey.getErrorKey("dao.method.without.implementation");
		throw new DAOException(errorKey,new Exception(),"AbstractJDBCDAOImpl.java :");
	}

	/**
	 *This method will be called to retrieved the current connection object.
	 *@return Connection object
	 *@throws DAOException :Generic DAOException.
	 */
	public Connection getCleanConnection() throws DAOException
	{
		ErrorKey errorKey = ErrorKey.getErrorKey("dao.method.without.implementation");
		throw new DAOException(errorKey,new Exception(),"AbstractJDBCDAOImpl.java :");
	}

}
