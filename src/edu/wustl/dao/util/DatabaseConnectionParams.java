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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;
import java.sql.DatabaseMetaData;

/**
 * @author kalpana_thakur
 * This class will handles all database specific parameters.
 */

public class DatabaseConnectionParams
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
	private static org.apache.log4j.Logger logger = Logger.getLogger(DatabaseConnectionParams.class);

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
		}
		catch (SQLException sqlExp)
		{
			logger.fatal(sqlExp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.conn.para.creation.error");
			throw new DAOException(errorKey,sqlExp,"DatabaseConnectionParams.java :"+
					DAOConstants.STMT_CREATION_ERROR);
		}
		return statement;

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
		}
		catch (SQLException sqlExp)
		{
			logger.fatal(sqlExp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.conn.para.creation.error");
			throw new DAOException(errorKey,sqlExp,"DatabaseConnectionParams.java :"+
					DAOConstants.RESULTSET_CREATION_ERROR);
		}
		return resultSet;
	}


	/**
	 * This method will be called to return query meta data.
	 * @param query : Query String
	 * @return ResultSetMetaData
	 * @throws DAOException :Generic Exception
	 */
	public ResultSetMetaData getMetaData(String query)throws DAOException
	{
		ResultSetMetaData metaData = null;
		try
		{

			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			metaData = resultSet.getMetaData();
		}
		catch (SQLException sqlExp)
		{
			logger.fatal(sqlExp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.conn.para.creation.error");
			throw new DAOException(errorKey,sqlExp,"DatabaseConnectionParams.java :"+
					DAOConstants.RS_METADATA_ERROR);
		}

		return metaData;
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
	 * @throws DAOException :Generic Exception
	 */
	public void executeUpdate(String query) throws DAOException
	{
		PreparedStatement stmt = null;
		try
		{
			stmt = getPreparedStatement(query);
			stmt.executeUpdate();
		//	connection.commit();
		}
		catch (SQLException sqlExp)
		{
			logger.fatal(sqlExp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,sqlExp,"DatabaseConnectionParams.java :"
					+DAOConstants.EXECUTE_QUERY_ERROR+"   "+query);
		}
		finally
		{
			closeConnectionParams();
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
	 *For tempory use ..
	 *TODO will be removed when getCleanConnection get removed.
	 * @throws SQLException :
	 */
	public void commit() throws SQLException
	{
		if(connection != null)
		{
			connection.commit();
		}
	}


}
