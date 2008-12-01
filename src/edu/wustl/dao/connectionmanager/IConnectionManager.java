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
	 * @return :
	 * @throws DAOException :
	 */
	Session getCleanSession() throws DAOException;

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

}
