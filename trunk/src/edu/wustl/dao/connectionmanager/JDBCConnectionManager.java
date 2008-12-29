package edu.wustl.dao.connectionmanager;

import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.HibernateException;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.DAOConstants;


/**
 * @author kalpana_thakur
 */
public class JDBCConnectionManager extends ConnectionManager
{

	/**
	 * Connection object.
	 */
	private Connection connection = null;

	/**
	 * This method will be called to obtain clean session.
	 * @return session object.
	 *@throws DAOException :Generic DAOException.
	 */
	public Connection getConnection() throws DAOException
	{

		try
		{
			return sessionFactory.openSession().connection();
		}
		catch (HibernateException exp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"JDBCConnectionManager.java :"+
					DAOConstants.NEW_SESSION_ERROR);
		}

	}

	/**
	 *This method will be called to close current connection.
	 *@throws DAOException :Generic DAOException.
	 */
	public void closeConnection() throws DAOException
	{
		try
		{
			if (connection != null)
			{
				connection.close();
				connection = null;
			}
		}
		catch (SQLException sqlExp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,sqlExp,"JDBCConnectionManager.java :"
					+DAOConstants.CLOSE_SESSION_ERROR);
		}

	}

	 /**
	 * Commit the database level changes.
	 * @throws DAOException : It will throw DAOException.
	 */
	public void commit() throws DAOException
	{
		try
		{
			connection.commit();
		}
		catch (SQLException dbex)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,dbex,"JDBCConnectionManager.java :"
					+DAOConstants.COMMIT_DATA_ERROR);
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
			connection.rollback();
		}
		catch (SQLException dbex)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,dbex,"JDBCConnectionManager.java :"
					+DAOConstants.COMMIT_DATA_ERROR);
		}
	}

}
