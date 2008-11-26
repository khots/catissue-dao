/**
 * <p>Title: HibernateDAO Class>
 * <p>Description:	HibernateDAO is default implemention of AbstractDAO through Hibernate ORM tool.
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on Mar 16, 2005
 */

package edu.wustl.dao;


import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.proxy.HibernateProxy;

import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.audit.Auditable;
import edu.wustl.common.beans.QueryResultObjectDataBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AuditException;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.dbmanager.DAOException;
import edu.wustl.common.util.dbmanager.HibernateMetaData;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.connectionmanager.IConnectionManager;
import edu.wustl.dao.util.DAOUtility;
import edu.wustl.dao.util.DatabaseConnectionParams;


/**
 * Default implementation of AbstractDAO through Hibernate ORM tool.
 * @author kapil_kaveeshwar
 */
public class HibernateDAOImpl implements HibernateDAO
{

    /**
     * logger Logger - Generic logger.
     */
      private static org.apache.log4j.Logger logger =
           Logger.getLogger(HibernateDAOImpl.class);

   /**
	 * specify Session instance.
	 */
	protected Session session = null;
		/**
	 * specify Transaction instance.
	 */
	protected Transaction transaction = null;
	/**
	 * specify AuditManager instance.
	 */
	protected AuditManager auditManager;
	/**
	 * specify isUpdated.
	 */
	private boolean updated = false;

	/**
	 * specify Connection Manager.
	 */
	private IConnectionManager connectionManager;

	/**
	 * This method will be used to establish the session with the database.
	 * Declared in AbstractDAO class.
	 * @param sessionDataBean session Data.
	 * @throws DAOException generic DAOException.
	 */
	public void openSession(SessionDataBean sessionDataBean) throws DAOException
	{

		try
		{
			session = connectionManager.currentSession();
			transaction = session.beginTransaction();
			auditManager = new AuditManager();

			if (sessionDataBean != null)
			{
				auditManager.setUserId(sessionDataBean.getUserId());
				auditManager.setIpAddress(sessionDataBean.getIpAddress());
			}
		/*
		 * TODO Removed ..check if some issues occur because of this
		 * 	else
			 {
				auditManager.setUserId(null);
			}
		 */
		}
		catch (HibernateException dbex)
		{
			logger.error(dbex.getMessage(), dbex);
			throw handleError(Constants.GENERIC_DATABASE_ERROR, dbex);
		}
	}

	/**
	 * This method will be used to close the session with the database.
	 * Declared in AbstractDAO class.
	 * @throws DAOException generic DAOException.
	 */
	public void closeSession() throws DAOException
	{
		try
		{
			getConnectionManager().closeSession();
		}
		catch (HibernateException dx)
		{
			logger.error(dx.getMessage(), dx);
			throw handleError(Constants.GENERIC_DATABASE_ERROR, dx);
		}
		session = null;
		transaction = null;
		auditManager = null;
	}

	/**
	 * Commit the database level changes.
	 * Declared in AbstractDAO class.
	 * @throws DAOException generic DAOException.
	 */
	public void commit() throws DAOException
	{
		try
		{
			auditManager.insert(this);

			if (transaction != null)
			{
				transaction.commit();
			}
		}
		catch (HibernateException dbex)
		{
			logger.error(dbex.getMessage() , dbex);
			throw handleError("Error in commit: " , dbex) ;
		}
	}

	/**
	 * RollBack all the changes after last commit.
	 * Declared in AbstractDAO class.
	 * @throws DAOException generic DAOException.
	 */
	public void rollback() throws DAOException
	{
		/**
		 * the isUpdated==true is removed because if there is cascade save-update
		 * and the association collection objects
		 * is violating constaring then in insert() method session.save() is throwing exception
		 * and isUpdated is not getting set to true.
		 * Because of this roll back is not happening on parent object.
		 *
		 */
		if (updated)
		{
			try
			{
				if (transaction != null)
				{
					transaction.rollback();
				}
			}
			catch (HibernateException dbex)
			{
				logger.error(dbex.getMessage(), dbex);
				throw handleError("Error in rollback: ", dbex);
			}
		}
	}

