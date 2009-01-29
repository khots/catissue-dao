/**
 * <p>Title: HibernateDAO Class>
 * <p>Description:	HibernateDAO is default implemention of DAO through Hibernate ORM tool.
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on Mar 16, 2005
 */

package edu.wustl.dao;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.AuditException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.DAOConstants;
import edu.wustl.dao.util.DAOUtility;


/**
 * Default implementation of DAO through Hibernate ORM tool.
 * @author kapil_kaveeshwar
 */
public class HibernateDAOImpl extends AbstractDAOImpl
{

    /**
     * logger Logger - Generic logger.
     */
      private static org.apache.log4j.Logger logger =
           Logger.getLogger(HibernateDAOImpl.class);

     /**
  	 * specify clean Session instance.
  	 */
     private Session cleanSession = null;

     /**
   	 * specify clean connection instance.
   	 */
     private Connection cleanConnection = null;

	/**
	 * specify Session instance.
	 */
     private Session session = null;

	/**
	 * This method will be used to establish the session with the database.
	 * Declared in  class.
	 * @param sessionDataBean session Data.
	 * @throws DAOException generic DAOException.
	 */
	public void openSession(SessionDataBean sessionDataBean) throws DAOException
	{
		super.openSession(sessionDataBean);
		session = connectionManager.currentSession();
	}

	/**
	 * This method will be used to close the session with the database.
	 * Declared in  class.
	 * @throws DAOException generic DAOException.
	 */
	public void closeSession() throws DAOException
	{
		super.closeSession();
		connectionManager.closeSession();
	}

	/**
	 * Commit the database level changes.
	 * Declared in  class.
	 * @throws DAOException generic DAOException.
	 */
	public void commit() throws DAOException
	{
		super.commit();
		connectionManager.commit();
	}

	/**
	 * RollBack all the changes after last commit.
	 * Declared in  class.
	 * @throws DAOException generic DAOException.
	 */
	public void rollback() throws DAOException
	{
		logger.debug("Session rollback");
		connectionManager.rollback();
	}

