package edu.wustl.dao;

import java.sql.Connection;
import java.util.Collection;

import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.audit.Auditable;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.domain.AuditEventLog;
import edu.wustl.common.exception.AuditException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.connectionmanager.IConnectionManager;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.DAOConstants;

/**
 * @author kalpana_thakur
 *
 */
public abstract class AbstractDAOImpl implements DAO
{

	/**
	 * Audit Manager.
	 */
	protected AuditManager auditManager;

	/**
	 * Connection Manager.
	 */
	protected IConnectionManager connectionManager ;

	/**
	 * Batch size.
	 */
	protected int batchSize = 1;

	/**
	 * Class Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(AbstractDAOImpl.class);
	/**
	 * Audit.
	 * @param obj The object to be audited.
	 * @param oldObj old Object.
	 * @param sessionDataBean session Data.
	 * @param isAuditable is Auditable.
	 * @throws DAOException generic DAOException.
	 *//*
	public void audit(Object obj, Object oldObj,
			SessionDataBean sessionDataBean, boolean isAuditable)
			throws DAOException
	{
		logger.debug("Inside Audit method");
		try
		{
			auditManager.compare(obj, (Auditable) oldObj, "UPDATE",isAuditable);
		}
		catch (AuditException auditExp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.audit.error");
			throw new DAOException(errorKey,auditExp,"AbstractDAOImpl.java :"+
					DAOConstants.AUDIT_ERROR);
		}

	}
*/
	/**
	 * add Audit Event Logs.
	 * @param auditEventDetailsCollection audit Event Details Collection.
	 */
	public void addAuditEventLogs(Collection<AuditEventLog> auditEventDetailsCollection)
	{
		logger.debug("Add audit event logs");
		auditManager.addAuditEventLogs(auditEventDetailsCollection);
	}

	/**
	 * This will be called to initialized the Audit Manager.
	 * @param sessionDataBean : This will holds the session data.
	 * @return AuditManager : instance of AuditManager
	 */
	private static AuditManager getAuditManager(SessionDataBean sessionDataBean)
	{
		logger.debug("Initialize audit manager");
		AuditManager auditManager = new AuditManager();
		if (sessionDataBean == null)
		{
			auditManager.setUserId(null);
		}
		else
		{
			auditManager.setUserId(sessionDataBean.getUserId());
			auditManager.setIpAddress(sessionDataBean.getIpAddress());
		}
		return auditManager;
	}

	/**
	 * This method will be used to establish the session with the database.
	 * Declared in  class.
	 * @param sessionDataBean session Data.
	 * @throws DAOException generic DAOException.
	 */
	public void openSession(SessionDataBean sessionDataBean)
	throws DAOException
	{
		logger.debug("Open the session");
	    auditManager = getAuditManager(sessionDataBean);

	}

	/**
	 * This method will be used to close the session with the database.
	 * Declared in DAO class.
	 * @throws DAOException : It will throw DAOException.
	 */
	public void closeSession() throws DAOException
	{
		logger.debug("Close the session");
		auditManager = null;
	}

	/**
	 * Commit the database level changes.
	 * Declared in  class.
	 * @throws DAOException generic DAOException.
	 */
	public void commit() throws DAOException
	{
		logger.debug("Session commit");
		auditManager.insert(this);

	}

	/**
	 * RollBack all the changes after last commit.
	 * Declared in  class.
	 * @throws DAOException generic DAOException.
	 */
	public abstract void rollback() throws DAOException;

	/**
	 * This method will be called to set connection Manager object.
	 * @param connectionManager : Connection Manager.
	 */
	public void setConnectionManager(IConnectionManager connectionManager)
	{
		logger.debug("Setting the connection manager");
		this.connectionManager = connectionManager;
	}

	/**(non-Javadoc).
	 * @see edu.wustl.dao.DAO#getConnectionManager()
	 * @return : It returns the Connection Manager
	 */
	protected IConnectionManager getConnectionManager()
	{
		logger.debug("Get the connection manager");
		return connectionManager;
	}

	/**
	 *This method will be called to retrieved the current connection object.
	 *@return Connection object
	 *@throws DAOException :Generic DAOException.
	 */
	public Connection getCleanConnection() throws DAOException
	{
		logger.debug("Get clean connection");
		return 	connectionManager.getCleanConnection();
	}

	/**
	 *This method will be called to close current connection.
	 *@throws DAOException :Generic DAOException.
	 */
	public void closeCleanConnection() throws DAOException
	{
		logger.debug("Close clean connection");
		connectionManager.closeCleanConnection();
	}
	/**
	 * This method will be called to set the size of the batch.
	 * @param batchSize batchSize
	 * @throws DAOException : Generic database exception.
	 */
	public void setBatchSize(int batchSize) throws DAOException
	{
		this.batchSize = batchSize;
	}


}
