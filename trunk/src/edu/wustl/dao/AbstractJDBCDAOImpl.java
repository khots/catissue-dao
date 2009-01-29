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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.querydatabean.QueryDataBean;
import edu.wustl.common.util.PagenatedResultData;
import edu.wustl.common.util.QueryParams;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.formatmessage.IDBExceptionFormatter;
import edu.wustl.dao.util.DAOConstants;
import edu.wustl.dao.util.DatabaseConnectionParams;
import edu.wustl.query.executor.AbstractQueryExecutor;
import edu.wustl.security.exception.SMException;


/**
 * @author kalpana_thakur
 *
 */
public abstract class AbstractJDBCDAOImpl extends AbstractDAOImpl implements JDBCDAO
{
	/**
	 * Class Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(AbstractJDBCDAOImpl.class);

	/**
	 * Connection.
	 */
	private Connection connection = null ;

	/**
	 * This will hold all database specific properties.
	 */
	private DatabaseProperties databaseProperties;

	/**
	 * batch statement.
	 */
	private Statement batchStatement;

	/**
	 * It holds the batch size.
	 */
	private transient int  batchCounter = 0;

	/**
	 * Batch size.
	 */
	private int batchSize = 1;

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
			super.openSession(sessionDataBean);
			connection = connectionManager.getConnection();
			initializeBatchstmt();
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
			super.closeSession();
			connectionManager.closeConnection();
			batchStatement = null;
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
			super.commit();
			commitUpdate();
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
		clearBatch();
		connectionManager.rollback();

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
	 * This method executed query, parses the result and returns List of rows after doing security checks
	 * for user's right to view a record/field.
	 * @param queryParams : TODO
	 * @return This will return the PagenatedResultData.
	 * @throws DAOException :DAOException
	 */
	public PagenatedResultData getQueryResultList(QueryParams queryParams) throws DAOException
	{
		PagenatedResultData pagenatedResultData = null;
		try
		{
			Class queryExeClass = Class.forName(databaseProperties.getQueryExecutorName());
			AbstractQueryExecutor queryExecutor = (AbstractQueryExecutor)queryExeClass.newInstance();
			pagenatedResultData = queryExecutor.getQueryResultList(queryParams);

		}
		catch(Exception exp)
		{
			logger.fatal(exp.getMessage(), exp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"AbstractJDBCDAOImpl.java :"+
					DAOConstants.EXECUTE_QUERY_ERROR);
		}
		return pagenatedResultData;
	}

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
	 * This method will be called to set the size of the batch.
	 * @param batchSize batchSize
	 * @throws DAOException : Generic database exception.
	 */
	public void setBatchSize(int batchSize) throws DAOException
	{
		this.batchSize = batchSize;
	}

