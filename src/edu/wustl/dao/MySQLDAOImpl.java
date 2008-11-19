package edu.wustl.dao;

import java.sql.SQLException;
import java.util.List;

import edu.wustl.common.dao.queryExecutor.PagenatedResultData;
import edu.wustl.common.exceptionformatter.ConstraintViolationFormatter;
import edu.wustl.common.util.QueryParams;
import edu.wustl.common.util.dbmanager.DAOException;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.query.executor.MysqlQueryExecutor;


public class MySQLDAOImpl extends AbstractJDBCDAOImpl
{
	
	private static org.apache.log4j.Logger logger = Logger.getLogger(MySQLDAOImpl.class);
		
	/**
	 * Deletes the specified table
	 * @param tableName
	 * @throws DAOException
	 */
	public void delete(String tableName) throws DAOException
	{
		StringBuffer query;
		query = new StringBuffer("DROP TABLE IF EXISTS ").append(tableName);
			executeUpdate(query.toString());
	}
		
	public String getDatePattern()
	{
		return "%m-%d-%Y";
	}
	
	public String getTimePattern()
	{
		return "%H:%i:%s";
	}
	public String getDateFormatFunction()
	{
		return "DATE_FORMAT";
	}
	public String getTimeFormatFunction()
	{
		return "TIME_FORMAT";
	}
	
	public String getDateTostrFunction()
	{
		return "TO_CHAR";
	}
	
	public String getStrTodateFunction()
	{
		
		return "STR_TO_DATE";
	}
	
	
	/* (non-Javadoc)
	 * @see edu.wustl.common.dao.JDBCDAO#insert(java.lang.String, java.util.List)
	 */
	public void insert(String tableName, List<Object> columnValues) throws DAOException, SQLException
	{
		insert(tableName, columnValues, null);
	}
	
	
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
			formattedErrMsg = (String) ConstraintViolationFormatter.getFormattedErrorMessage(args, objExcp, tableName);
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
	 * for user's right to view a record/field
	 * @param query
	 * @param sessionDataBean
	 * @param isSecureExecute
	 * @param hasConditionOnIdentifiedField
	 * @param queryResultObjectDataMap
	 * @param startIndex The offset value, from which the result will be returned. 
	 * 		This will be used for pagination purpose, 
	 * @param noOfRecords
	 * @return
	 * @throws DAOException
	 * 
	 * 
	 * -- TODO have to look into this 
	 */
	public PagenatedResultData getQueryResultList(QueryParams queryParams) throws DAOException
	{
		PagenatedResultData pagenatedResultData = null;
				
		queryParams.setConnection(getConnectionManager().getConnection());
		MysqlQueryExecutor mysqlQueryExecutor = new MysqlQueryExecutor();
		pagenatedResultData = mysqlQueryExecutor.getQueryResultList(queryParams);
		
		return pagenatedResultData;

	}
	
	
		
}
