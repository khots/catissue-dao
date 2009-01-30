/**
 *<p>Title: IConnectionManager Interface>
 * <p>Description:	Pluggable IConnectionManager allows developer to select
 * ConnectionManager at run time.It handles all
 * hibernate specific operations like opening and closing of hibernate connection, session etc</p>
 * @author kalpana_thakur
 * @version 1.0
 */
package edu.wustl.dao.connectionmanager;

import java.sql.Connection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import edu.wustl.dao.exception.DAOException;


/**
 * @author kalpana_thakur
 *TODO
 */
public interface IConnectionManager
{


	/**
	 * @return :
	 * @throws DAOException :
	 */
	Connection getConnection() throws DAOException;

	/**
	 * @throws DAOException :
	 */
	void closeConnection() throws DAOException;

	/**
	 * @return :
	 * @throws DAOException :
	 */
	Session newSession() throws DAOException;

	/**
	 * @throws DAOException :
	 *TODO
	 */
	void closeSession() throws DAOException;

	/**
	 * @return :
	 * @throws DAOException :
	 */
	Session currentSession() throws DAOException;

	/**
	 * @param applicationName : Name of the Application
	 * TODO
	 */
	void setApplicationName(String applicationName);

	/**
	 * @return This will return the application name.
	 */
	String getApplicationName();

	/**
	 * @param sessionFactory :
	 */
	void setSessionFactory(SessionFactory sessionFactory);

	/**
	 * @return SessionFactory
	 */
	SessionFactory getSessionFactory();

	/**
	 * @param cfg : Configuration
	 */
	void setConfiguration(Configuration cfg);

	/**
	 * @return Configuration.
	 */
	Configuration getConfiguration();

	/**
	 * This method will be called to get the data source.
	 * @return dataSource
	 */
	 String getDataSource();

	 /**
	 * This method will be called to set the data source.
	 * @param dataSource : JDBC connection name.
	 */
	 void setDataSource(String dataSource);

	 /**
	 * Commit the database level changes.
	 * @throws DAOException : It will throw DAOException.
	 */
	 void commit() throws DAOException;

	 /**
	 * RollBack all the changes after last commit.
	 * Declared in DAO class.
	 * @throws DAOException : It will throw DAOException.
	 */
	 void rollback() throws DAOException;

	 /**
	 *This method will be called to retrieved the current connection object.
	 *@return Connection object
	 *@throws DAOException :Generic DAOException.
	 */
	 Connection getCleanConnection() throws DAOException;

	 /**
	 *This method will be called to close current connection.
	 *@throws DAOException :Generic DAOException.
	 */
	void closeCleanConnection() throws DAOException;

	/**
	 *This method will be called to close current connection.
	 *@throws DAOException :Generic DAOException.
	 */
	void closeCleanSession() throws DAOException;

	/**
	 * @return :
	 * @throws DAOException :
	 */
	Session getCleanSession() throws DAOException;

}
