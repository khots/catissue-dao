
package edu.wustl.dao.newdao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.domain.AuditEvent;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.AuditException;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.dao.util.DAOUtility;
import edu.wustl.dao.util.NamedQueryParam;

public abstract class GenericHibernateDAO<T, ID extends Serializable> implements DAO<T,ID>
{

	/**
	* LOGGER Logger - class logger.
	*/
	private static final Logger logger = Logger.getCommonLogger(GenericHibernateDAO.class);
	
	private String applicationName;
	
	private SessionDataBean sessionDataBean;
	
	private Class<T> persistentClass;

	/**
	* AuditManager for auditing.
	*/
	private AuditManager auditManager;

	public GenericHibernateDAO(String applicationName,SessionDataBean sessionDataBean)
	{
		this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		this.applicationName = applicationName;
		this.sessionDataBean = sessionDataBean;
	}

	public Class<T> getPersistentClass()
	{
		return persistentClass;
	}

	protected Session getSession()
	{
		return SessionFactoryHolder.getInstance().getSessionFactory(applicationName).getCurrentSession();
	}
	
	protected Session getNewSession()
	{
		return SessionFactoryHolder.getInstance().getSessionFactory(applicationName).openSession();
	}
	/**
	 * Insert the Object to the database.
	 * @param obj Object to be inserted in database
	 * @throws DAOException generic DAOException
	 */
	public void insert(Object obj) throws DAOException
	{
		try
		{
			getSession().save(obj);
			auditManager.audit(obj, null, "INSERT");
			insertAudit();
		}
		catch (HibernateException hibExp)
		{
			logger.error(hibExp.getMessage(), hibExp);
			throw DAOUtility.getInstance().getDAOException(hibExp, "db.insert.data.error",
					"GenericHibernateDAO.java ");
		}
		catch (AuditException exp)
		{

			logger.warn(exp.getMessage());
			logger.debug(exp.getMessage(), exp);
		}
	}

	/**
	 * updates the object into the database.
	 * @param currentObj Object to be updated in database
	 * @throws DAOException : generic DAOException
	 */
	public void update(Object currentObj) throws DAOException
	{
		logger.debug("Update Object");
		try
		{
			Object previousObj = retrieveOldObject(currentObj);
			update(currentObj, previousObj);
		}
		catch (HibernateException hibExp)
		{
			logger.error(hibExp.getMessage(), hibExp);
			throw DAOUtility.getInstance().getDAOException(hibExp, "db.update.data.error",
					"GenericHibernateDAO.java ");
		}
	}

	/**
	 * This method will be called to retrieve the oldObject.
	 * @param currentObj Object whose old values has to be fetched from database.
	 * @return old Object.
	 * @throws DAOException database exception.
	 */
	private Object retrieveOldObject(Object currentObj) throws DAOException
	{
		Session session = null;
		try
		{
			Long objectId = auditManager.getObjectId(currentObj);
			return getNewSession().get(Class.forName(currentObj.getClass().getName()), objectId);
		}
		catch (AuditException exp)
		{
			logger.warn(exp.getMessage());
			logger.debug(exp.getMessage(), exp);
			return null;
			/*throw DAOUtility.getInstance().getDAOException(exp, exp.getErrorKeyName(),
					exp.getMsgValues());*/
		}
		catch (HibernateException hibExp)
		{
			logger.error(hibExp.getMessage(), hibExp);
			return null;
			//	throw DAOUtility.getInstance().getDAOException(hibExp, "db.update.data.error",
			//"HibernateDAOImpl.java ");
		}
		catch (ClassNotFoundException exp)
		{
			logger.error(exp.getMessage(), exp);
			return null;
			//throw DAOUtility.getInstance().getDAOException(exp, "class.not.found.error",
			//currentObj.getClass().getName());
		}
		finally
		{
			if (session != null)
			{
				session.close();
				session = null;
			}
		}
	}

