/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/catissue-dao/LICENSE.txt for details.
 */

/**
 * <p>Title: DAOFactory Class>
 * <p>Description:	DAOFactory is a factory class pluggable for different applications used
 * to instantiate different DAO type objects.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @version 1.00
 */

package edu.wustl.dao.daofactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.io.DOMWriter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.util.XMLHelper;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.AbstractJDBCDAOImpl;
import edu.wustl.dao.DAO;
import edu.wustl.dao.DatabaseProperties;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.connectionmanager.IConnectionManager;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.DAOConstants;
import edu.wustl.dao.util.HibernateMetaData;


/**
 * @author kalpana_thakur
 */

public class DAOFactory implements IDAOFactory
{
	/**
	 * This member will store the default Connection Manager name.
	 */
	private String defaultConnMangrName;

	/**
	 * This member will store the JDBC Connection Manager name.
	 */
	private String jdbcConnMangrName;

	/**
	 * This member will store data source for JDBC connection.
	 */
	private String dataSource;

	/**
	 * This member will store the Default DAO class name.
	 * Mostly HibernateDAOImpl will be considered as default DAO
	 */
	private String defaultDAOClassName;
	/**
	 * This member will store the JDBC DAO class name.
	 */
	private String jdbcDAOClassName;
	/**
	 * This member will store the name of the application.
	 */
	private String applicationName;
	/**
	 * This member will store the configuration file name.
	 */
	private String configurationFile;
	/**
	 * This member will store the EntityResolver.
	 */
	private static final EntityResolver entityResolver =
		XMLHelper.DEFAULT_DTD_RESOLVER;

	/**
	 * This member will store the default connectionManager instance.
	 */
	private IConnectionManager defaultConnectionManager;

	/**
	 * This member will store the JDBC connectionManager instance.
	 */
	private IConnectionManager jdbcConnectionManager;


	/**
	 * This will store the default setting for DAO factory(true / false).
	 */
	private Boolean isDefaultDAOFactory;

	/**
	 * Database properties.
	 */
	private DatabaseProperties databaseProperties;

	/**
	 * Class logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(DAOFactory.class);


	/**
	 * This method will be called to retrieved default DAO instance.
	 * It will read the concrete class for DAO and instantiate it
	 * and also sets the Connection manager object to it.
	 * @return return the DAO instance.
	 * @throws DAOException :Generic DAOException.
	 */
	public DAO getDAO()throws DAOException
	{
		DAO dao = null;

		try
		{
		   dao = (DAO)Class.forName(defaultDAOClassName).newInstance();
		   dao.setConnectionManager(getDefaultConnectionManager());
		   HibernateMetaData.initHibernateMetaData(getDefaultConnectionManager().getConfiguration());

		}
		catch (Exception excp )
		{
			logger.error(excp.getMessage() + DAOConstants.DEFAULTDAO_INIT_ERR + excp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,excp,"DAOFactory.java :"+
					DAOConstants.DEFAULTDAO_INIT_ERR);
		}

		return dao;
	}

	/**
	 * This method will be called to retrieved the JDBC DAO instance.
	 * It will read the concrete class for DAO and instantiate it
	 * and also sets the Connection manager object to it.
	 * @return the JDBCDAO instance
	 * @throws DAOException :Generic DAOException.
	 */
	public JDBCDAO getJDBCDAO()throws DAOException
	{
		JDBCDAO jdbcDAO = null;

		try
		{
			   jdbcDAO = (JDBCDAO) Class.forName(jdbcDAOClassName).newInstance();
			   jdbcDAO.setConnectionManager(getJdbcConnectionManager());
			   ((AbstractJDBCDAOImpl)jdbcDAO).setDatabaseProperties(databaseProperties);
			   jdbcDAO.setBatchSize(databaseProperties.getDefaultBatchSize());
			   HibernateMetaData.initHibernateMetaData(getJdbcConnectionManager().
					   getConfiguration());
		}
		catch (Exception excp )
		{
			logger.error(excp.getMessage() + DAOConstants.JDBCDAO_INIT_ERR + excp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,excp,"DAOFactory.java :"+
					DAOConstants.JDBCDAO_INIT_ERR);
		}
		return jdbcDAO;
	}