	/**
	 * Disabled Related Objects.
	 * @param tableName table Name.
	 * @param whereColumnName Column name to be included in where clause.
	 * @param whereColumnValues Value of the Column name that included in where clause.
	 * @throws DAOException generic DAOException.
	 * --have to check this
	 */
	public void disableRelatedObjects(String tableName, String whereColumnName,
			Long[] whereColumnValues) throws DAOException
	{

		DatabaseConnectionParams  databaseConnectionParams = new DatabaseConnectionParams();
		try
		{
			databaseConnectionParams.setConnection(session.connection());
			//Statement statement = session.connection().createStatement();

			StringBuffer buff = new StringBuffer();
			for (int i = 0; i < whereColumnValues.length; i++)
			{
				buff.append(whereColumnValues[i].longValue());
				if ((i + 1) < whereColumnValues.length)
				{
					buff.append("  ,");
				}
			}
			String sql = "UPDATE " + tableName + " SET ACTIVITY_STATUS = '"
					+ Constants.ACTIVITY_STATUS_DISABLED + "' WHERE "
					+ whereColumnName + " IN ( "
					+ buff.toString() + ")";
			databaseConnectionParams.executeUpdate(sql);
		}
		catch (HibernateException dbex)
		{
			logger.error(dbex.getMessage(), dbex);
			throw handleError("Error in JDBC connection: ", dbex);
		}
		finally
		{
			databaseConnectionParams.closeConnectionParams();
		}

	}

	/**
	 * Saves the persistent object in the database.
	 * @param obj The object to be saved.
	 * @param sessionDataBean The session in which the object is saved.
	 * @param isAuditable is Auditable.
	 * @param isSecureInsert is Secure Insert.
	 * @throws DAOException generic DAOException.
	 * @throws UserNotAuthorizedException User Not Authorized Exception.
	 */
	public void insert(Object obj, SessionDataBean sessionDataBean, boolean isAuditable,
			boolean isSecureInsert) throws DAOException, UserNotAuthorizedException
	{
		try
		{
			session.save(obj);
			isObjectAuditable(obj, isAuditable);
			updated = true;
		}
		catch (HibernateException hibExp)
		{
			throw handleError("", hibExp);
		}
		catch (AuditException hibExp)
		{
			throw handleError("", hibExp);
		}


	}

	/**
	 * @param obj :Object
	 * @param isAuditable : This will be true if object will be Auditable.
	 * @throws AuditException :Exception thrown
	 */
	private void isObjectAuditable(Object obj, boolean isAuditable)
			throws AuditException
	{

		if (obj instanceof Auditable && isAuditable)
		{
			auditManager.compare((Auditable) obj, null, "INSERT");
		}
	}

	/**
	 * Handles Error.
	 * @param message message
	 * @param hibExp Exception
	 * @return DAOException
	 */
	private DAOException handleError(String message, Exception hibExp)
	{
		logger.error(hibExp.getMessage(), hibExp);
		String msg = generateErrorMessage(message, hibExp);
		return new DAOException(msg, hibExp);
	}

	/**
	 * Generates Error Message.
	 * @param messageToAdd message To Add.
	 * @param exception Exception
	 * @return message.
	 */
	private String generateErrorMessage(String messageToAdd, Exception exception)
	{
		String returnMessage;
		if (exception instanceof HibernateException)
		{
			HibernateException hibernateException = (HibernateException) exception;
			StringBuffer message = new StringBuffer(messageToAdd);
			String [] str = hibernateException.getMessages();

			//TODO have to look for this message won't be null ever .
			if (message != null)
			{
				for (int i = 0; i < str.length; i++)
				{
					message.append(str[i]).append("   ");
				}
				returnMessage = message.toString();
			}
			else
			{
				returnMessage = "Unknown Error";
			}

		}
		else
		{
			returnMessage = exception.getMessage();
		}
		return returnMessage;
	}

	/**
	 * Updates the persistent object in the database.
	 * @param obj The object to be updated.
	 * @throws DAOException generic DAOException.
	 */
	public void update(Object obj) throws DAOException
	{
		try
		{
			session.update(obj);
			updated = true;

		}
		catch (HibernateException hibExp)
		{
			throw handleError("", hibExp);
		}
	}

	/**
	 * Audit.
	 * @param obj The object to be audited.
	 * @param oldObj old Object.
	 * @param sessionDataBean session Data.
	 * @param isAuditable is Auditable.
	 * @throws DAOException generic DAOException.
	 */
	public void audit(Object obj, Object oldObj, SessionDataBean sessionDataBean,
			boolean isAuditable) throws DAOException
	{
		try
		{
			if (obj instanceof Auditable && isAuditable)
			{
				auditManager.compare((Auditable) obj, (Auditable) oldObj, "UPDATE");
			}

		}
		catch (AuditException hibExp)
		{
			throw handleError("", hibExp);
		}
	}

