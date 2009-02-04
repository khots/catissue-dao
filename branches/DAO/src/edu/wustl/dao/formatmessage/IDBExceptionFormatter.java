/*
 * TODO
 */
package edu.wustl.dao.formatmessage;

import edu.wustl.dao.JDBCDAO;

/**
 * @author kalpana_thakur
 *
 */
public interface IDBExceptionFormatter
{
	/**
	 * @param objExcp :Exception.
	 * @param jdbcDAO : jdbcDAO.
 	 * @return It will return the formatted messages.
	 */
	String getFormatedMessage(Exception objExcp,JDBCDAO jdbcDAO);
}
