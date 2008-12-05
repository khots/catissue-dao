/**
 * <p>Title: DAOFactory Class>
 * <p>Description:	DAOFactory is a factory for JDBC DAO instances of various domain objects.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
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
import edu.wustl.dao.DAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.connectionmanager.IConnectionManager;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.DAOConstants;


/**
 * @author kalpana_thakur
 */

public class DAOFactory implements IDAOFactory
{
	/**
	 * This member will store the Connection Manager name.
	 */
	private String connectionManagerName;

	/**
	 * This member will store the Default DAO class name.
	 * TODO
	 */
	private String defaultDAOClassName;
	/**
	 * This member will store the JDBC DAO class name.
	 * TODO
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
	 * This member will store the connectionManager instance.
	 */
	private IConnectionManager connectionManager;


	/**
	 * This will store the default setting for DAO factory(true / false).
	 */
	private Boolean isDefaultDAOFactory;


	/**
	 * Class logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(DAOFactory.class);


	/**
	 * This method will be called to retrieved default DAO instance.
	 * @return return the DAO instance.
	 * @throws DAOException :Generic DAOException.
	 */
	public DAO getDAO()throws DAOException
	{
		DAO dao = null;

		try
		{
		   dao = (DAO)Class.forName(defaultDAOClassName).newInstance();
		   dao.setConnectionManager(getConnectionManager());

		}
		catch (Exception excp )
		{
			logger.error(excp.getMessage() + DAOConstants.DEFAULT_DAO_INSTANTIATION_ERROR + excp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,excp,"DAOFactory.java :"+
					DAOConstants.DEFAULT_DAO_INSTANTIATION_ERROR);
		}

		return dao;
	}

	/**
	 * This method will be called to retrieved the JDBC DAO instance.
	 * @return the JDBCDAO instance
	 * @throws DAOException :Generic DAOException.
	 */
	public JDBCDAO getJDBCDAO()throws DAOException
	{
		JDBCDAO dao = null;

		try
		{
			   dao = (JDBCDAO) Class.forName(jdbcDAOClassName).newInstance();
			   dao.setConnectionManager(getConnectionManager());
		}
		catch (Exception excp )
		{
			logger.error(excp.getMessage() + DAOConstants.JDBCDAO_INSTANTIATION_ERROR + excp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,excp,"DAOFactory.java :"+
					DAOConstants.JDBCDAO_INSTANTIATION_ERROR);
		}
		return dao;
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
					DAOConstants.BUILD_SESSION_FACTORY_ERROR);

		}
	}

	/**
	 * This method instantiate the Connection Manager.
	 * @param sessionFactory session factory object
	 * @param configuration configuration
	 *@throws DAOException :Generic DAOException.
	 */
	private void setConnectionManager(SessionFactory sessionFactory,Configuration configuration)
	throws DAOException
	{
		/*
		 * Is writing this is valid here ...confirm !!!
		 */
		try
		{
			IConnectionManager connectionManager =
				(IConnectionManager)Class.forName(connectionManagerName).newInstance();
			connectionManager.setApplicationName(applicationName);
			connectionManager.setSessionFactory(sessionFactory);
			connectionManager.setConfiguration(configuration);
			setConnectionManager(connectionManager);
		}
		catch (Exception exp)
		{
			logger.error(exp.getMessage(),exp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"DAOFactory.java :"+
					DAOConstants.CONN_MANAGER_INSTANTIATION_ERROR);

		}
	}


	 /**
     * This method adds configuration file to Hibernate Configuration.
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
	public void setConnectionManagerName(String connectionManagerName)
	{
		this.connectionManagerName = connectionManagerName;
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
	 * This method will be called to set jdbcDAOClassName.
	 * @param jdbcDAOClassName : jdbcDAOClassName.
	 */
	public void setJdbcDAOClassName(String jdbcDAOClassName)
	{
		this.jdbcDAOClassName = jdbcDAOClassName;
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
	 * This method will be called to retrieved the connectionManagerName.
	 * @return connectionManagerName.
	 */
	public String getConnectionManagerName()
	{
		return connectionManagerName;
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
	private IConnectionManager getConnectionManager()
	{
		return connectionManager;
	}

	/**
	 * This will called to set connectionManager object.
	 * @param connectionManager :connectionManager
	 */
	private void setConnectionManager(IConnectionManager connectionManager)
	{
		this.connectionManager = connectionManager;
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

}
