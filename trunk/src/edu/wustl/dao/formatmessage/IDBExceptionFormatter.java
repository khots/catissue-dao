/*
 * TODO
 */
package edu.wustl.dao.formatmessage;

import java.sql.Connection;

/**
 * @author kalpana_thakur
 *
 */
public interface IDBExceptionFormatter
{
	/**
	 * @param objExcp :Exception.
	 * @param connection : Connection to database.
 	 * @return It will return the formatted messages.
	 */
	String getFormatedMessage(Exception objExcp,Connection connection);
}
