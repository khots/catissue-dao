package edu.wustl.dao.daofactory;

import edu.wustl.common.util.dbmanager.DAOException;
import edu.wustl.dao.DAO;
import edu.wustl.dao.JDBCDAO;



public interface IDAOFactory
{	
	void buildSessionFactory ()throws DAOException;
	
	void setDefaultDAOClassName(String defaultDAOClassName);
	String getDefaultDAOClassName();
	
	void setJdbcDAOClassName(String jdbcDAOClassName);
	String getJdbcDAOClassName();
	
	void setConnectionManagerName(String connectionManagerName);
	String getConnectionManagerName();
	
	void setApplicationName(String applicationName);
	String getApplicationName();
	
	void setConfigurationFile(String configurationFile);
	String getConfigurationFile();
	
	DAO getDAO()throws DAOException;
	
	JDBCDAO getJDBCDAO()throws DAOException;

}
