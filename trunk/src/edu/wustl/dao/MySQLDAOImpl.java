package edu.wustl.dao;

import edu.wustl.common.dao.queryExecutor.PagenatedResultData;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.exceptionformatter.ConstraintViolationFormatter;
import edu.wustl.common.util.QueryParams;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;
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
	 * @param args : TODO
	 * @return : It will return the formated messages.
	 */
	public String formatMessage(Exception excp, Object[] args)
	{
		logger.debug(excp.getClass().getName());
		Exception objExcp = excp;
		String tableName = null; // stores Table_Name for which column name to be found
		String formattedErrMsg = null; // Formatted Error Message return by this method
		try
		{
			if (objExcp instanceof gov.nih.nci.security.exceptions.CSTransactionException)
			{
				objExcp = (Exception) objExcp.getCause();
				logger.debug(objExcp);
			}
			tableName = ConstraintViolationFormatter.getTableName(args);
			formattedErrMsg = (String) ConstraintViolationFormatter.getFormattedErrorMessage(args,
					objExcp, tableName);
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
			formattedErrMsg = Constants.GENERIC_DATABASE_ERROR;
		}
		return formattedErrMsg;

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
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"MySQLDAOImpl.java");
		}
		return pagenatedResultData;
	}


}