	/**
	 * add Audit Event Logs.
	 * @param auditEventDetailsCollection audit Event Details Collection.
	 */
	public void addAuditEventLogs(Collection<Object> auditEventDetailsCollection)
	{
		auditManager.addAuditEventLogs(auditEventDetailsCollection);
	}

	/**
	 * Deletes the persistent object from the database.
	 * @param obj The object to be deleted.
	 * @throws DAOException generic DAOException.
	 */
	public void delete(Object obj) throws DAOException
	{
		try
		{
			session.delete(obj);

		}
		catch (HibernateException hibExp)
		{
			logger.error(hibExp.getMessage() , hibExp);
			throw new DAOException("Error in delete" , hibExp);
		}
	}

	/**
	 * Retrieves all the records for class name in sourceObjectName.
	 * @param sourceObjectName Contains the class Name whose records are to be retrieved.
	 * @return List.
	 * @throws DAOException generic DAOException.
	 */
	public List<Object> retrieve(String sourceObjectName) throws DAOException
	{
		String[] selectColumnName = null;

		QueryWhereClause queryWhereClause = new QueryWhereClause();
		queryWhereClause.setWhereClause(null, null, null, null);

		return retrieve(sourceObjectName, selectColumnName, queryWhereClause);
	}

	/**
	 * Retrieves all the records for class name in sourceObjectName.
	 * @param sourceObjectName Contains the class Name whose records are to be retrieved.
	 * @param whereColumnName Column name to be included in where clause.
	 * @param whereColumnValue Value of the Column name that included in where clause.
	 * @return List.
	 * @throws DAOException generic DAOException.
	 */
	public List<Object> retrieve(String sourceObjectName, String whereColumnName, Object whereColumnValue)
			throws DAOException
	{
		String [] whereColumnNames =  {whereColumnName};
		String [] colConditions =  {"="};
		Object [] whereColumnValues =  {whereColumnValue};
		String[] selectColumnName = null;

		QueryWhereClause queryWhereClause = new QueryWhereClause();
        queryWhereClause.setWhereClause(whereColumnNames,
				colConditions, whereColumnValues, Constants.AND_JOIN_CONDITION);

		return retrieve(sourceObjectName, selectColumnName,queryWhereClause);
	}

	/**
	 * @param sourceObjectName Contains the class Name whose records are to be retrieved.
	 * @param selectColumnName select Column Name.
	 * @return List.
	 * @throws DAOException generic DAOException.
	 */
	public List<Object> retrieve(String sourceObjectName, String[] selectColumnName)
	throws DAOException
	{

		QueryWhereClause queryWhereClause = new QueryWhereClause();
		queryWhereClause.setWhereClause(null, null, null, null);

		return retrieve(sourceObjectName, selectColumnName,queryWhereClause);
	}

