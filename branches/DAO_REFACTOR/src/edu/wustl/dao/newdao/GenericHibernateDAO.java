
package edu.wustl.dao.newdao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;

import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.domain.AuditEvent;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.QueryWhereClause;
import edu.wustl.dao.condition.EqualClause;
import edu.wustl.dao.exception.AuditException;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.dao.util.DAOUtility;
import edu.wustl.dao.util.NamedQueryParam;

public class GenericHibernateDAO<T, ID extends Serializable> implements DAO<T, ID>
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

	public GenericHibernateDAO(String applicationName, SessionDataBean sessionDataBean)
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
		return SessionFactoryHolder.getInstance().getSessionFactory(applicationName)
				.getCurrentSession();
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
		Session session = getSession();
		try
		{
			Long objectId = auditManager.getObjectId(currentObj);
			session.evict(currentObj);
			return getSession().get(Class.forName(currentObj.getClass().getName()), objectId);
		}
		catch (AuditException exp)
		{
			logger.warn(exp.getMessage());
			logger.debug(exp.getMessage(), exp);
			return null;
		}
		catch (HibernateException hibExp)
		{
			logger.error(hibExp.getMessage(), hibExp);
			return null;
		}
		catch (ClassNotFoundException exp)
		{
			logger.error(exp.getMessage(), exp);
			return null;
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
			logger.error(hibExp.getMessage(), hibExp);
			throw DAOUtility.getInstance().getDAOException(hibExp, "db.delete.data.error",
					"GenericHibernateDAO.java ");

		}
	}

	public T findById(ID id)
	{
		T entity = (T) getSession().load(getPersistentClass(), id);

		return entity;
	}

	public List<T> findAll()
	{
		return findByCriteria();
	}

	@SuppressWarnings("unchecked")
	protected List<T> findByCriteria(Criterion... criterion)
	{
		Criteria crit = getSession().createCriteria(getPersistentClass());
		for (Criterion c : criterion)
		{
			crit.add(c);
		}
		return crit.list();
	}



	public List executeQuery(String query, Integer startIndex, Integer maxRecords,
			List<ColumnValueBean> columnValueBeans) throws DAOException
	{
		logger.debug("Execute query");
		try
		{
			Query hibernateQuery = getSession().createQuery(query);
			if (startIndex != null && maxRecords != null)
			{
				hibernateQuery.setFirstResult(startIndex.intValue());
				hibernateQuery.setMaxResults(maxRecords.intValue());
			}
			setQueryParametes(hibernateQuery, columnValueBeans);
			return hibernateQuery.list();

		}
		catch (HibernateException hiberExp)
		{
			logger.error(hiberExp.getMessage(), hiberExp);
			throw DAOUtility.getInstance().getDAOException(hiberExp, "db.retrieve.data.error",
					"GenericHibernateDAO.java " + query);
		}
	}

	/**
	 * This method returns named query.
	 * @param queryName : handle for named query.
	 * @param namedQueryParams : Map holding the parameter type and parameter value.
	 * @return the list of data.
	 * @throws DAOException : database exception.
	 */
	public List executeNamedQuery(String queryName, List<ColumnValueBean> columnValueBeans)
			throws DAOException
	{
		logger.debug("Execute named query");
		try
		{
			Query query = getSession().getNamedQuery(queryName);
			setQueryParametes(query, columnValueBeans);
			return query.list();
		}
		catch (HibernateException hiberExp)
		{
			logger.error(hiberExp.getMessage(), hiberExp);
			throw DAOUtility.getInstance().getDAOException(hiberExp, "db.retrieve.data.error",
					"GenericHibernateDAO.java " + queryName);
		}
	}

	private void setQueryParametes(Query query, List<ColumnValueBean> columnValueBeans)
	{
		if (columnValueBeans != null)
		{
			Iterator<ColumnValueBean> colValItr = columnValueBeans.iterator();
			while (colValItr.hasNext())
			{
				ColumnValueBean colValueBean = colValItr.next();
				query.setParameter(colValueBean.getColumnName(), colValueBean.getColumnValue());
			}
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

	/**
	 * Retrieves the records for class name in sourceObjectName according
	 * to field values passed in the passed session.
	 * @param sourceObjectName source Object Name.
	 * @param selectColumnName select Column Name.
	 * @param queryWhereClause where column conditions
	 * @param onlyDistinctRows true if only distinct rows should be selected.
	 * @return List.
	 * @deprecated
	 * @throws DAOException generic DAOException.
	 */
	public List retrieve(String sourceObjectName, String[] selectColumnName,
			QueryWhereClause queryWhereClause, boolean onlyDistinctRows) throws DAOException
	{
		logger.debug("Inside retrieve method");
		List list;
		try
		{
			StringBuffer queryStrBuff = new StringBuffer();
			String className = DAOUtility.getInstance().parseClassName(sourceObjectName);
			Query query;

			generateSelectPartOfQuery(selectColumnName, queryStrBuff, className);
			generateFromPartOfQuery(sourceObjectName, queryStrBuff, className);

			if (queryWhereClause != null)
			{
				queryStrBuff.append(queryWhereClause.toWhereClause());
			}
			query = getSession().createQuery(queryStrBuff.toString());

			list = query.list();

		}
		catch (HibernateException hibExp)
		{
			logger.error(hibExp.getMessage(), hibExp);
			throw DAOUtility.getInstance().getDAOException(hibExp, "db.retrieve.data.error",
					"HibernateDAOImpl.java ");

		}
		return list;
	}

	/**
	 * Generate Select Block.
	 * @param selectColumnName select Column Name.
	 * @param sqlBuff sqlBuff
	 * @param className class Name.
	 */
	private void generateSelectPartOfQuery(String[] selectColumnName, StringBuffer sqlBuff,
			String className)
	{
		logger.debug("Prepare select part of query.");
		if (selectColumnName != null && selectColumnName.length > 0)
		{
			sqlBuff.append("Select ");
			for (int i = 0; i < selectColumnName.length; i++)
			{
				sqlBuff.append(DAOUtility.getInstance().createAttributeNameForHQL(className,
						selectColumnName[i]));
				if (i != selectColumnName.length - 1)
				{
					sqlBuff.append(", ");
				}
			}
			sqlBuff.append("   ");
		}
	}

	/**
	 * @param sourceObjectName source Object Name.
	 * @param sqlBuff query buffer
	 * @param className gives the class name
	 */
	private void generateFromPartOfQuery(String sourceObjectName, StringBuffer sqlBuff,
			String className)
	{
		logger.debug("Prepare from part of query");
		sqlBuff.append("from " + sourceObjectName + " " + className);
	}

	/**
	 * Retrieves the records for class name in sourceObjectName according
	 * to field values passed in the passed session.
	 * @param sourceObjectName source Object Name.
	 * @param selectColumnName select Column Name.
	 * @param queryWhereClause where column conditions
	 * @return List.
	 * @deprecated
	 * @throws DAOException generic DAOException.
	 */
	public List retrieve(String sourceObjectName, String[] selectColumnName,
			QueryWhereClause queryWhereClause) throws DAOException
	{
		return retrieve(sourceObjectName, selectColumnName, queryWhereClause, false);
	}

	/**
	 * Retrieves all the records for class name in sourceObjectName.
	 * @param sourceObjectName Contains the class Name whose records are to be retrieved.
	 * @return List.
	 * @throws DAOException generic DAOException.
	 */
	public List retrieve(String sourceObjectName) throws DAOException
	{
		logger.debug("Inside retrieve method");
		String[] selectColumnName = null;
		return retrieve(sourceObjectName, selectColumnName, null, false);
	}

	/**
	 * Retrieves all the records for class name in sourceObjectName.
	 * @param sourceObjectName Contains the class Name whose records are to be retrieved.
	 * @param whereColumnName Column name to be included in where clause.
	 * @param whereColumnValue Value of the Column name that included in where clause.
	 * @return List.
	 * @deprecated
	 * @throws DAOException generic DAOException.
	 */
	public List retrieve(String sourceObjectName, String whereColumnName, Object whereColumnValue)
			throws DAOException
	{
		logger.debug("Inside retrieve method");
		String[] selectColumnName = null;

		QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
		queryWhereClause.addCondition(new EqualClause(whereColumnName, whereColumnValue));

		return retrieve(sourceObjectName, selectColumnName, queryWhereClause, false);
	}

	/**
	 * @param sourceObjectName Contains the class Name whose records are to be retrieved.
	 * @param selectColumnName select Column Name.
	 * @return List.
	 * @throws DAOException generic DAOException.
	 */
	public List retrieve(String sourceObjectName, String[] selectColumnName) throws DAOException
	{
		logger.debug("Inside retrieve method");
		return retrieve(sourceObjectName, selectColumnName, null, false);
	}

	/**
	 * Returns the ResultSet containing all the rows from the table represented in sourceObjectName
	 * according to the where clause.It will create the where condition clause which holds where column name,
	 * value and conditions applied.
	 * @param sourceObjectName The table name.
	 * @param columnValueBean columnValueBean
	 * @return The ResultSet containing all the rows from the table represented
	 * in sourceObjectName which satisfies the where condition
	 * @throws DAOException : DAOException
	 */
	public List retrieve(String sourceObjectName, ColumnValueBean columnValueBean)
			throws DAOException
	{
		logger.debug("Inside retrieve method");
		String[] selectColumnName = null;

		QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
		queryWhereClause.addCondition(new EqualClause(columnValueBean.getColumnName(), '?'));

		List<ColumnValueBean> columnValueBeans = new ArrayList<ColumnValueBean>();
		columnValueBeans.add(columnValueBean);
		return retrieve(sourceObjectName, selectColumnName, queryWhereClause, false,
				columnValueBeans);
	}

	/**
	 * Retrieve and returns the list of all source objects that satisfy the
	 * for given conditions on a various columns.
	 * @param sourceObjectName Source object's name to be retrieved from database.
	 * @param selectColumnName Column names in SELECT clause of the query.
	 * @param queryWhereClause : This will hold following:
	 * 1.whereColumnName Array of column name to be included in where clause.
	 * 2.whereColumnCondition condition to be satisfy between column and its value.
	 * e.g. "=", "!=", "<", ">", "in", "null" etc
	 * 3. whereColumnValue Value of the column name that included in where clause.
	 * 4.joinCondition join condition between two columns. (AND, OR)
	 * @param onlyDistinctRows true if only distinct rows should be selected.
	 * @param columnValueBeans columnValueBeans
	 * @return the list of all source objects that satisfy the search conditions.
	 * @throws DAOException generic DAOException.
	 *  */
	public List retrieve(String sourceObjectName, String[] selectColumnName,
			QueryWhereClause queryWhereClause, boolean onlyDistinctRows,
			List<ColumnValueBean> columnValueBeans) throws DAOException
	{
		logger.debug("Inside retrieve method !!");
		try
		{
			StringBuffer queryStrBuff = new StringBuffer();
			String className = DAOUtility.getInstance().parseClassName(sourceObjectName);

			generateSelectPartOfQuery(selectColumnName, queryStrBuff, className);
			generateFromPartOfQuery(sourceObjectName, queryStrBuff, className);

			if (queryWhereClause != null)
			{
				queryStrBuff.append(queryWhereClause.toWhereClause());
			}
			return executeQuery(queryStrBuff.toString(), null,null,columnValueBeans);

		}
		catch (HibernateException hibExp)
		{
			logger.error(hibExp.getMessage(), hibExp);
			throw DAOUtility.getInstance().getDAOException(hibExp, "db.retrieve.data.error",
					"HibernateDAOImpl.java ");

		}

	}
}
