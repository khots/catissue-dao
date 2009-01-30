/**
*<p>Title: ConnectionManager class>
 * <p>Description:	It handles all
 * hibernate specific operations like opening and closing of hibernate connection, session etc
 * Connection manager has ThreadLocal instance variable which holds the Map having session object
 * as per the application .It holds Map<ApplicationName, session>
 * thus allow user to use multiple hibernate sessions as per the application.</p>
 * @author kalpana_thakur
 * @version 1.0
 */
package edu.wustl.dao.connectionmanager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.DAOConstants;

/**
 * @author kalpana_thakur
 *
 */
public class ConnectionManager implements IConnectionManager
{
	
	/**
     * logger Logger - Generic logger.
     */
      private static org.apache.log4j.Logger logger =
           Logger.getLogger(ConnectionManager.class);

	
	/**
	 * This member will store the name of the application.
	 */
	protected String applicationName;


	/**
	 * This member will store the configuration instance.
	 */
	protected Configuration configuration;


	/**
	 * This member will store the sessionFactory instance.
	 */
	protected SessionFactory sessionFactory;


	/**
	 * This member will store data source for JDBC connection.
	 */
	protected String dataSource;

	/**
	 * specify Session instance.
	 */
      private Session session = null;

      /**
  	 * specify Transaction instance.
  	 */
      private Transaction transaction = null;

      /**
    	 * specify clean Session instance.
    	 */
       private Session cleanSession = null;

       /**
     	 * specify clean connection instance.
     	 */
       private Connection cleanConnection = null;

	/**
	 * ThreadLocal to hold the Session for the current executing thread.
	 * It holds Map(ApplicationName, session)
  		thus allow user to use multiple Hibernate sessions as per the application.
	 */
	private static final ThreadLocal<Map<String, Session>> SESSION_THREAD_LOCAL
	= new ThreadLocal<Map<String, Session>>();


	/**
	 * This block will instantiate applicationSessionMap.
	 * This map holds the session object associated to the application.
	 * Map will stored in threadLocal,whenever new session will be created ,
	 * threadLocal will be checked first to obtain the session associated to application.
	 */

	/*static
	{
		Map<String, Session> applicationSessionMap = new HashMap<String, Session>();
		SESSION_THREAD_LOCAL.set(applicationSessionMap);
	}
*/

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
		Map<String, Session> applicationSessionMap = SESSION_THREAD_LOCAL.get();
		if(applicationSessionMap.containsKey(applicationName))
		{
			Session session = applicationSessionMap.get(applicationName);
			if(session != null)
			{
				session.close();
				applicationSessionMap.remove(applicationName);
				session=null;
				transaction = null;
			}
		}
	}
	/**
	 * It will instantiate applicationSessionMap.
	 * This map holds the session object associated to the application.
	 * Map will stored in threadLocal,whenever new session will be created ,
	 * threadLocal will be checked first to obtain the session associated to application.
	 * This method will be called to retrieve the current session.
	 * It will check the session for the running application in applicationSessionMap.
	 * If present, retrieved the session from the Map otherwise create the
	 * new session and store it into the Map.
	 * @return session object.
	 *@throws DAOException :Generic DAOException.
	 */
	public Session currentSession() throws DAOException
	{

		Map<String, Session> appSessionMap = SESSION_THREAD_LOCAL.get();
		if(appSessionMap == null)
		{
			appSessionMap = new HashMap<String, Session>();
			SESSION_THREAD_LOCAL.set(appSessionMap);
		}

	    // Open a new Session, if this Thread has none yet
		if (!(appSessionMap.containsKey(applicationName)) )
		{
        	Session session = newSession();
        	transaction = session.beginTransaction();
        	appSessionMap.put(applicationName, session);
        }
		return appSessionMap.get(applicationName);

	}


	/**
	 * This method will be called to create new session.
	 * @return session object.
	 *@throws DAOException :Generic DAOException.
	 */
	public Session newSession() throws DAOException
	{
		try
        {
			Session session = sessionFactory.openSession();
			session.setFlushMode(FlushMode.COMMIT);
            session.connection().setAutoCommit(false);
            return session;
        }
        catch (Exception excp)
        {
        	ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,excp,"ConnectionManager.java :"+
					DAOConstants.NEW_SESSION_ERROR);
        }
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
	 * Commit the database level changes.
	 * @throws DAOException : It will throw DAOException.
	 */
	public void commit() throws DAOException
	{
		if (transaction != null)
		{
			transaction.commit();
		}
	}


	 /**
	 * RollBack all the changes after last commit.
	 * Declared in DAO class.
	 * @throws DAOException : It will throw DAOException.
	 */
	public void rollback() throws DAOException
	{
		if (transaction != null)
		{
			transaction.rollback();
		}
	}

	/**
	 *This method will be called to retrieved the current connection object.
	 *@return Connection object
	 *@throws DAOException :Generic DAOException.
	 */
	public Connection getCleanConnection() throws DAOException
	{
		logger.debug("Get clean connection");
		cleanConnection = getCleanSession().connection();
		return cleanConnection;
	}

	/**
	 *This method will be called to close current connection.
	 *@throws DAOException :Generic DAOException.
	 */
	public void closeCleanConnection() throws DAOException
	{
		logger.debug("Close clean connection");
		try
		{
			cleanConnection.close();
		}
		catch (SQLException sqlExp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,sqlExp,"DAOFactory.java :"+
					DAOConstants.CLOSE_CONN_ERR);
		}
	}

	/**
	 *This method will be called to close current connection.
	 *@throws DAOException :Generic DAOException.
	 */
	public void closeCleanSession() throws DAOException
	{
		logger.debug("Close clean session");
		cleanSession.close();
	}
	/**
	 * This method will be called to obtain clean session.
	 * @return session object.
	 *@throws DAOException :Generic DAOException.
	 */
	public Session getCleanSession() throws DAOException
	{
		try
		{
			return sessionFactory.openSession();
		}
		catch (HibernateException exp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"ConnectionManager.java :"+
					DAOConstants.NEW_SESSION_ERROR);
		}

	}

}