	/**
	 * This method will be called when user need to audit and update the changes.
	 * @param currentObj object with new changes
	 * * @param previousObj persistent object fetched from database.
	 * @throws DAOException : generic DAOException
	 */
	public void update(Object currentObj, Object previousObj) throws DAOException
	{
		logger.debug("Update Object");
		try
		{
			getSession().merge(currentObj);
			auditManager.audit(currentObj, previousObj, "UPDATE");
			insertAudit();
		}
		catch (HibernateException hibExp)
		{
			logger.error(hibExp.getMessage(), hibExp);
			throw DAOUtility.getInstance().getDAOException(hibExp, "db.update.data.error",
					"GenericHibernateDAO.java ");
		}
		catch (AuditException exp)
		{
			logger.warn(exp.getMessage());
			logger.debug(exp.getMessage(), exp);
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
			getSession().delete(obj);
		}
		catch (HibernateException hibExp)
		{
			logger.error(hibExp.getMessage(),hibExp);
			throw DAOUtility.getInstance().getDAOException(hibExp, "db.delete.data.error",
			"GenericHibernateDAO.java ");

		}
	}

	public T findById(ID id)
	{
		T entity = (T) getSession().load(getPersistentClass(), id);

		return entity;
	}

	/**
	 * Create a new instance of Query for the given HQL query string.
	 * Execute the Query and returns the list of data.
	 * @param query query
	 * @return List.
	 *  API exposed to support this issue.
	 */
	public List<T> executeQuery(String query) throws DAOException
	{
		return executeQuery(query,null, null,null);
	}
	

	/**
	 * Executes the HQL query.
	 * @param query HQL query to execute.
	 * @param columnValueBeans column data beans.
	 * @return list of data.
	 * @throws DAOException Database exception.
	 */
	public List<T> executeQuery(String query, List<ColumnValueBean> columnValueBeans) throws DAOException
	{
		return executeQuery(query,null, null,columnValueBeans);
	}

	
	public List executeQuery(String query,Integer startIndex,Integer maxRecords,List<ColumnValueBean> columnValueBeans) throws DAOException
	{
		logger.debug("Execute query");
		try
		{
	    	Query hibernateQuery = getSession().createQuery(query);
	    	if(startIndex != null && maxRecords !=null )
	    	{
	    		hibernateQuery.setFirstResult(startIndex.intValue());
	    		hibernateQuery.setMaxResults(maxRecords.intValue());
	    	}
	    	if(columnValueBeans != null)
			{
				Iterator<ColumnValueBean> colValItr =  columnValueBeans.iterator();
				while(colValItr.hasNext())
				{
					ColumnValueBean colValueBean = colValItr.next();
					hibernateQuery.setParameter(colValueBean.getColumnName(),
							colValueBean.getColumnValue());
				}
			}
		    return hibernateQuery.list();

		}
		catch(HibernateException hiberExp)
		{
			logger.error(hiberExp.getMessage(),hiberExp);
			throw DAOUtility.getInstance().getDAOException(hiberExp, "db.retrieve.data.error",
					"GenericHibernateDAO.java "+query);
		}
	}
	
	
	/**
	 * This method returns named query.
	 * @param queryName : handle for named query.
	 * @param namedQueryParams : Map holding the parameter type and parameter value.
	 * @return the list of data.
	 * @throws DAOException : database exception.
	 */
	public List executeNamedQuery(String queryName,Map<String, NamedQueryParam> namedQueryParams)
	throws DAOException
	{
		logger.debug("Execute named query");
		try
		{
			Query query = getSession().getNamedQuery(queryName);
			DAOUtility.getInstance().substitutionParameterForQuery(query, namedQueryParams);
			return query.list();
		}
		catch(HibernateException hiberExp)
		{
			logger.error(hiberExp.getMessage(),hiberExp);
			throw DAOUtility.getInstance().getDAOException(hiberExp, "db.retrieve.data.error",
					"GenericHibernateDAO.java "+queryName);
		}
	}
	

	/**
	 * This method inserts audit Event details in database.
	 * @throws DAOException generic DAOException.
	 */
	public void insertAudit() throws DAOException
	{
		if (auditManager.getAuditEvent() != null
				&& !auditManager.getAuditEvent().getAuditEventLogCollection().isEmpty())
		{
			getSession().save(auditManager.getAuditEvent());
		}
		auditManager.setAuditEvent(new AuditEvent());
		auditManager.initializeAuditManager(sessionDataBean);
	}
}
