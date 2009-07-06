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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.condition.EqualClause;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.dao.query.generator.DBTypes;
import edu.wustl.dao.util.DAOConstants;
import edu.wustl.dao.util.DAOUtility;
import edu.wustl.dao.util.StatementData;


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
	private PreparedStatement prepBatchStatement;

	/**
	 * Query preparedStatement.
	 */
	private PreparedStatement preparedStatement;

	/**
	 * This will maintain the list of all statements.
	 */
	private List<Statement> openedStmts = new ArrayList<Statement>();

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
			connection = connectionManager.getConnection();
		}
		catch (Exception sqlExp)
		{
			throw DAOUtility.getInstance().getDAOException(sqlExp, "db.open.session.error",
					"AbstractJDBCDAOImpl.java ");
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
			connectionManager.closeConnection();
			batchClose();
			closeConnectionParams();
		}
		catch(Exception dbex)
		{
			throw DAOUtility.getInstance().getDAOException(dbex, "db.close.conn.error",
					"AbstractJDBCDAOImpl.java ");
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
		logger.debug("Session commit");
		try
		{
			batchCommit();
			connectionManager.commit();
		}
		catch (DAOException exp)
		{
			throw DAOUtility.getInstance().getDAOException(exp, "db.commit.error",
					"AbstractJDBCDAOImpl.java");
		}
	}

	/**
	 * Commit the database level changes.
	 * Declared in DAO class.
	 * @throws DAOException : It will throw DAOException
	 * @throws SMException
	 *//*
	public void commitTransaction() throws DAOException
	{
		logger.debug("Session commit");
			connectionManager.commit();

	}*/
