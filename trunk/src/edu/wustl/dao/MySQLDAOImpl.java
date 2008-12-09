/*
 * TODO
 */
package edu.wustl.dao;

import java.sql.Connection;

import edu.wustl.common.dao.queryExecutor.PagenatedResultData;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.QueryParams;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.formatmessage.MysqlFormatter;
import edu.wustl.dao.util.DAOConstants;
import edu.wustl.query.executor.MysqlQueryExecutor;


/**
 * @author kalpana_thakur
 *
 */
public class MySQLDAOImpl extends AbstractJDBCDAOImpl
{

	/**
	 * Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(MySQLDAOImpl.class);

	/**
	 * Deletes the specified table.
	 * @param tableName : Table name
	 * @throws DAOException : DAOException
	 */
	public void delete(String tableName) throws DAOException
	{
		StringBuffer query;
		query = new StringBuffer("DROP TABLE IF EXISTS ").append(tableName);
			executeUpdate(query.toString());
	}

	/**
	 * @return :This will return the Date Pattern.
	 */
	public String getDatePattern()
	{
		return "%m-%d-%Y";
	}

	/**
	 * @return :This will return the Time Pattern.
	 */
	public String getTimePattern()
	{
		return "%H:%i:%s";
	}
	/**
	 * @return :This will return the Date Format Function.
	 */
	public String getDateFormatFunction()
	{
		return "DATE_FORMAT";
	}
	/**
	 * @return :This will return the Time Format Function.
	 */
	public String getTimeFormatFunction()
	{
		return "TIME_FORMAT";
	}

	/**
	 * @return :This will return the Date to string function
	 */
	public String getDateTostrFunction()
	{
		return "TO_CHAR";
	}
	/**
	 * @return :This will return the string to Date function
	 */
	public String getStrTodateFunction()
	{

		return "STR_TO_DATE";
	}


	/* (non-Javadoc)
	 * @see edu.wustl.common.dao.JDBCDAO#insert(java.lang.String, java.util.List)

	*//**
	 * @param tableName : Name of the table.
	 * @param columnValues : Column values of table.
	 * @throws SQLException : SQLException
	 * @throws DAOException : DAOException
	 *//*
	public void insertHashedValues(String tableName, List<Object> columnValues)
	throws DAOException, SQLException
	{
		insertHashedValues(tableName, columnValues, null);
	}*/


	/**
	 * @param excp : Exception Object.
	 * @param connection :
	 * @return : It will return the formated messages.
	 */
	public String formatMessage(Exception excp,Connection connection)
	{
		MysqlFormatter mysqlFormatter = new MysqlFormatter();
		return mysqlFormatter.getFormatedMessage(excp,connection);
	}

	/**
	 * This method executed query, parses the result and returns List of rows after doing security checks
	 * for user's right to view a record/field.
	 * @param queryParams : TODO
	 * @return This will return the PagenatedResultData.
	 * @throws DAOException :DAOException
	 */
	public PagenatedResultData getQueryResultList(QueryParams queryParams) throws DAOException
	{
		PagenatedResultData pagenatedResultData = null;
		try
		{
			queryParams.setConnection(getConnectionManager().getConnection());
			MysqlQueryExecutor mysqlQueryExecutor = new MysqlQueryExecutor();
			pagenatedResultData = mysqlQueryExecutor.getQueryResultList(queryParams);

		}
		catch(Exception exp)
		{
			logger.fatal(exp.getMessage(), exp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"MySQLDAOImpl.java :"+
					DAOConstants.EXECUTE_QUERY_ERROR);
		}
		return pagenatedResultData;
	}



}