	/**
	 *This method will be called to build the session factory.
	 *It reads the configuration file ,build the sessionFactory and configuration object
	 *and set the connection manager.
	 *@throws DAOException :Generic DAOException.
	 */
	public void buildSessionFactory() throws DAOException
	{
		try
		{
			Configuration configuration = setConfiguration(configurationFile);
			SessionFactory sessionFactory = configuration.buildSessionFactory();
			setConnectionManager(sessionFactory,configuration);
		}
		catch (Exception exp)
		{
			logger.fatal(exp.getMessage());
			logger.error(exp.getMessage(),exp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"DAOFactory.java :"+
					DAOConstants.BUILD_SESS_FACTORY_ERR);

		}
	}

	/**
	 * This method instantiate the Connection Manager.
	 * It will read the concrete class for Connection Manager,
	 * instantiate it and sets the application name ,session factory and configuration object to it.
	 * @param sessionFactory session factory object
	 * @param configuration configuration
	 *@throws DAOException :Generic DAOException.
	 */
	private void setConnectionManager(SessionFactory sessionFactory,Configuration configuration)
	throws DAOException
	{
		try
		{
			setDefaultConnManager(sessionFactory, configuration);
			setJDBCConnManager(sessionFactory, configuration);
		}
		catch (Exception exp)
		{
			logger.error(exp.getMessage(),exp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"DAOFactory.java :"+
					DAOConstants.CONN_MANAGER_INIT_ERR);

		}
	}
	/**
	 * @param sessionFactory :session factory object
	 * @param configuration :configuration
	 * @throws Exception : exception
	 */
	private void setDefaultConnManager(SessionFactory sessionFactory,
			Configuration configuration) throws Exception
	{
		IConnectionManager connectionManager =
			(IConnectionManager)Class.forName(defaultConnMangrName).newInstance();
		connectionManager.setApplicationName(applicationName);
		connectionManager.setSessionFactory(sessionFactory);
		connectionManager.setConfiguration(configuration);
		setDefaultConnectionManager(connectionManager);
	}

	/**
	 * @param sessionFactory  session factory object
	 * @param configuration configuration
	 * @throws Exception exception
	 */
	private void setJDBCConnManager(SessionFactory sessionFactory,
			Configuration configuration) throws Exception
	{
		IConnectionManager connectionManager =
			(IConnectionManager)Class.forName(jdbcConnMangrName).newInstance();
		connectionManager.setApplicationName(applicationName);
		connectionManager.setDataSource(dataSource);
		connectionManager.setSessionFactory(sessionFactory);
		connectionManager.setConfiguration(configuration);
		setJdbcConnectionManager(connectionManager);

	}