	/**
	 * Adds the given SQL command to the current list of commands for
     * batchStatement object. The commands in this list can be
     * executed as a batch by calling the method executeBatch
	 @param sql typically this is a static SQL INSERT or
     * UPDATE statement
	 * @throws DAOException : Generic database exception.
	 */
	private void addSQLToBatch(String sql) throws DAOException
	{
		try
		{
			batchStatement.addBatch(sql);

			if(++batchCounter >= batchSize)
			{
				batchStatement.executeBatch();
				clearBatch();
			}
		}
		catch (SQLException exp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"AbstractJDBCDAOImpl.java :"+
				DAOConstants.BATCH_UPDATE_ERROR);
		}

	}

	/**
	 * This method will be called for batch update insert.
	 * @throws DAOException :Generic DAOException.
	 */
	private void commitUpdate() throws DAOException
	{
		try
		{
			if(batchCounter != 0 )
			{
				batchStatement.executeBatch();
			}
			connectionManager.commit();
			clearBatch();

		}
		catch (SQLException exp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"AbstractJDBCDAOImpl.java :"+
					DAOConstants.BATCH_UPDATE_ERROR);
		}
	}


	/**
	 * This method will be called to clear the batch.
	 * @throws DAOException :Generic DAOException.
	 */
	private void clearBatch() throws DAOException
	{
		logger.debug("Clear the batch");
		try
		{
			batchCounter = 0;
			if(batchStatement != null)
			{
				batchStatement.clearBatch();
			}
		}
		catch (SQLException exp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"AbstractJDBCDAOImpl.java :");
		}
	}
	/**
	 * Adds the given SQL command to the current list of commands for
     * batchStatement object.
	 *@param sql typically this is a static SQL INSERT or
     * UPDATE statement
	 * @param isAuditable : is auditable.
	 * @throws DAOException : Generic database exception.
	 */
	public void insert(String sql,boolean isAuditable)
			throws DAOException
	{
		logger.debug("Add DML to batch");
		validate(sql);
		addSQLToBatch(sql);

	}

	/**
	 * This method will be called to validate batch query.
	 * @param sql : This is a static SQL INSERT or
     * UPDATE statement
	 * @throws DAOException : database exception
	 */
	private void validate(String sql) throws DAOException
	{
		if(!(sql.contains("insert") || sql.contains("update")))
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("dao.batch.query.error");
			throw new DAOException(errorKey,null,"AbstractJDBCDAOImpl.java :");
		}
	}

	/**
	 * This method will be called to set the batch statement.
	 * @throws DAOException : Database exception.
	 */
	private void initializeBatchstmt() throws DAOException
	{
		logger.debug("Initialize batch statement");
		try
		{
			batchStatement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			ResultSet.CONCUR_UPDATABLE);

		}
		catch (SQLException exp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"AbstractJDBCDAOImpl.java :"+
				DAOConstants.STMT_CREATION_ERROR);
		}
	}

	/**
	 *This method will be called to get all database properties.
	 * @return database properties.
	 */
	public DatabaseProperties getDatabaseProperties()
	{
		return databaseProperties;
	}

	/**
	 * This method will be called to set all the database specific properties.
	 * @param databaseProperties : database properties.
	 */
	public void setDatabaseProperties(DatabaseProperties databaseProperties)
	{
		this.databaseProperties = databaseProperties;
	}

	/**
	 * @return :This will return the Date Pattern.
	 */
	public String getDatePattern()
	{
		return databaseProperties.getDatePattern();
	}

	/**
	 * @return :This will return the Time Pattern.
	 */
	public String getTimePattern()
	{
		return databaseProperties.getTimePattern();
	}
	/**
	 * @return :This will return the Date Format Function.
	 */
	public String getDateFormatFunction()
	{
		return databaseProperties.getDateFormatFunction();
	}
	/**
	 * @return :This will return the Time Format Function.
	 */
	public String getTimeFormatFunction()
	{
		return databaseProperties.getTimeFormatFunction();
	}

	/**
	 * @return :This will return the Date to string function
	 */
	public String getDateTostrFunction()
	{
		return databaseProperties.getDateTostrFunction();
	}
	/**
	 * @return :This will return the string to Date function
	 */
	public String getStrTodateFunction()
	{

		return databaseProperties.getStrTodateFunction();
	}

	/**
	 * @param excp : Exception Object.
	 * @param connection :
	 * @return : It will return the formated messages.
	 * @throws DAOException : database exception
	 */
	public String formatMessage(Exception excp,Connection connection)throws DAOException
	{
		String formattedMsg;
		try
		{
			Class formatterClass = Class.forName(databaseProperties.getExceptionFormatterName());
			IDBExceptionFormatter formatter =  (IDBExceptionFormatter)formatterClass.newInstance();
			formattedMsg =  formatter.getFormatedMessage(excp,connection);
		}
		catch(Exception exp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"AbstractJDBCDAOImpl.java :");
		}
		return formattedMsg;
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
	public Object retrieveById(String sourceObjectName, Long identifier)
			throws DAOException
	{
		ErrorKey errorKey = ErrorKey.getErrorKey("dao.method.without.implementation");
		throw new DAOException(errorKey,new Exception(),"AbstractJDBCDAOImpl.java :");
	}

	/**
	 * @param excp : Exception Object. Refactoring
	 * @return : It will return the formated messages.
	 * @throws DAOException : DAO exception.
	 */
	public String formatMessage(Exception excp)
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
	 *@throws DAOException :Generic DAOException.
	 */
	public void closeCleanConnection() throws DAOException
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

	/**
	 * @param obj : object to be deleted
	 * @throws DAOException : daoExp
	 */
	public void delete(Object obj) throws DAOException
	{
		ErrorKey errorKey = ErrorKey.getErrorKey("dao.method.without.implementation");
		throw new DAOException(errorKey,new Exception(),"AbstractJDBCDAOImpl.java :");
	}

	/**
	 * Insert the Object in the database.
	 * @param obj Object to be inserted in database
	 * @param isAuditable is Auditable.
	 * @throws DAOException generic DAOException
	 */
	public void insert(Object obj, boolean isAuditable) throws DAOException
	{
		ErrorKey errorKey = ErrorKey.getErrorKey("dao.method.without.implementation");
		throw new DAOException(errorKey,new Exception(),"AbstractJDBCDAOImpl.java :");
	}

	/**
	 * This method executes the named query and returns the results.
	 * @param queryName : handle for named query.
	 * @return result as list of Object
	 * @throws DAOException : daoExp
	 */
	public Collection executeNamedQuery(String queryName)throws DAOException
	{
		ErrorKey errorKey = ErrorKey.getErrorKey("dao.method.without.implementation");
		throw new DAOException(errorKey,new Exception(),"AbstractJDBCDAOImpl.java :");
	}

	/**
	 * This method returns named query.
	 * @param queryName : handle for named query.
	 * @return Query named query
	 * @throws DAOException : daoExp
	 */
	public Query getNamedQuery(String queryName)throws DAOException
	{
		ErrorKey errorKey = ErrorKey.getErrorKey("dao.method.without.implementation");
		throw new DAOException(errorKey,new Exception(),"AbstractJDBCDAOImpl.java :");
	}

}
