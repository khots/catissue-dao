/*
 *
 */
package edu.wustl.dao.exception;

import org.hibernate.HibernateException;

import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.ErrorKey;


/**
 * @author kalpana_thakur
 *
 */
public class DAOException extends ApplicationException
{

	/**
	 * Default serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Error log.
	 */
	private String errorlog ;

	/**
	 * It will called on occurrence of database related exception.
	 * @param errorKey : key assigned to the error
	 * @param throwable :
	 * @param msgValues : message displayed when error occurred
	 */
	public DAOException(final ErrorKey errorKey, final Throwable throwable, final String msgValues)
	{
		super(errorKey, throwable, msgValues);
	}

	/**
	 * It will invoked on occurrence of hibernate exception.
	 * @param errorKey : key assigned to the error
	 * @param hibernateException :
	 * @param msgValues : message displayed when error occurred
	 */
	public DAOException(final ErrorKey errorKey,HibernateException hibernateException,
			final String msgValues)
	{
		super(errorKey, hibernateException, msgValues);
		errorlog = generateErrorMessage(hibernateException);
	}

	/**
	 * This function formats a complete message with all details about error which caused the
	 * exception. This function intended to use for logging error message.
	 *
	 * Usage: logger.error(ex.getLogMessage,ex);
	 *
	 * @return formatted detailed error message.
	 */
	public String getLogMessage()
	{
		StringBuffer logMsg = new StringBuffer(super.getLogMessage());
		logMsg.append(errorlog);
		return logMsg.toString();
	}


	/**
	 * Generates Error Message.
	 * @param hibernateException Exception
	 * @return message.
	 */
	private String generateErrorMessage(HibernateException hibernateException)
	{
		StringBuffer message = new StringBuffer();
		String [] str = hibernateException.getMessages();

		for (int i = 0; i < str.length; i++)
		{
			message.append(str[i]).append("   ");
		}
		return message.toString();
	}
}