	/**
	 * Retrieves the records for class name in sourceObjectName according
	 * to field values passed in the passed session.
	 * @param sourceObjectName source Object Name.
	 * @param selectColumnName select Column Name.
	 * @param queryWhereClause where column conditions
	 * @return List.
	 * @throws DAOException generic DAOException.
	 */
	public List<Object> retrieve(String sourceObjectName,String[] selectColumnName,
			QueryWhereClause queryWhereClause) throws DAOException
			{
		List<Object> list;
		try
		{
			StringBuffer sqlBuff = new StringBuffer();
			String className = DAOUtility.parseClassName(sourceObjectName);
			Query query;

			generateSelectPartOfQuery(selectColumnName, sqlBuff, className);
			generateFromPartOfQuery(sourceObjectName, sqlBuff, className);

			if (queryWhereClause.isConditionSatisfied())
            {
				sqlBuff.append(queryWhereClause.toString(className));
				query = session.createQuery(sqlBuff.toString());
				queryWhereClause.setParametersToQuery(query);
			}
			else
			{
				query = session.createQuery(sqlBuff.toString());
			}

			list = query.list();

		}
		catch (HibernateException hibExp)
		{
			logger.error(hibExp.getMessage(), hibExp);
			throw new DAOException("Error in retrieve " + hibExp.getMessage(), hibExp);
		}
		catch (Exception exp)
		{
			logger.error(exp.getMessage(), exp);
			throw new DAOException("Logical Erroe in retrieve method " + exp.getMessage(), exp);
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
	public Object retrieve(String sourceObjectName, Long identifier)
	 throws DAOException
	 {
		try
		{
			Object object = session.load(Class.forName(sourceObjectName), identifier);
			HibernateProxy hibernatProxy = (HibernateProxy) object;
			return (Object)hibernatProxy.getHibernateLazyInitializer().getImplementation();
			//return HibernateMetaData.getProxyObjectImpl(object);
		}
		catch (ClassNotFoundException cnFoundExp)
		{
			logger.error(cnFoundExp.getMessage(), cnFoundExp);
			throw new DAOException("Error in retrieve " + cnFoundExp.getMessage(), cnFoundExp);
		}
		catch (HibernateException hibExp)
		{
			logger.error(hibExp.getMessage(), hibExp);
			throw new DAOException("Error in retrieve " + hibExp.getMessage(), hibExp);
		}
	}

	/**
	 * Loads Clean Object.
	 * @param sourceObjectName source Object Name.
	 * @param identifier identifier.
	 * @return object.
	 * @throws DAOException It will throw the DAOException
	 */
	public Object loadCleanObj(String sourceObjectName, Long identifier)
	 throws DAOException
	{
		Object obj = retrieve(sourceObjectName, identifier);
		session.evict(obj);
		return obj;
	}

	/**
	 * Executes the HQL query.
	 * @param query HQL query to execute.
	 * @param sessionDataBean session Data
	 * @param isSecureExecute is Secure Execute.
	 * @param queryResultObjectDataMap query Result Object Data Map.
	 * @return List.
	 * @throws ClassNotFoundException Class Not Found Exception.
	 * @throws DAOException generic DAOException.
	 */
	public List<Object> executeQuery(String query, SessionDataBean sessionDataBean,
			boolean isSecureExecute, Map<Object,QueryResultObjectDataBean>
			queryResultObjectDataMap) throws ClassNotFoundException,
			DAOException
	{
		List < Object > returner;
		try
		{
			Query hibernateQuery = session.createQuery(query);
			returner = hibernateQuery.list();

		}
		catch (HibernateException e)
		{
			throw (new DAOException(e));
		}

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
		try
		{
			String objClassName = objClass.getName();
			String simpleName = objClass.getSimpleName();
			String nameOfAttribute = DAOUtility.createAttributeNameForHQL(simpleName, attributeName);
			StringBuffer queryStringBuffer = new StringBuffer();

			queryStringBuffer.append("Select ").append(nameOfAttribute).
			append(" from ").append(objClassName).append("    ").append(simpleName).
			append(" where ").append(simpleName).append(".").append(columnName).
			append("=  ").append(identifier);

			return session.createQuery(queryStringBuffer.toString()).list();
		}
		catch (HibernateException exception)
		{
			throw new DAOException(exception.getMessage(), exception);
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
		if (selectColumnName != null && selectColumnName.length > 0)
		 {
		    sqlBuff.append("Select ");
		    for (int i = 0; i < selectColumnName.length; i++)
		     {
		        sqlBuff.append(DAOUtility.createAttributeNameForHQL(className, selectColumnName[i]));
		        if (i != selectColumnName.length - 1)
		        {
		            sqlBuff.append(", ");
		        }
		    }
		    sqlBuff.append("   ");
		}
	}

	/** (non-Javadoc).
	 * @see edu.wustl.dao.DAO#setConnectionManager(edu.wustl.dao.connectionmanager.IConnectionManager)
	 * @param connectionManager : Connection Manager
	 */
	public void setConnectionManager(IConnectionManager connectionManager)
	 {
		this.connectionManager = connectionManager;

	}

	/**(non-Javadoc).
	 * @see edu.wustl.dao.DAO#getConnectionManager()
	 * @return : It returns the Connection Manager
	 */
	public IConnectionManager getConnectionManager()
	{
		return connectionManager;
	}

	/**
	 * This method opens a new session, loads an object with given class and Id,
	 * and closes the session. This method should be used only when an object is
	 * to be opened in separate session.
	 *
	 * @param objectClass class of the object
	 * @param identifier id of the object
	 * @return object
	 * Have to remove this method::::
	 */
	public Object loadCleanObj(Class objectClass, Long identifier)
	 {
		Session session = null;
		try
		{
			session = getConnectionManager().getSessionFactory().openSession();
			return session.load(objectClass, identifier);
		}
		finally
		{
			if(session != null)
			{
				session.close();
			}
		}
	}

}