	/**
	 * Saves the persistent object in the database.
	 * @param obj The object to be saved.
	 * @param isAuditable is Auditable.
	 * @throws DAOException generic DAOException.
	 */
	public void insert(Object obj,boolean isAuditable) throws DAOException
	{
		logger.debug("Insert Object");
		try
		{
			session.save(obj);
			auditManager.compare(obj, null, "INSERT",isAuditable);
		}
		catch (HibernateException hibExp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,hibExp,"HibernateDAOImpl.java :"+
					DAOConstants.INSERT_OBJ_ERROR);
		}
		catch (AuditException exp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.audit.error");
			throw new DAOException(errorKey,exp,"HibernateDAOImpl.java :");
		}


	}

	/**
	 * Updates the persistent object in the database.
	 * @param obj The object to be updated.
	 * @throws DAOException generic DAOException.
	 */
	public void update(Object obj) throws DAOException
	{
		logger.debug("Update Object");
		try
		{
			session.update(obj);
		}
		catch (HibernateException hibExp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,hibExp,"HibernateDAOImpl.java :"+
					DAOConstants.UPDATE_OBJ_ERROR);
		}
	}

	/**
	 * Deletes the persistent object from the database.
	 * @param obj The object to be deleted.
	 * @throws DAOException generic DAOException.
	 */
	public void delete(Object obj) throws DAOException
	{
		logger.debug("Delete Object");
		try
		{
			session.delete(obj);
		}
		catch (HibernateException hibExp)
		{
			logger.error(hibExp.getMessage() , hibExp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,hibExp,"HibernateDAOImpl.java :"+
					DAOConstants.DELETE_OBJ_ERROR);
		}
	}

	/**
	 * Retrieves the records for class name in sourceObjectName according
	 * to field values passed in the passed session.
	 * @param sourceObjectName source Object Name.
	 * @param selectColumnName select Column Name.
	 * @param queryWhereClause where column conditions
	 * @param onlyDistinctRows true if only distinct rows should be selected.
	 * @return List.
	 * @throws DAOException generic DAOException.
	 */
	public List<Object> retrieve(String sourceObjectName,String[] selectColumnName,
			QueryWhereClause queryWhereClause,boolean onlyDistinctRows) throws DAOException
	{
		logger.debug("Inside retrieve method");
		List<Object> list;
		try
		{
			StringBuffer queryStrBuff = new StringBuffer();
			String className = DAOUtility.getInstance().parseClassName(sourceObjectName);
			Query query;

			generateSelectPartOfQuery(selectColumnName, queryStrBuff, className);
			generateFromPartOfQuery(sourceObjectName, queryStrBuff, className);

			if(queryWhereClause != null)
			{
				queryStrBuff.append(queryWhereClause.toWhereClause());
			}
			query = session.createQuery(queryStrBuff.toString());

			list = query.list();

		}
		catch (Exception exp)
		{
			logger.error(exp.getMessage(), exp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey, exp,"HibernateDAOImpl.java :"
					+DAOConstants.RETRIEVE_ERROR);
		}
		return list;
	}


	/**
	 * @param sourceObjectName source Object Name.
	 * @param sqlBuff query buffer
	 * @param className gives the class name
	 */
	private void generateFromPartOfQuery(String sourceObjectName,
			StringBuffer sqlBuff, String className)
	{
		logger.debug("Prepare from part of query");
		sqlBuff.append("from " + sourceObjectName
		        + " " + className);
	}

	/**
	 * Retrieve Object.
	 * @param sourceObjectName source Object Name.
	 * @param identifier identifier.
	 * @return object.
	 * @throws DAOException generic DAOException.
	 */
	public Object retrieveById(String sourceObjectName, Long identifier)
	 throws DAOException
	 {
		logger.debug("Inside retrieve method");
		try
		{
			Object object = session.load(Class.forName(sourceObjectName), identifier);
			//HibernateProxy hibernatProxy = (HibernateProxy) object;
			//return (Object)hibernatProxy.getHibernateLazyInitializer().getImplementation();
			//return HibernateMetaData.getProxyObjectImpl(object);
			//session.evict(object);
			return object;
		}
		catch (Exception exp)
		{
			logger.error(exp.getMessage(), exp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey, exp,"HibernateDAOImpl.java :"+
					DAOConstants.RETRIEVE_ERROR);
		}

	}

	/**
	 * Executes the HQL query.
	 * @param query HQL query to execute.
	 * @return List.
	 */
	public List executeQuery(String query)
	{
		logger.debug("Execute query");
		Query hibernateQuery = session.createQuery(query);
		List <Object> returner = hibernateQuery.list();
		return returner;
	}

	/**
	 * Retrieve Attribute.
	 * @param objClass object.
	 * @param identifier identifier.
	 * @param attributeName attribute Name.
	 * @param columnName Name of the column.
	 * @return Object.
	 * @throws DAOException generic DAOException.
	 */
	public Object retrieveAttribute(Class objClass, Long identifier,
			String attributeName,String columnName) throws DAOException
	 {
		logger.debug("Retrieve attributes");
		try
		{
			String objClassName = objClass.getName();
			String simpleName = objClass.getSimpleName();
			String nameOfAttribute = DAOUtility.getInstance().
			createAttributeNameForHQL(simpleName, attributeName);
			StringBuffer queryStringBuffer = new StringBuffer(DAOConstants.TAILING_SPACES);

			queryStringBuffer.append("Select").append(DAOConstants.TAILING_SPACES).
			append(nameOfAttribute).append(DAOConstants.TAILING_SPACES).
			append("from").append(DAOConstants.TAILING_SPACES).
			append(objClassName).append(DAOConstants.TAILING_SPACES).
			append(simpleName).append(DAOConstants.TAILING_SPACES).
			append("where").append(DAOConstants.TAILING_SPACES).
			append(simpleName).append(DAOConstants.DOT_OPERATOR).append(columnName).
			append(DAOConstants.EQUAL).append(DAOConstants.TAILING_SPACES).
			append(identifier);

			return session.createQuery(queryStringBuffer.toString()).list();
		}
		catch (HibernateException exception)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey, exception,"HibernateDAOImpl.java :"+
					DAOConstants.RETRIEVE_ERROR);
		}
	}

	/**
	 * Generate Select Block.
	 * @param selectColumnName select Column Name.
	 * @param sqlBuff sqlBuff
	 * @param className class Name.
	 */
	private void generateSelectPartOfQuery(String[] selectColumnName, StringBuffer sqlBuff, String className)
	 {
		logger.debug("Prepare select part of query.");
		if (selectColumnName != null && selectColumnName.length > 0)
		 {
		    sqlBuff.append("Select ");
		    for (int i = 0; i < selectColumnName.length; i++)
		     {
		        sqlBuff.append(DAOUtility.getInstance().
		        		createAttributeNameForHQL(className, selectColumnName[i]));
		        if (i != selectColumnName.length - 1)
		        {
		            sqlBuff.append(", ");
		        }
		    }
		    sqlBuff.append("   ");
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
		cleanConnection =  connectionManager.getCleanSession().connection();
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
	 * This method will be called to obtain clean session.
	 * @return session object.
	 *@throws DAOException :Generic DAOException.
	 */
	public Session getCleanSession() throws DAOException
	{
		logger.debug("Get clean session");
		cleanSession = connectionManager.getCleanSession();
		return cleanSession;
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
	 * @param excp : Exception Object.
	 * @return : It will return the formated messages.
	 * @throws DAOException :
	 */
	public String formatMessage(Exception excp) throws DAOException
	{
		logger.debug("Format error message");
		String formatMessage = DAOConstants.TAILING_SPACES;
		String appName = CommonServiceLocator.getInstance().getAppHome();
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		JDBCDAO jdbcDAO = daoFactory.getJDBCDAO();
		//HibernateMetaData.initHibernateMetaData(jdbcDAO.getConnectionManager().getConfiguration());
		formatMessage = jdbcDAO.formatMessage(excp,getCleanConnection());
		getConnectionManager().closeConnection();
		return formatMessage;
	}

	/**
	 * This method executes the named query and returns the results.
	 * @param queryName : handle for named query.
	 * @return result as list of Object
	 */
	public List executeNamedQuery(String queryName)
	{
		Query query=session.getNamedQuery(queryName);
		return query.list();
	}

	/**
	 * This method returns named query.
	 * @param queryName : handle for named query.
	 * @return Query named query
	 */
	public Query getNamedQuery(String queryName)
	{
		return session.getNamedQuery(queryName);
	}

}