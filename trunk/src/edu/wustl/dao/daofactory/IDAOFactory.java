package edu.wustl.dao.daofactory;

import edu.wustl.dao.DAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;



/**
 * @author kalpana_thakur
 *TODO
 */
public interface IDAOFactory
{

	/**
	 *This method will be called to build the session factory.
	 *It reads the configuration file ,build the sessionFactory and configuration object
	 *and set the connection manager.
	 * @throws DAOException  generic exception
	 */
	void buildSessionFactory ()throws DAOException;

	/**
	 * This method will be called to set default DAO class name.
	 * @param defaultDAOClassName default DAO class name
	 */
	void setDefaultDAOClassName(String defaultDAOClassName);

	/**
	 * This method will be called to get default DAO class name.
	 * @return Default DAO class name.
	 */
	String getDefaultDAOClassName();

	/**
	 * This method will be called to set the JDBC DAO class name.
	 * @param jdbcDAOClassName : JDBC DAO class name.
	 */
	void setJdbcDAOClassName(String jdbcDAOClassName);

	/**
	 * This method will be called to get the JDBC DAO class name.
	 * @return jdbcDAOClassName
	 */
	String getJdbcDAOClassName();

	/**
	 * This method will be called to set the Connection Manager name.
	 * @param connectionManagerName : Name of connection manager.
	 */
	void setConnectionManagerName(String connectionManagerName);
	/**
	 * This method will be called to get the Connection Manager name.
	 * @return Connection Manager name.
	 */
	String getConnectionManagerName();

	/**
	 * This method will be called to set the application name.
	 * @param applicationName : Application Name.
	 */
	void setApplicationName(String applicationName);
	/**
	 * This method will be called to get the applicationName name.
	 * @return Application name.
	 */
	String getApplicationName();

	/**
	 * This method will be called to set the configuration file name.
	 * @param configurationFile : name of configuration file.
	 */
	void setConfigurationFile(String configurationFile);

	/**
	 * This method will be called to set the configuration file name.
	 * @return configurationFile
	 */
	String getConfigurationFile();

	/**
	 * This will be invoked to retrieve default DAO.
	 * @return default DAO
	 * @throws DAOException : generic exception
	 */
	DAO getDAO()throws DAOException;

	/**
	 * This will be invoked to retrieve JDBC DAO.
	 * @return JDBC DAO.
	 * @throws DAOException : generic exception
	 */
	JDBCDAO getJDBCDAO()throws DAOException;

}
