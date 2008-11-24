package edu.wustl.dao.connectionmanager;

import java.sql.Connection;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import edu.wustl.common.util.dbmanager.DAOException;

/**
 * @author kalpana_thakur
 *
 */
public interface IConnectionManager
{


	/**
	 * @return :
	 * @throws DAOException :
	 * TODO
	 */
	Connection getConnection() throws DAOException;

	/**
	 * @throws DAOException :
	 * TODO
	 */
	void closeConnection() throws DAOException;

	/**
	 * @return :
	 * @throws DAOException :
	 * TODO
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
	 * TODO
	 */
	Session currentSession() throws DAOException;

	/**
	 * @return :
	 * @throws DAOException :
	 * TODO
	 */
	Session getCleanSession() throws DAOException;

	/**
	 * @param applicationName : Name of the Application
	 * TODO
	 */
	void setApplicationName(String applicationName);

	/**
	 * @return This will return the application name.
	 * TODO
	 */
	String getApplicationName();

	/**
	 * @param sessionFactory
	 * TODO
	 */
	void setSessionFactory(SessionFactory sessionFactory);

	/**
	 * @return SessionFactory
	 * TODO
	 */
	SessionFactory getSessionFactory();

	/**
	 * @param cfg : Configuration
	 * TODO
	 */
	void setConfiguration(Configuration cfg);

	/**
	 * @return Configuration.
	 * TODO
	 */
	Configuration getConfiguration();

}
