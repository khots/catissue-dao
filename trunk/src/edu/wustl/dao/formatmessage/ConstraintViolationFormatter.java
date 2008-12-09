/*
 * TODO
 */
package edu.wustl.dao.formatmessage;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.exceptionformatter.ExceptionFormatter;
import edu.wustl.dao.DAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.HibernateMetaData;

/**
 * @author kalpana_thakur
 *
 */
public class ConstraintViolationFormatter implements ExceptionFormatter
{

	/**
	 * @param objExcp :
	 * @param applicationName :
	 * This will be called to format exception messages.
	 * @throws DAOException :
	 * @return string :formated message.
	 */
	public String formatMessage(Exception objExcp ,String applicationName) throws DAOException
	{
		String formatedMessage ;
		try
		{
			IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(applicationName);
			DAO dao = daoFactory.getDAO();
			HibernateMetaData.initHibernateMetaData(dao.getConnectionManager().getConfiguration());
			formatedMessage = dao.formatMessage(objExcp,applicationName);
		}
		catch(Exception exp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"ConstraintViolationFormatter.java :");
		}
		return formatedMessage;
	}

	/**
	 * @param objExcp :
	 * @param args :
	 * This will be called to format exception messages.
	 * @return string :formated message.
	 */
	public String formatMessage(Exception objExcp, Object[] args)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