	 /**
     * This method adds configuration file to Hibernate Configuration.
     * It will parse the configuration file and creates the configuration.
     * @param configurationfile name of the file that needs to be added
     * @return Configuration :Configuration object.
	 * @throws DAOException :Generic DAOException.
     */
    private Configuration setConfiguration(String configurationfile) throws DAOException
    {
        try
        {

        	Configuration configuration = new Configuration();
            //InputStream inputStream = DAOFactory.class.getClassLoader().getResourceAsStream(configurationfile);
        	InputStream inputStream = Thread.currentThread().getContextClassLoader().
        	getResourceAsStream(configurationfile);
            List<Object> errors = new ArrayList<Object>();
            // hibernate api to read configuration file and convert it to
            // Document(dom4j) object.
            XMLHelper xmlHelper = new XMLHelper();
            Document document = xmlHelper.createSAXReader(configurationfile, errors, entityResolver).read(
                    new InputSource(inputStream));
            // convert to w3c Document object.
            DOMWriter writer = new DOMWriter();
            org.w3c.dom.Document doc = writer.write(document);
            // configure
            configuration.configure(doc);
            return configuration;
        }
        catch (Exception exp)
        {
        	logger.fatal(exp.getMessage());
        	ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"DAOFactory.java :"+
					DAOConstants.CONFIG_FILE_PARSE_ERROR);
        }

    }


	/**
	 * This method will be called to set Connection Manager name.
	 * @param connectionManagerName : Connection Manager.
	 */
	public void setDefaultConnMangrName(String connectionManagerName)
	{
		this.defaultConnMangrName = connectionManagerName;
	}

	/**
	 * This method will be called to set defaultDAOClassName.
	 * @param defaultDAOClassName : defaultDAOClassName.
	 */
	public void setDefaultDAOClassName(String defaultDAOClassName)
	{
		this.defaultDAOClassName = defaultDAOClassName;
	}


	/**
	 * This method will be called to set applicationName.
	 * @param applicationName : Name of the application.
	 */
	public void setApplicationName(String applicationName)
	{
		this.applicationName = applicationName;
	}

	/**
	 * This method will be called to retrieved the application Name.
	 * @return application name.
	 */
	public String getApplicationName()
	{
		return applicationName;
	}

	/**
	 * This method will be called to set jdbcDAOClassName.
	 * @param jdbcDAOClassName : jdbcDAOClassName.
	 */
	public void setJdbcDAOClassName(String jdbcDAOClassName)
	{
		this.jdbcDAOClassName = jdbcDAOClassName;
	}


	/**
	 * This method will be called to retrieved the connectionManagerName.
	 * @return connectionManagerName.
	 */
	public String getDefaultConnMangrName()
	{
		return defaultConnMangrName;
	}

	/**
	 * This method will be called to retrieved the defaultDAOClassName.
	 * @return defaultDAOClassName.
	 */
	public String getDefaultDAOClassName()
	{
		return defaultDAOClassName;
	}

	/**
	 * This method will be called to retrieved the jdbcDAOClassName.
	 * @return jdbcDAOClassName.
	 */
	public String getJdbcDAOClassName()
	{
		return jdbcDAOClassName;
	}

	/**
	 * This method will be called to retrieved the configurationFile name.
	 * @return configurationFile.
	 */
	public String getConfigurationFile()
	{
		return configurationFile;
	}

	/**
	 *This method will be called to set the configuration file name.
	 *@param configurationFile : Name of configuration file.
	 */
	 public void setConfigurationFile(String configurationFile)
	{
		this.configurationFile = configurationFile;
	}

	 /**
	 * This will called to retrieve connectionManager object.
	 * @return connectionManager
	 */
	private IConnectionManager getDefaultConnectionManager()
	{
		return defaultConnectionManager;
	}

	/**
	 * This will called to set connectionManager object.
	 * @param connectionManager :connectionManager
	 */
	private void setDefaultConnectionManager(IConnectionManager connectionManager)
	{
		this.defaultConnectionManager = connectionManager;
	}

	/**
	 * @return This will return true if DAO factory is default.
	 */
	public Boolean getIsDefaultDAOFactory()
	{

		return isDefaultDAOFactory;
	}

	/**
	 * This will be set to true if DAO factory is default.
	 * @param isDefaultDAOFactory :
	 */
	public void setIsDefaultDAOFactory(Boolean isDefaultDAOFactory)
	{
		this.isDefaultDAOFactory = isDefaultDAOFactory;
	}

	/**
	 * This method will be called to get the JDBC connection manager name.
	 * @return jdbcConnMangrName
	 */
	public String getJdbcConnMangrName()
	{
		return jdbcConnMangrName;
	}

	/**
	 * This method will be called to set connection manager name.
	 * @param jdbcConnMangrName : JDBC connection manager name.
	 */
	public void setJdbcConnMangrName(String jdbcConnMangrName)
	{
		this.jdbcConnMangrName = jdbcConnMangrName;
	}

	/**
	 * This method will be called to get the data source.
	 * @return dataSource
	 */
	public String getDataSource()
	{
		return dataSource;
	}

	/**
	 * This method will be called to set the data source.
	 * @param dataSource : JDBC connection name.
	 */
	public void setDataSource(String dataSource)
	{
		this.dataSource = dataSource;
	}

	/**
	 * @return :
	 */
	public IConnectionManager getJdbcConnectionManager()
	{
		return jdbcConnectionManager;
	}

	/**
	 * @param jdbcConnectionManager :
	 */
	public void setJdbcConnectionManager(IConnectionManager jdbcConnectionManager)
	{
		this.jdbcConnectionManager = jdbcConnectionManager;
	}

	/**
	  * This method will be called to set all database properties.
	  * @param databaseProperties :database properties.
	  */
	public void setDatabaseProperties(DatabaseProperties databaseProperties)
	{
		this.databaseProperties = databaseProperties;
	}

	/**
	 * This method will be called to get the database name.
	 * @return database name.
	 */
	public String getDataBaseType()
	{
		return databaseProperties.getDataBaseType();
	}

}
