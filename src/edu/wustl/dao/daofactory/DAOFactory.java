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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.DOMWriter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
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
public class DAOFactory implements IConnectionManager,IDAOFactory
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
	 * This member will store the configuration instance.
	 */
	private Configuration configuration;
	/**
	 * This member will store the sessionFactory instance.
	 */
	private SessionFactory sessionFactory;
	/**
	 * This member will store the connectionManager instance.
	 */
	private IConnectionManager connectionManager;

	/**
	 * ThreadLocal to hold the Session for the current executing thread.
	 */
	private static final ThreadLocal<Map<String, Session>> threadLocal
	= new ThreadLocal<Map<String, Session>>();

	/**
	 * Class logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(DAOFactory.class);

	/**
	 * This block will instantiate applicationSessionMap.
	 * This map holds the session object associated to the application.
	 * Map will stored in threadLocal,whenever new session will be created ,
	 * threadLocal will be checked first to obtain the session associated to application.
	 */
	static
	{
		Map<String, Session> applicationSessionMap = new HashMap<String, Session>();
		threadLocal.set(applicationSessionMap);
	}

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
			throw new DAOException(errorKey,excp,"DAOFactory.java"+
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
			throw new DAOException(errorKey,excp,"DAOFactory.java"+
					DAOConstants.JDBCDAO_INSTANTIATION_ERROR);
		}
		return dao;
	}


	/**
	 *This method will be called to retrieved the current connection object.
	 *@return Connection object
	 *@throws DAOException :Generic DAOException.
	 */
	public Connection getConnection() throws DAOException
	{
		return currentSession().connection();
	}

	/**
	 *This method will be called to close current connection.
	 *@throws DAOException :Generic DAOException.
	 */
	public void closeConnection() throws DAOException
	{
		closeSession();
	}

	/**
	 * This method will be called to close the session.
	 * It will check the session for the running application in applicationSessionMap,
	 * if present it will remove it from the Map.
	 *@throws DAOException :Generic DAOException.
	 */
	public void closeSession() throws DAOException
	{
		Map<String, Session> applicationSessionMap = (Map<String, Session>) threadLocal.get();
		if(applicationSessionMap.containsKey(applicationName))
		{
			Session session = (Session)applicationSessionMap.get(applicationName);
			if(session != null)
			{
				session.close();
			}
			applicationSessionMap.remove(applicationName);
		}
	}

	/**
	 * This method will be called to retrieve the current session.
	 * It will check the session for the running application in applicationSessionMap.
	 * If present, retrieved the session from the Map otherwise create the
	 * new session and store it into the Map.
	 * @return session object.
	 *@throws DAOException :Generic DAOException.
	 */
	public Session currentSession() throws DAOException
	{

		Map<String, Session> applicationSessionMap = (Map<String, Session>)threadLocal.get();
	    // Open a new Session, if this Thread has none yet
		if (!(applicationSessionMap.containsKey(applicationName)) )
		{
        	Session session = newSession();
        	applicationSessionMap.put(applicationName, session);
        }
        return (Session)applicationSessionMap.get(applicationName);

	}

	/**
	 * This method will be called to create new session.
	 * @return session object.
	 *@throws DAOException :Generic DAOException.
	 */
	public Session newSession() throws DAOException
	{
		Session session = null;
		try
        {
			session = sessionFactory.openSession();
			session.setFlushMode(FlushMode.COMMIT);
            session.connection().setAutoCommit(false);
        }
        catch (Exception excp)
        {
        	ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,excp,"DAOFactory.java"+
					DAOConstants.NEW_SESSION_ERROR);
        }
        return session;

	}

	/**
	 * This method will be called to obtain clean session.
	 * @return session object.
	 *@throws DAOException :Generic DAOException.
	 */
	public Session getCleanSession() throws DAOException
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			return session;
		}
		catch (HibernateException exp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"DAOFactory.java"+
					DAOConstants.NEW_SESSION_ERROR);
		}

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
			logger.error(exp.getMessage(),exp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"DAOFactory.java"+
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
			throw new DAOException(errorKey,exp,"DAOFactory.java"+
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
        	ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"DAOFactory.java"+
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
	 * This will called to retrieve session factory object.
	 * @return sessionFactory
	 */
	public SessionFactory getSessionFactory()
	{
		return sessionFactory;
	}

	/**
	 * This will called to set session factory object.
	 * @param sessionFactory : session factory.
	 */
	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	/**
	 * This will called to retrieve configuration object.
	 * @return configuration
	 */
	public Configuration getConfiguration()
	{
		return configuration;
	}

	/**
	 * This will called to set the configuration object.
	 * @param cfg configuration
	 */
	public void setConfiguration(Configuration cfg)
	{
		this.configuration = cfg;
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

}
