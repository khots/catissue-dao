package edu.wustl.dao.connectionmanager;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.DAOConstants;

/**
 * @author kalpana_thakur
 *
 */
public class ConnectionManager implements IConnectionManager
{
	/**
	 * This member will store the name of the application.
	 */
	private String applicationName;


	/**
	 * This member will store the configuration instance.
	 */
	private Configuration configuration;


	/**
	 * This member will store the sessionFactory instance.
	 */
	private SessionFactory sessionFactory;


	/**
	 * ThreadLocal to hold the Session for the current executing thread.
	 */
	private static final ThreadLocal<Map<String, Session>> threadLocal
	= new ThreadLocal<Map<String, Session>>();


	static
	{
		Map<String, Session> applicationSessionMap = new HashMap<String, Session>();
		threadLocal.set(applicationSessionMap);
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

}
