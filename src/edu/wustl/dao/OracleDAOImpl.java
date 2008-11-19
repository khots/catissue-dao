package edu.wustl.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import edu.wustl.common.dao.queryExecutor.PagenatedResultData;
import edu.wustl.common.exceptionformatter.ConstraintViolationFormatter;
import edu.wustl.common.util.QueryParams;
import edu.wustl.common.util.dbmanager.DAOException;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.util.DAOConstants;
import edu.wustl.dao.util.DatabaseConnectionParams;
import edu.wustl.query.executor.OracleQueryExecutor;


public class OracleDAOImpl extends AbstractJDBCDAOImpl
{

	private static org.apache.log4j.Logger logger = Logger.getLogger(OracleDAOImpl.class);

	/**
	 * Deletes the specified table
	 * @param tableName
	 * @throws DAOException
	 */
	public void delete(String tableName) throws DAOException
	{
		DatabaseConnectionParams databaseConnectionParams = new DatabaseConnectionParams();
		ResultSet resultSet = null;
		try
		{
			
			databaseConnectionParams.setConnection(getConnection());
						
			StringBuffer query = new StringBuffer("select tname from tab where tname='" + tableName + "'");
			resultSet = databaseConnectionParams.getResultSet(query.toString());
			boolean isTableExists = resultSet.next();
			
			logger.debug("ORACLE :" + query.toString() + isTableExists);
			
			if (isTableExists)
			{
				
				logger.debug("Drop Table");
				databaseConnectionParams.executeUpdate("DROP TABLE " + tableName + " cascade constraints");
			}
			
		} 
		catch (Exception sqlExp)
		{
			
			logger.error(sqlExp.getMessage(), sqlExp);
			throw new DAOException(Constants.GENERIC_DATABASE_ERROR, sqlExp);
			
		}
		finally
		{
			try
			{
				if (resultSet != null)
				{
					resultSet.close();
				}
				databaseConnectionParams.closeConnectionParams();
				
			}
			catch(SQLException sqlExp)
			{
				logger.fatal(DAOConstants.CONNECTIONS_CLOSING_ISSUE, sqlExp);
			}
		}
		
	}
	
		
		
	public String getDatePattern()
	{
		
		return "mm-dd-yyyy";
	}
	
	public String getTimePattern()
	{
		
		return "hh-mi-ss";
	}
	public String getDateFormatFunction()
	{
		
		return "TO_CHAR";
	}
	public String getTimeFormatFunction()
	{
		return "TO_CHAR";
	}
	
	public String getDateTostrFunction()
	{
		
		return "TO_CHAR";
	}
	
	public String getStrTodateFunction()
	{
		
		return "TO_DATE";
	}
	
	/** 
	 * This method is called to insert data.
	 * @see edu.wustl.dao.JDBCDAO#insert(java.lang.String, java.util.List)
	 */
	public void insert(String tableName, List<Object> columnValues) throws DAOException, SQLException
	{
		insert(tableName, columnValues, null);
	}
	
	public String formatMessage(Exception excp, Object[] args)
	{
		
		String formattedErrMsg; // Formatted Error Message return by this method
		Exception objExcp = excp;
		
		try
		{
			if (excp instanceof gov.nih.nci.security.exceptions.CSTransactionException)
			{
				objExcp = (Exception) objExcp.getCause();
				logger.debug(objExcp);
			}
			formattedErrMsg = ConstraintViolationFormatter.getFormatedErrorMessageForOracle(args,objExcp);
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
			formattedErrMsg = Constants.GENERIC_DATABASE_ERROR;
		}
		return formattedErrMsg;
	}
	
	public PagenatedResultData getQueryResultList(QueryParams queryParams) throws DAOException
	{
		PagenatedResultData pagenatedResultData = null;
				
		queryParams.setConnection(getConnectionManager().getConnection());
		OracleQueryExecutor oracleQueryExecutor = new OracleQueryExecutor();
		pagenatedResultData = oracleQueryExecutor.getQueryResultList(queryParams);
		
		return pagenatedResultData;

	}

	
}
