/**
 * <p>Title: DatabaseConnectionParams Class>
 * <p>Description:	DatabaseConnectionParams handles opening closing ,initialization of all database specific
 * parameters  .</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @version 1.00
 * @author kalpana_thakur
 */
package edu.wustl.dao.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;

/**
 * @author kalpana_thakur
 * This class will handles all database specific parameters.
 */

public class DatabaseConnectionUtiliy
{

	/**
	 * Connection statement.
	 */
	private Statement statement;
	/**
	 * Query resultSet.
	 */
	private ResultSet resultSet;
	/**
	 * connection object.
	 */
	private Connection connection;
	/**
	 * Query preparedStatement.
	 */
	private PreparedStatement preparedStatement;

	/**
	 * class logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(DatabaseConnectionUtiliy.class);

	/**
	 * This method will be called to create new connection statement.
	 * @throws DAOException :Generic Exception
	 * @return Statement
	 */
	public Statement getDatabaseStatement()throws DAOException
	{
		try
		{
			statement = connection.createStatement();
			return statement;
		}
		catch (SQLException sqlExp)
		{
			logger.fatal(sqlExp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.conn.para.creation.error");
			throw new DAOException(errorKey,sqlExp,"DatabaseConnectionParams.java :"+
					DAOConstants.STMT_CREATION_ERROR);
		}


	}

	/**
	 * This method will return the query ResultSet.
	 * @param query Query String
	 * @throws DAOException :Generic Exception
	 * @return ResultSet
	 */
	public ResultSet getResultSet(String query) throws DAOException
	{
		try
		{
			statement = getDatabaseStatement();
			resultSet = statement.executeQuery(query);
			return resultSet;
		}
		catch (SQLException sqlExp)
		{
			logger.fatal(sqlExp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.conn.para.creation.error");
			throw new DAOException(errorKey,sqlExp,"DatabaseConnectionParams.java :"+
					DAOConstants.RESULTSET_CREATION_ERROR);
		}

	}


	/**
	 * This method will be called to return query meta data.
	 * @param query : Query String
	 * @return ResultSetMetaData
	 * @throws DAOException :Generic Exception
	 */
	public ResultSetMetaData getMetaData(String query)throws DAOException
	{

		try
		{

			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			ResultSetMetaData metaData = resultSet.getMetaData();
			return metaData;
		}
		catch (SQLException sqlExp)
		{
			logger.fatal(sqlExp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.conn.para.creation.error");
			throw new DAOException(errorKey,sqlExp,"DatabaseConnectionParams.java :"+
					DAOConstants.RS_METADATA_ERROR);
		}
	}

	/**
	 * This method will be called to close all the Database connections.
	 * @throws DAOException :Generic Exception
	 */
	public void closeConnectionParams()throws DAOException
	{
		try
		{
			if(resultSet != null )
			{
				resultSet.close();
				resultSet = null;
			}
			if (statement != null)
			{
				statement.close();
				statement = null;
			}
			if (preparedStatement != null)
			{
				preparedStatement.close();
				statement = null;
			}

		}
		catch(SQLException sqlExp)
		{
			logger.fatal(sqlExp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.close.conn.error");
			throw new DAOException(errorKey,sqlExp,"DatabaseConnectionParams.java :"+
					DAOConstants.CLOSE_CONN_ERR);
		}
	}

	/**
	 * This method will return the Query prepared statement.
	 * @param query :Query String
	 * @return PreparedStatement.
	 * @throws DAOException :Generic Exception
	 */
	public PreparedStatement getPreparedStatement(String query) throws DAOException
	{
		try
		{
			preparedStatement = (PreparedStatement) connection.prepareStatement(query);
		}
		catch (SQLException sqlExp)
		{
			logger.fatal(sqlExp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.conn.para.creation.error");
			throw new DAOException(errorKey,sqlExp,"DatabaseConnectionParams.java :"+
					DAOConstants.PRPD_STMT_ERROR);
		}
		return preparedStatement;
	}

	/**
	 * This method will be called to execute query.
	 * @param query :query string.
	 * @return (1) the row count for INSERT,UPDATE or DELETE statements
	 * or (2) 0 for SQL statements that return nothing
	 * @throws DAOException :Generic Exception
	 */
	public int executeUpdate(String query) throws DAOException
	{

		try
		{
			PreparedStatement stmt = getPreparedStatement(query);
			return stmt.executeUpdate();
		}
		catch (SQLException sqlExp)
		{
			logger.fatal(sqlExp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,sqlExp,"DatabaseConnectionParams.java :"
					+DAOConstants.EXECUTE_QUERY_ERROR+"   "+query);
		}

	}

	/**
	 * @return This method will return the connection object.
	 */
	public Connection getConnection()
	{
		return connection;
	}

	/**
	 * This method will be called to set connection Object.
	 * @param connection :connection object.
	 */
	public void setConnection(Connection connection)
	{
			this.connection = connection;
	}

	/**
	 * This method will be called to execute query.
	 * @param query :query string.
	 * @return ResultSet result set
	 * @throws DAOException :Generic Exception
	 */
	public ResultSet getQueryRS(String query) throws DAOException
	{
		logger.debug("Get Query RS");
		try
		{
			PreparedStatement stmt = getPreparedStatement(query);
			resultSet = stmt.executeQuery();
			return resultSet;
		}
		catch (SQLException exp)
		{
			logger.fatal(exp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"DatabaseConnectionParams.java :"
					+DAOConstants.EXECUTE_QUERY_ERROR+"   "+query);
		}
	}

	/**
	 * Checks result set.
	 * @return :true if result set exists.
	 * @param query : query String
	 * @throws DAOException : DAOException
	 */
	public boolean isResultSetExists(String query)throws DAOException
	{
		boolean isResultSetExists = false;
		try
		{

			resultSet = getResultSet(query);
			if(resultSet.next())
			{
				isResultSetExists = true;
			}

		}
		catch(SQLException exp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"DatabaseConnectionParams.java :"+
					DAOConstants.RS_METADATA_ERROR);
		}
		return isResultSetExists;
	}

	/**
	 *To get database meta data object for the connection.
	 * @return Database meta data.
	 * @throws DAOException  :
	 */
	public DatabaseMetaData getDatabaseMetaData() throws DAOException
	{
		try
		{
			return (DatabaseMetaData)connection.getMetaData();
		}
		catch(SQLException exp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"DatabaseConnectionParams.java :"+
					DAOConstants.RS_METADATA_ERROR);
		}
	}

	/**
	 * @param  query : sql string.
	 * @return : list of data.
	 * @throws DAOException : database exception
	 */
	public List getListFromRS(String query) throws DAOException
	{
		logger.debug("get list from RS");
		List list = new ArrayList();
		try
		{
			getQueryRS(query);
			ResultSetMetaData metaData = resultSet.getMetaData();
			int columnCount = metaData.getColumnCount();
			while (resultSet.next())
			{
				updateList(list,columnCount,metaData);
			}
		}
		catch(SQLException exp)
		{
			logger.fatal(exp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"DatabaseConnectionParams.java :"+
					DAOConstants.RS_METADATA_ERROR);
		}
		return list;
	}

	/**
	 * This method will read the resultSet and update the list.
	 * @param list :list of data
	 * @param columnCount : number of columns
	 * @param metaData : meta data
	 * @throws SQLException : exception
	 */
	private void updateList(List list,int columnCount,ResultSetMetaData metaData) throws SQLException
	{
		for (int i = 1; i <= columnCount; i++)
		{
			logger.debug("Inside for "+i);
			Object retObj;
			switch (metaData.getColumnType(i))
			{
			case Types.CLOB :
				retObj = resultSet.getObject(i);
				break;
			case Types.DATE :
			case Types.TIMESTAMP :
				retObj = resultSet.getTimestamp(i);
				if (retObj == null)
				{
					break;
				}
				SimpleDateFormat formatter = new SimpleDateFormat(
						DAOConstants.DATE_PATTERN_MM_DD_YYYY + " "
						+ DAOConstants.TIME_PATTERN_HH_MM_SS);
				retObj = formatter.format((java.util.Date) retObj);
				break;
			default :
				retObj = resultSet.getObject(i);
				if (retObj != null)
				{
					retObj = retObj.toString();
				}
			}
			if (retObj == null)
			{
				list.add("");
			}
			else
			{
				list.add(retObj);
			}
			logger.debug("list size "+list.size());
		}
	}

	/**
	 *For temporary use ..
	 *TODO will be removed when getCleanConnection get removed.
	 * @throws DAOException :database exception.
	 */
	public void commit() throws DAOException
	{
		if(connection != null)
		{
			try
			{
				connection.commit();
			}
			catch (SQLException exp)
			{
				logger.fatal(exp);
				ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
				throw new DAOException(errorKey,exp,"DatabaseConnectionParams.java :"+
						DAOConstants.COMMIT_DATA_ERROR);
			}
		}
	}


}
