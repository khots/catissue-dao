
package edu.wustl.dao;

import edu.wustl.common.dao.queryExecutor.PagenatedResultData;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.QueryParams;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.DAOConstants;
import edu.wustl.dao.util.DatabaseConnectionParams;

/**
 * @author kalpana_thakur
 *
 */
public class DB2DAOImpl extends AbstractJDBCDAOImpl
{
	/**
	 * Class Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(AbstractJDBCDAOImpl.class);

	/**
	 * Deletes the specified table.
	 * @param tableName : Table name
	 * @throws DAOException : DAOException
	 */
	public void delete(String tableName) throws DAOException
	{
		StringBuffer query = new StringBuffer(DAOConstants.TAILING_SPACES);
		query.append("select 1 from SYSCAT.TABLES where upper(tabname)=upper('")
		.append(tableName).append("')");

		try
		{
			DatabaseConnectionParams databaseConnectionParams =
				new DatabaseConnectionParams();
			databaseConnectionParams.setConnection(getConnection());

			boolean isTableExists =databaseConnectionParams.isResultSetExists(query.toString());
			logger.info("DB2****" + query.toString() + isTableExists);
			if (isTableExists)
			{
				logger.debug("Drop Table");
				databaseConnectionParams.executeUpdate("DROP TABLE " + tableName);
			}

		}
		catch (Exception exp)
		{
			logger.error(exp.getMessage(), exp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"AbstractJDBCDAOImpl.java"+
					DAOConstants.OPEN_SESSION_ERROR);
		}
	}

	/**
	 * This method executed query, parses the result and returns List of rows after doing security checks
	 * for user's right to view a record/field.
	 * @param queryParams : TODO
	 * @return This will return the PagenatedResultData.
	 * @throws DAOException :DAOException
	 */

	@Override
	public PagenatedResultData getQueryResultList(QueryParams queryParams)
			throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param excp : Exception Object.
	 * @param args : TODO
	 * @return : It will return the formated messages.
	 */
	public String formatMessage(Exception excp, Object[] args)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return : This method will return Date Format function.
	 */
	public String getDateFormatFunction()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return : This method will return Date Pattern.
	 */
	public String getDatePattern()
	{
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * @return : This method will return Date to String function.
	 */
	public String getDateTostrFunction()
	{
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * @return : This method will return String to Date function.
	 */
	public String getStrTodateFunction()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return : This method will return Time format function.
	 */
	public String getTimeFormatFunction()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return : This method will return Time Pattern.
	 */
	public String getTimePattern()
	{
		// TODO Auto-generated method stub
		return null;
	}


}
