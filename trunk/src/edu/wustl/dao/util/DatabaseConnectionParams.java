package edu.wustl.dao.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.PreparedStatement;

import edu.wustl.common.util.dbmanager.DAOException;
import edu.wustl.common.util.logger.Logger;


/**
 * @author kalpana_thakur
 *
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
			logger.fatal(DAOConstants.DB_PARAM_INIT_ERROR , sqlExp);
			throw new DAOException(DAOConstants.DB_PARAM_INIT_ERROR, sqlExp);
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
			resultSet = statement.executeQuery(query);
		}
		catch (SQLException sqlExp)
		{
			logger.fatal(DAOConstants.DB_PARAM_INIT_ERROR, sqlExp);
			throw new DAOException(DAOConstants.DB_PARAM_INIT_ERROR, sqlExp);
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
			logger.fatal(DAOConstants.DB_PARAM_INIT_ERROR, sqlExp);
			throw new DAOException(DAOConstants.DB_PARAM_INIT_ERROR, sqlExp);
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
			if(connection != null)
			{
				connection.close();
			}
			if(resultSet != null )
			{
				resultSet.close();
			}
			if (statement != null)
			{
				statement.close();
			}
			if (preparedStatement != null)
			{
				preparedStatement.close();
			}

		}
		catch(SQLException sqlExp)
		{
			logger.fatal(DAOConstants.CONNECTIONS_CLOSING_ISSUE, sqlExp);
			throw new DAOException(DAOConstants.DB_PARAM_INIT_ERROR, sqlExp);
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
			logger.fatal(DAOConstants.DB_PARAM_INIT_ERROR, sqlExp);
			throw new DAOException(DAOConstants.DB_PARAM_INIT_ERROR, sqlExp);
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
		}
		catch (SQLException sqlExp)
		{
			logger.error(DAOConstants.EXECUTE_UPDATE_ERROR, sqlExp);
			throw new DAOException(DAOConstants.EXECUTE_UPDATE_ERROR, sqlExp);
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

}
