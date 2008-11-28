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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.beans.QueryResultObjectDataBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.dao.queryExecutor.PagenatedResultData;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.QueryParams;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.dbmanager.DAOException;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.condition.EqualClause;
import edu.wustl.dao.connectionmanager.IConnectionManager;
import edu.wustl.dao.util.DAOConstants;
import edu.wustl.dao.util.DatabaseConnectionParams;

/**
 * @author kalpana_thakur
 *
 */
public abstract class AbstractJDBCDAOImpl implements JDBCDAO
{

	/**
	 * Connection object.
	 */
	private Connection connection = null;
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
			initializeAuditManager(sessionDataBean);
			connection = connectionManager.getConnection();
			connection.setAutoCommit(false);
		}
		catch (SQLException sqlExp)
		{
			logger.error(sqlExp.getMessage(), sqlExp);
			throw new DAOException(DAOConstants.OPEN_SESSION_ERROR,sqlExp);
		}
	}

	/**
	 * This method will be used to close the session with the database.
	 * Declared in DAO class.
	 * @throws DAOException : It will throw DAOException.
	 */
	public void closeSession() throws DAOException
	{
		auditManager = null;
		getConnectionManager().closeConnection();
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
			auditManager.insert(this);
			if(connection == null)
			{
				logger.fatal(DAOConstants.NO_CONNECTION_TO_DB);
			}

			connection.commit();
		}
		catch (SQLException dbex)
		{
			logger.error(dbex.getMessage(), dbex);
			throw new DAOException(DAOConstants.COMMIT_DATA_ERROR, dbex);
		}
	}

	/**
	 * RollBack all the changes after last commit.
	 * Declared in DAO class.
	 * @throws DAOException : It will throw DAOException.
	 */
	public void rollback() throws DAOException
	{
		try
		{
			if(connection == null)
			{
				logger.fatal(DAOConstants.NO_CONNECTION_TO_DB);
			}

			connection.rollback();
		}
		catch (SQLException dbex)
		{
			logger.error(dbex.getMessage(), dbex);
			throw new DAOException(DAOConstants.ROLLBACK_ERROR, dbex);

		}
	}


	/**
	 * This will be called to initialized the Audit Manager.
	 * @param sessionDataBean : This will holds the session data.
	 */
	private void initializeAuditManager(SessionDataBean sessionDataBean)
	{
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
		StringBuffer query = new StringBuffer("CREATE TABLE ").append(tableName).append(" (");
		int index;

		for ( index=0; index < (columnNames.length - 1); index++)
		{

			query = query.append(columnNames[index]).append(" VARCHAR(50),");
		}
		query.append(columnNames[index]).append(" VARCHAR(50));");

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
		String[] selectColumnName = null;

		QueryWhereClause queryWhereClause = new QueryWhereClause();
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
		catch (ClassNotFoundException classExp)
		{
			logger.error(classExp.getMessage(), classExp);
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
	 * @throws ClassNotFoundException : ClassNotFoundException
	 */
	public List<Object> executeQuery(String query, SessionDataBean sessionDataBean,
			boolean isSecureExecute, Map<Object,QueryResultObjectDataBean>
			queryResultObjectDataMap) throws ClassNotFoundException,
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
		PagenatedResultData pagenatedResultData = null;
		if (!(Constants.SWITCH_SECURITY && queryParams.isSecureToExecute() &&
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
	 * @param tableName TODO
	 * @param columnValues TODO
	 * @param columnNames TODO
	 * @throws DAOException  :DAOException
	 * @throws SQLException : SQLException
	 */
	public void insert(String tableName, List<Object> columnValues, List<String>... columnNames)
	throws DAOException, SQLException
	{
		List<Integer>dateColumns = new ArrayList<Integer>();
		List<Integer>numberColumns = new ArrayList<Integer>();
		List<Integer>tinyIntColumns = new ArrayList<Integer>();
		List<String>columnNamesList = new ArrayList<String>();
		ResultSetMetaData metaData;
		DatabaseConnectionParams databaseConnectionParams = new DatabaseConnectionParams();
		databaseConnectionParams.setConnection(getConnection());
		PreparedStatement stmt = null;
		try
		{
			if(columnNames != null && columnNames.length > 0)
			{
				metaData = getMetaData(tableName, columnNames[0]);
			}
			else
			{
				metaData = getMetaDataAndUpdateColumns(tableName,columnNamesList);
			}
			updateColumns(metaData, dateColumns,numberColumns, tinyIntColumns);
			String insertQuery = createInsertQuery(tableName,columnNamesList,columnValues);
			stmt = databaseConnectionParams.getPreparedStatement(insertQuery);
			for (int i = 0; i < columnValues.size(); i++)
			{
				Object obj = columnValues.get(i);
				setDateColumns(stmt, i,obj, dateColumns);
				setTinyIntColumns(stmt, i, obj,tinyIntColumns);
				setTimeStampColumn(stmt, i, obj);
				setNumberColumns(numberColumns, stmt, i, obj);
			}
			stmt.executeUpdate();
		}
		catch (SQLException sqlExp)
		{
			logger.error(sqlExp.getMessage(),sqlExp);
			throw new DAOException(sqlExp.getMessage(), sqlExp);
		}
		finally
		{
			databaseConnectionParams.closeConnectionParams();
		}
	}

	/**
	 * @param metaData ResultSetMetaData
	 * @param dateColumns : TODO
	 * @param numberColumns : TODO
	 * @param tinyIntColumns : TODO
	 * @throws SQLException : SQLException
	 */
	protected void updateColumns(ResultSetMetaData metaData,List<Integer> dateColumns,
			List<Integer> numberColumns,List<Integer> tinyIntColumns) throws SQLException
	{
		for (int i = 1; i <= metaData.getColumnCount(); i++)
		{
			String type = metaData.getColumnTypeName(i);
			if (("DATE").equals(type))
			{
				dateColumns.add(Integer.valueOf(i));
			}
			if (("NUMBER").equals(type))
			{
				numberColumns.add(Integer.valueOf(i));
			}
			if (("TINYINT").equals(type))
			{
				tinyIntColumns.add(Integer.valueOf(i));
			}

		}

	}

	/**
	 * This method returns the metaData associated to the table specified in tableName.
	 * @param tableName Name of the table whose metaData is requested
	 * @param columnNames Table columns
	 * @return It will return the metaData associated to the table.
	 * @throws DAOException : DAOException
	 */
	protected final ResultSetMetaData getMetaData(String tableName,List<String> columnNames)throws DAOException
	{
		DatabaseConnectionParams databaseConnectionParams = new DatabaseConnectionParams();

		ResultSetMetaData metaData;
		StringBuffer sqlBuff = new StringBuffer(DAOConstants.TAILING_SPACES);
		sqlBuff.append("Select").append(DAOConstants.TAILING_SPACES);
		try
		{

			databaseConnectionParams.setConnection(connection);
			for (int i = 0; i < columnNames.size(); i++)
			{
				sqlBuff.append(columnNames.get(i));
				if (i != columnNames.size() - 1)
				{
					sqlBuff.append("  ,");
				}
			}
			sqlBuff.append(" from " + tableName + " where 1!=1");
			metaData = databaseConnectionParams.getMetaData(sqlBuff.toString());

		}
		finally
		{

			databaseConnectionParams.closeConnectionParams();
		}

		return metaData;

	}

	/**
	 * This method will returns the metaData associated to the table specified in tableName
	 * and update the list columnNames.
	 * @param tableName Name of the table whose metaData is requested
	 * @param columnNames Table columns
	 * @return It will return the metaData associated to the table.
	 * @throws DAOException : DAOException
	 */
	protected final ResultSetMetaData getMetaDataAndUpdateColumns(String tableName,
			List<String> columnNames)throws DAOException
	{
		DatabaseConnectionParams databaseConnectionParams = new DatabaseConnectionParams();
		ResultSetMetaData metaData;
		try
		{

			databaseConnectionParams.setConnection(connection);
			StringBuffer sqlBuff = new StringBuffer(DAOConstants.TAILING_SPACES);
			sqlBuff.append("Select * from " ).append(tableName).append(" where 1!=1");
			metaData = databaseConnectionParams.getMetaData(sqlBuff.toString());

			for (int i = 1; i <= metaData.getColumnCount(); i++)
			{
				columnNames.add(metaData.getColumnName(i));
			}
		}
		catch (SQLException sqlExp)
		{

			logger.fatal(sqlExp.getMessage(), sqlExp);
			throw new DAOException(sqlExp);

		}
		finally
		{
			databaseConnectionParams.closeConnectionParams();
		}
		return metaData;
	}

	/**
	 * This method generates the Insert query.
	 * @param tableName : Name of the table given to insert query
	 * @param columnNamesList : List of columns of the table.
	 * @param columnValues : Column values.
	 * @return
	 * TODO Have to refractor this !!!!!!! columnValues might not needed
	 */
	protected String createInsertQuery(String tableName,List<String> columnNamesList,List<Object> columnValues)
	{
		StringBuffer query = new StringBuffer("INSERT INTO " + tableName + "(");

		Iterator<String> columnIterator = columnNamesList.iterator();
		while (columnIterator.hasNext())
		{
			query.append(columnIterator.next());
			if (columnIterator.hasNext())
			{
				query.append(", ");
			}
			else
			{
				query.append(") values(");
			}
		}
		Iterator<Object> iterator = columnValues.iterator();
		while (iterator.hasNext())
		{
			iterator.next();
			query.append("? ");

			if (iterator.hasNext())
			{
				query.append(", ");
			}
			else
			{
				query.append(") ");
			}
		}
		return query.toString();
	}


	/**
	 * This method called to set Number value to PreparedStatement.
	 * @param numberColumns : TODO
	 * @param stmt : TODO
	 * @param index : TODO
	 * @param obj : Object
	 * @throws SQLException : SQLException
	 */
	protected void setNumberColumns(List<Integer> numberColumns, PreparedStatement stmt,
			int index, Object obj) throws SQLException
	{
		if (obj != null && numberColumns.contains(Integer.valueOf(index + 1))
				&& obj.toString().equals("##"))
		{
			stmt.setObject(index + 1, Integer.valueOf(-1));
		}
		else
		{
			stmt.setObject(index + 1, obj);
		}
	}

	/**
	 * This method called to set TimeStamp value to PreparedStatement.
	 * @param stmt :PreparedStatement
	 * @param index :
	 * @param obj :
	 * @throws SQLException SQLException
	 */
	protected void setTimeStampColumn(PreparedStatement stmt, int index,Object obj) throws SQLException
	{
		Timestamp date = isColumnValueDate(obj);
		if (date != null)
		{
			stmt.setObject(index + 1, date);
		}
	}

	/**
	 * @param stmt :
	 * @param index :
	 * @param obj :
	 * @param tinyIntColumns :
	 * @throws SQLException :
	 */
	protected void setTinyIntColumns(PreparedStatement stmt,
			int index, Object obj,List<Integer> tinyIntColumns) throws SQLException
	{
		if (tinyIntColumns.contains(Integer.valueOf(index + 1)))
		{
			setTinyIntColumns(stmt, index, obj);
		}
	}

	/**
	 * This method is called to set TinyInt value
	 * to prepared statement.
	 * @param stmt : TODO
	 * @param index :
	 * @param obj :
	 * @throws SQLException : SQLException
	 */
	private void setTinyIntColumns(PreparedStatement stmt, int index, Object obj)
			throws SQLException
	{
		if (obj != null && (obj.equals("true") || obj.equals("TRUE") || obj.equals("1")))
		{
			stmt.setObject(index + 1, 1);
		}
		else
		{
			stmt.setObject(index + 1, 0);
		}
	}

	/**
	 * This method used to set Date values.
	 * to prepared statement
	 * @param stmt :TODO
	 * @param index :
	 * @param obj :
	 * @param dateColumns :
	 * @throws SQLException : SQLException
	 * @throws DAOException : DAOException
	 */
	protected void setDateColumns(PreparedStatement stmt,
			int index,Object obj,List<Integer> dateColumns)
			throws SQLException, DAOException
	{
		if (obj != null && dateColumns.contains(Integer.valueOf(index + 1))
				&& obj.toString().equals("##"))
		{
			java.util.Date date = null;
			try
			{
				date = Utility.parseDate("1-1-9999", "mm-dd-yyyy");
			}
			catch (ParseException e)
			{
				throw new DAOException(e);
			}
			Date sqlDate = new Date(date.getTime());
			stmt.setDate(index + 1, sqlDate);
		}
	}


	/**
	 * This method checks the TimeStamp value.
	 * @param obj :
	 * @return It returns the TimeStamp value
	 * */
	private Timestamp isColumnValueDate(Object obj)
	{
		Timestamp timestamp = null;
		try
		{
			DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy",Locale.getDefault());
			formatter.setLenient(false);
			java.util.Date date;
			date = formatter.parse((String) obj);
			/*
			 * Recheck if some issues occurs.
			 */
			Timestamp timestampInner = new Timestamp(date.getTime());
			if (obj != null && !obj.toString().equals(""))
			{
				timestamp = timestampInner;
			}
		}
		catch (ParseException parseExp)
		{
			logger.error(parseExp.getMessage(),parseExp);
		}

		return timestamp;
	}

	/* @see edu.wustl.dao.DAO#setConnectionManager(edu.wustl.dao.connectionmanager.IConnectionManager)
	 */

	/**
	 * This method will be called to set connection Manager object.
	 * @param connectionManager : Connection Manager.
	 */
	public void setConnectionManager(IConnectionManager connectionManager)
	{
		this.connectionManager = connectionManager;
	}

	/**
	 * This method will be called to get connection Manager object.
	 * @return IConnectionManager: Connection Manager.
	 */
	public IConnectionManager getConnectionManager()
	{
		return connectionManager;
	}

	/**
	 * This method will be called to get connection object.
	 * @return Connection: Connection object.
	 */
	protected Connection getConnection()
	{
		return connection;
	}


	/**@see edu.wustl.dao.JDBCDAO#getActivityStatus(java.lang.String, java.lang.Long)
	 * @param sourceObjectName :
	 * @param indetifier :
	 * @throws DAOException :
	 * @return Activity status :
	 */

	public String getActivityStatus(String sourceObjectName, Long indetifier) throws DAOException
	{
		throw new DAOException(DAOConstants.METHOD_WITHOUT_IMPLEMENTATION);
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
		throw new DAOException(DAOConstants.METHOD_WITHOUT_IMPLEMENTATION);

	}

	/**
	 * @param obj :
	 * @throws DAOException :
	 */
	public void delete(Object obj) throws DAOException
	{
		throw new DAOException(DAOConstants.METHOD_WITHOUT_IMPLEMENTATION);

	}

	/**
	 * @param tableName :
	 * @param whereColumnName :
	 * @param whereColumnValues :
	 * @throws DAOException :
	 */
	public void disableRelatedObjects(String tableName, String whereColumnName,
			Long[] whereColumnValues) throws DAOException
	{
		throw new DAOException(DAOConstants.METHOD_WITHOUT_IMPLEMENTATION);

	}

	/**
	 * @param obj :
	 * @param sessionDataBean :
	 * @param isAuditable :
	 * @param isSecureInsert :
	 * @throws DAOException :
	 * @throws UserNotAuthorizedException :
	 */
	public void insert(Object obj, SessionDataBean sessionDataBean,
			boolean isAuditable, boolean isSecureInsert)
			throws DAOException, UserNotAuthorizedException
	{
		throw new DAOException(DAOConstants.METHOD_WITHOUT_IMPLEMENTATION);

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
		throw new DAOException(DAOConstants.METHOD_WITHOUT_IMPLEMENTATION);
	}


	/**
	 * @param obj :
	 * @throws DAOException :
	 */
	public void update(Object obj) throws DAOException
	{
		throw new DAOException(DAOConstants.METHOD_WITHOUT_IMPLEMENTATION);
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
		throw new DAOException(DAOConstants.METHOD_WITHOUT_IMPLEMENTATION);
	}



}