/*
	 *//**
	 * This method will be called to open new transaction.
	 * @throws DAOException database exception.
	 *//*
	public void openTransaction()throws DAOException
	{
		try
		{
			connectionManager.openTransaction();
		}
		catch(HibernateException exp)
		{
			throw DAOUtility.getInstance().getDAOException(exp, "db.open.transaction.error",
			"AbstractJDBCDAOImpl.java");
		}
	}*/
	/**
	 * RollBack all the changes after last commit.
	 * Declared in DAO class.
	 * @throws DAOException : It will throw DAOException.
	 */
	public void rollback() throws DAOException
	{
		try
		{
			logger.debug("Session rollback");
			connectionManager.rollback();
		}
		catch (Exception exp)
		{
			throw DAOUtility.getInstance().getDAOException(exp, "db.rollback.error",
					"AbstractJDBCDAOImpl.java");
		}

	}

	/**
	 * @param batchSize :batchSize
	 * @param tableName : name of the table
	 * @param columnSet : columns
	 * @throws DAOException : database exception
	 */
	public void batchInitialize(int batchSize,String tableName,SortedSet<String> columnSet)
	throws DAOException
	{
		logger.debug("Initialize batch");
		try
		{
			validateBatchParams(batchSize,tableName,columnSet);
			batchCounter = 0;
			setBatchSize(batchSize);
			String sql = generateQuery(tableName,columnSet);
			prepBatchStatement = connection.prepareStatement(sql);
		}
		catch (SQLException exp)
		{
			throw DAOUtility.getInstance().getDAOException
			(exp, "db.batch.initialization.error", "AbstractJDBCDAOImpl.java");
		}
	}

	/**
	 * @param batchSize : set the batch size
	 * @param tableName : name of the table
	 * @param columnSet : set of columns
	 * @throws DAOException : database exception.
	 */
	private void validateBatchParams(int batchSize,String tableName,
			SortedSet<String> columnSet) throws DAOException
	{
		Validator validator = new Validator();
		if(batchSize == 0 || !validator.isNumeric(String.valueOf(batchSize)))
		{
			throw DAOUtility.getInstance().getDAOException
			(null, "db.batch.size.issue", "AbstractJDBCDAOImpl.java");
		}

		if(Validator.isEmpty(tableName))
		{
			throw DAOUtility.getInstance().getDAOException
			(null, "db.table.name.empty", "AbstractJDBCDAOImpl.java ");
		}
		if(columnSet == null || columnSet.isEmpty())
		{
			throw DAOUtility.getInstance().getDAOException
			(null, "db.column.set.empty", "AbstractJDBCDAOImpl.java");
		}
	}

	/**
	 * @param dataMap Map holding the column value data.
	 * @throws DAOException : database exception.
	 */
	public void batchInsert(SortedMap<String,ColumnValueBean> dataMap)throws DAOException
	{
		logger.debug("insert batch");
		try
		{
			if(prepBatchStatement == null)
			{
				ErrorKey errorKey = ErrorKey.getErrorKey("db.batch.initialization.error");
				throw new DAOException(errorKey,null,"AbstractJDBCDAOImpl.java :");
			}

			Iterator<String> columns = dataMap.keySet().iterator();
			int columnIndex = 1;
			while(columns.hasNext())
			{
				String column = columns.next();
				ColumnValueBean colValueBean = dataMap.get(column);
				if(colValueBean.getColumnType() == DBTypes.DATE)
				{
				  prepBatchStatement.setDate(columnIndex,
						  setDateToPrepStmt(colValueBean));
				}
				else if(colValueBean.getColumnType() == DBTypes.TIMESTAMP)
				{
				  prepBatchStatement.setTimestamp(columnIndex,
						  setTimeStampToPrepStmt(colValueBean));
				}
				else
				{
					prepBatchStatement.setObject(columnIndex, colValueBean.getColumnValue());
				}
				columnIndex += 1;
			}
			prepBatchStatement.addBatch();
			batchCounter += 1;
			if(batchCounter >= batchSize)
			{
				prepBatchStatement.executeBatch();
				prepBatchStatement.clearBatch();
				batchCounter = 0;
			}


		}
		catch (Exception exp)
		{
			throw DAOUtility.getInstance().getDAOException
			(exp, "db.batch.insert.error", "AbstractJDBCDAOImpl.java");
		}

	}

	/**
	 * @param colValueBean : column data bean
	 * @throws SQLException : SQL exception.
	 * @throws DAOException database exception.
	 * @return Timestamp date time value.
	 */
	private Timestamp setTimeStampToPrepStmt(ColumnValueBean colValueBean)
	throws SQLException, DAOException
	{

		if(!(colValueBean.getColumnValue() instanceof Timestamp))
		{
			throw DAOUtility.getInstance().getDAOException
			(null, "db.dataType.invalid", "AbstractJDBCDAOImpl.java");
		}
		Timestamp dateTime = (Timestamp)colValueBean.getColumnValue();
		return dateTime;

	}

	/**
	 * @param colValueBean : column data bean
	 * @throws SQLException : SQL exception.
	 * @throws DAOException database exception.
	 * @return Date date value
	 */
	private java.sql.Date setDateToPrepStmt(ColumnValueBean colValueBean)
			throws SQLException, DAOException
	{
		if(!(colValueBean.getColumnValue() instanceof Date))
		{
			throw DAOUtility.getInstance().getDAOException
			(null, "db.dataType.invalid", "AbstractJDBCDAOImpl.java");
		}
		Date date = (Date)colValueBean.getColumnValue();
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		return sqlDate;
	}
	/**
	 * This method will be called to commit batch updates.
	 * @throws DAOException : Database exception
	 */
	public void batchCommit()throws DAOException
	{
		try
		{
			if(prepBatchStatement != null)
			{
				 if(batchCounter != 0)
				 {
					 prepBatchStatement.executeBatch();
				 }
				batchCounter = 0;
				connection.commit();
			}

		}
		catch (SQLException exp)
		{
			throw DAOUtility.getInstance().getDAOException
			(exp, "db.batch.commit.error", "AbstractJDBCDAOImpl.java");

		}
	}

	/**
	 * Close the batch statement.
	 * @throws DAOException Database exception
	 */
	public void batchClose()throws DAOException
	{
		try
		{
			if(prepBatchStatement != null)
			{
				prepBatchStatement.close();
				preparedStatement = null;
			}
		}
		catch (SQLException exp)
		{
			throw DAOUtility.getInstance().getDAOException
			(exp, "db.batch.close.error", "AbstractJDBCDAOImpl.java");

		}

	}

	/**
	 * @param tableName :
	 * @param columnSet set of column names
	 * @return String SQL
	 */
	private String generateQuery(String tableName,SortedSet<String> columnSet)
	{
		logger.debug("Generate String");

		StringBuffer insertSql = new StringBuffer(DAOConstants.TRAILING_SPACES);
		StringBuffer valuePart = new StringBuffer(DAOConstants.TRAILING_SPACES);
		insertSql.append("insert into").append(DAOConstants.TRAILING_SPACES).
		append(tableName).append(" (");
		valuePart.append("values (");
		Iterator<String> columns = columnSet.iterator();

		while(columns.hasNext())
		{
			insertSql.append(columns.next().toString());
			valuePart.append('?');
			if(columns.hasNext())
			{
				insertSql.append(DAOConstants.SPLIT_OPERATOR);
				valuePart.append(DAOConstants.SPLIT_OPERATOR);
			}

		}
		insertSql.append(" )");
		valuePart.append(" )");

		insertSql.append(valuePart.toString());
		logger.debug("Sql String:"+insertSql.toString());
		return insertSql.toString();

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
	public ResultSet retrieveResultSet(String sourceObjectName, String[] selectColumnName,
			QueryWhereClause queryWhereClause,
			 boolean onlyDistinctRows) throws DAOException
	{
		try
		{
			StringBuffer queryStrBuff = generateSQL(sourceObjectName,
				selectColumnName, queryWhereClause, onlyDistinctRows);
			return getQueryResultSet(queryStrBuff.toString());
		}
		catch (Exception exp)
		{
			throw DAOUtility.getInstance().getDAOException
			(exp, "db.retrieve.data.error", "AbstractJDBCDAOImpl.java ");

		}

	}

	/**
	 * Retrieves the records for class name in sourceObjectName according to
	 * field values passed in the passed session.
	 * @param sourceObjectName This will holds the object name.
	 * @param selectColumnName An array of field names in select clause.
	 * @param queryWhereClause This will hold the where clause.
	 * @param onlyDistinctRows True if only distinct rows should be selected
	 * @return The list containing all the rows from the table represented
	 * in sourceObjectName which satisfies the where condition
	 * @throws DAOException : DAOException
	 */
	public List retrieve(String sourceObjectName, String[] selectColumnName,
			QueryWhereClause queryWhereClause,
			 boolean onlyDistinctRows) throws DAOException
	{
		logger.debug("Inside retrieve method");
		try
		{
			StringBuffer queryStrBuff = generateSQL(sourceObjectName,
					selectColumnName, queryWhereClause, onlyDistinctRows);
			List list  = executeQuery(queryStrBuff.toString());
			return list;
		}
		catch (Exception exp)
		{
			throw DAOUtility.getInstance().getDAOException
			(exp, "db.retrieve.data.error", "AbstractJDBCDAOImpl.java ");

		}

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
	 */
	private StringBuffer generateSQL(String sourceObjectName,
			String[] selectColumnName, QueryWhereClause queryWhereClause,
			boolean onlyDistinctRows)
	{
		StringBuffer queryStrBuff = getSelectPartOfQuery(selectColumnName, onlyDistinctRows);
		logger.debug("Prepare from part of the query");
		queryStrBuff.append("FROM ").append(sourceObjectName);

		if(queryWhereClause != null)
		{
			queryStrBuff.append(queryWhereClause.toWhereClause());
		}

		logger.debug("JDBC Query " + queryStrBuff);
		return queryStrBuff;
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
	 * This method will be called for executing a static SQL statement.
	 * @see edu.wustl.dao.JDBCDAO#executeUpdate(java.lang.String)
	 * @return (1) the row count for INSERT,UPDATE or DELETE statements
	 * or (2) 0 for SQL statements that return nothing
	 * @param query :Holds the query string.
	 * @throws DAOException : DAOException.
	 */
	public StatementData executeUpdate(String query) throws DAOException
	{
		logger.debug("Execute query.");
		Statement statement = null;
		StatementData statementData = new StatementData();
		try
		{
			statement = createStatement();
			statementData.setRowCount(statement.executeUpdate(query,Statement.RETURN_GENERATED_KEYS));
			setStatementData(statement, statementData,query);
			return statementData ;
		}
		catch (SQLException sqlExp)
		{
			throw DAOUtility.getInstance().getDAOException(sqlExp, "db.update.data.error",
			"AbstractJDBCDAOImpl.java :   "+query);
		}

	}

	/**
	 * Remove statement from opened statements list.
	 * @param statement statement instance
	 * @throws DAOException database exception.
	 */
	private void removeStmts(Statement statement)throws DAOException
	{
		try
		{
			statement.close();
			openedStmts.remove(statement);
		}
		catch (SQLException exp)
		{
			throw DAOUtility.getInstance().getDAOException(exp, "db.stmt.close.error",
					"AbstractJDBCDAOImpl.java ");
		}
	}

	/**
	 * This method will be called to get the result set.
	 * @param sql SQL statement.
	 * @throws DAOException generic DAOException.
	 * @return ResultSet : ResultSet
	 */
	public ResultSet getQueryResultSet(String sql) throws DAOException
	{

		logger.debug("Get Query RS");
		try
		{

			Statement statement = createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			return resultSet;
		}
		catch (SQLException exp)
		{
			throw DAOUtility.getInstance().getDAOException(exp, "db.retrieve.data.error",
			"AbstractJDBCDAOImpl.java  "+sql);

		}
	}

	/**
	 *This method will be called to close current connection.
	 *@param query :
	 *@throws DAOException :Generic DAOException.
	 *@return list
	 */
	public List executeQuery(String query) throws DAOException
	{

		logger.debug("get list from RS");
		try
		{
			ResultSet resultSet = getQueryResultSet(query);
			logger.debug("RS"+resultSet);
			List resultData =  DAOUtility.getInstance().getListFromRS(resultSet);
			closeStatement(resultSet);
			return resultData;
		}
		catch(SQLException exp)
		{
			throw DAOUtility.getInstance().getDAOException(exp, "db.retrieve.data.error",
			"AbstractJDBCDAOImpl.java "+query);
		}

	}

	/**
	 * This method will be called to execute query.
	 * @param sql query string.
	 * @param columnValueBeanSet :Bean having column name ,
	 * column value,and column type.
	 * @return (1) the row count for INSERT,UPDATE or DELETE statements
	 * or (2) 0 for SQL statements that return nothing
	 * @throws DAOException :Generic Exception
	 */
	public StatementData executeUpdate(String sql,LinkedList<ColumnValueBean> columnValueBeanSet)
	throws DAOException
	{
		try
		{
			if(!sql.contains("?") || columnValueBeanSet == null || columnValueBeanSet.isEmpty())
			{
				throw DAOUtility.getInstance().getDAOException(null, "db.prepstmt.param.error",
				"AbstractJDBCDAOImpl.java  "+sql);
			}

			PreparedStatement stmt = getPreparedStatement(sql);

			Iterator<ColumnValueBean> colValItr =  columnValueBeanSet.iterator();
			int index = 1;
			while(colValItr.hasNext())
			{
				ColumnValueBean colValueBean = colValItr.next();

				if(colValueBean.getColumnType() == DBTypes.DATE)
				{
					stmt.setDate(index,setDateToPrepStmt(colValueBean));
				}
				else if(colValueBean.getColumnType() == DBTypes.TIMESTAMP)
				{
					stmt.setTimestamp(index,setTimeStampToPrepStmt(colValueBean));
				}
				else
				{
					stmt.setObject(index, colValueBean.getColumnValue());
				}

				index += 1;
			}
			StatementData statementData = new StatementData();
			statementData.setRowCount(stmt.executeUpdate());
			setStatementData(stmt, statementData,sql);
			return statementData;
		}
		catch (SQLException sqlExp)
		{
			throw DAOUtility.getInstance().getDAOException(sqlExp, "db.update.data.error",
			"AbstractJDBCDAOImpl.java   "+sql);
		}
		finally
		{
			closePreparedStmt();
		}
	}


	/**
	 * @param stmt statement instance.
	 * @param statementData statement data
	 * @param sql query string
	 * @throws SQLException SQL exception
	 */
	private void setStatementData(Statement stmt,StatementData statementData,String sql)
	throws SQLException
	{
		String token = DAOUtility.getInstance().getToken(sql, "insert".length());
		if(token.compareToIgnoreCase("insert") == 0)
		{
			statementData.setGeneratedKeys(stmt.getGeneratedKeys());
		}
		statementData.setFetchSize(stmt.getFetchSize());
		statementData.setMaxFieldSize(stmt.getMaxFieldSize());
		statementData.setMaxRows(stmt.getMaxRows());
	}

	/**
	 * This method will return the Query prepared statement.
	 * @return Statement statement.
	 * @throws DAOException :Generic Exception
	 */
	private Statement createStatement()throws DAOException
	{
		try
		{
			Statement statement = (Statement)connection.createStatement();
			openedStmts.add(statement);
			return statement;
		}
		catch (SQLException sqlExp)
		{
			throw DAOUtility.getInstance().getDAOException(sqlExp, "db.stmt.creation.error",
			"AbstractJDBCDAOImpl.java ");
		}
	}

	/**
	 * This method will return the Query prepared statement.
	 * Cider specific. please make sure stmt getting closed.
	 * @param query :Query String
	 * @return PreparedStatement.
	 * @throws DAOException :Generic Exception
     * @deprecated Do not use this method.
	 */
	public PreparedStatement getPreparedStatement(String query) throws DAOException
	{
		try
		{
			// closePreparedStmt();
			preparedStatement = (PreparedStatement) connection.prepareStatement
			(query,Statement.RETURN_GENERATED_KEYS);
			return preparedStatement;
		}
		catch (SQLException sqlExp)
		{
			throw DAOUtility.getInstance().getDAOException(sqlExp, "db.stmt.creation.error",
			"AbstractJDBCDAOImpl.java ");
		}
	}

	/**
     * Retrieves a DatabaseMetaData object that contains
     * metadata about the database to which this
     * Connection  object represents a connection.
     * The metadata includes information about the database's
     * tables, its supported SQL grammar, its stored
     * procedures, the capabilities of this connection, and so on.
     *@param tableName : table name must match the table name as it is stored
     *in this database
     * @return a  ResultSet  for this
     *          Connection  object
     * @exception DAOException if a database access error occurs
     */
   public ResultSet getDBMetaDataResultSet(String tableName) throws DAOException
   {
	   try
		{
		   ResultSet resultSet = connection.getMetaData().
		   getIndexInfo(connection.getCatalog(), null,tableName, true, false);
		   return resultSet;
		}
	    catch (SQLException sqlExp)
		{
	    	throw DAOUtility.getInstance().getDAOException(sqlExp, "db.retrieve.data.error",
				"AbstractJDBCDAOImpl.java ");
		}
   }
	/**
	 * This method will be called to close all the Database connections.
	 * @throws DAOException :Generic Exception
	 */
	protected void closeConnectionParams()throws DAOException
	{
			closeStmt();
			closePreparedStmt();
	}

	/**
	 * Closes the prepared statement if open.
	 * @throws DAOException : DAO exception
	 */
	private void closePreparedStmt() throws DAOException
	{

		try
		{
			if (preparedStatement != null)
			{
				preparedStatement.close();
				preparedStatement = null;
			}
		}
		catch(SQLException sqlExp)
		{
			throw DAOUtility.getInstance().getDAOException(sqlExp, "db.stmt.close.error",
					"AbstractJDBCDAOImpl.java ");
		}
	}

	/**
	 * Closes the statement if open.
	 * @throws DAOException : DAO exception
	 */
	private void closeStmt() throws DAOException
	{
		try
		{
			Iterator<Statement> stmtIterator = openedStmts.iterator();
			while(stmtIterator.hasNext())
			{
				Statement stmt = stmtIterator.next();
				/*do
				{
					ResultSet resultSet = stmt.getResultSet();
					if(resultSet != null)
					{
						resultSet.close();
					}
				}while(stmt.getMoreResults());*/

				stmt.close();
			}

		}
		catch(SQLException sqlExp)
		{
			throw DAOUtility.getInstance().getDAOException(sqlExp, "db.stmt.close.error",
					"AbstractJDBCDAOImpl.java ");
		}
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
	 * Returns the ResultSet containing all the rows in the table represented in sourceObjectName.
	 * @param sourceObjectName The table name.
	 * @return The ResultSet containing all the rows in the table represented in sourceObjectName.
	 * @throws ClassNotFoundException
	 * @throws DAOException generic DAOException
	 */
	public List retrieve(String sourceObjectName) throws DAOException
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
	public List retrieve(String sourceObjectName, String[] selectColumnName) throws DAOException
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
	public List retrieve(String sourceObjectName, String[] selectColumnName,
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
	public List retrieve(String sourceObjectName,
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
	public List retrieve(String sourceObjectName, String whereColumnName, Object whereColumnValue)
			throws DAOException
	{
		logger.debug("Inside retrieve method");
		String[] selectColumnName = null;

		QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
		queryWhereClause.addCondition(new EqualClause(whereColumnName,whereColumnValue,sourceObjectName));

		return retrieve(sourceObjectName, selectColumnName,queryWhereClause,false);
	}

	/**
	 * This method has been added to close statement for which resultset is returned.
	 * @param resultSet ResultSet
	 * @throws DAOException : database exception
	 */
	public void closeStatement(ResultSet resultSet) throws DAOException
	{
		try
		{
			Statement stmt=resultSet.getStatement();
			removeStmts(stmt);
		}
		catch(SQLException sqlExp)
		{
			throw DAOUtility.getInstance().getDAOException(sqlExp, "db.stmt.close.error",
			"AbstractJDBCDAOImpl.java ");
		}
	}

	/**
     * This method added only for CIDER, next releases handles this issue.(Quick/Patch fix)
     * * @deprecated
     */
	public void commitConnection() throws DAOException
	{
		try
		{
			connection.commit();
		}
		catch (SQLException sqlExp)
		{
			throw DAOUtility.getInstance().getDAOException(sqlExp, "db.stmt.close.error",
			"AbstractJDBCDAOImpl.java ");
		}
	}
}
