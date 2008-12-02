
package edu.wustl.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import edu.wustl.common.dao.queryExecutor.PagenatedResultData;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.QueryParams;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.DAOConstants;

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
		StringBuffer query = new StringBuffer("select 1 from SYSCAT.TABLES where upper(tabname)=" + "upper('"
				+ tableName + "')");
		try
		{
			Connection connection = getConnection();
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(query.toString());
			boolean isTableExists = rs.next();
			logger.info("DB2****" + query.toString() + isTableExists);
			if (isTableExists)
			{
				logger.debug("Drop Table");
				executeUpdate("DROP TABLE " + tableName);
			}
			rs.close();
			statement.close();
		}
		catch (SQLException sqlExp)
		{
			logger.error(sqlExp.getMessage(), sqlExp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,sqlExp,"AbstractJDBCDAOImpl.java"+
					DAOConstants.OPEN_SESSION_ERROR);
		}
	}

	@Override
	public PagenatedResultData getQueryResultList(QueryParams queryParams) throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String formatMessage(Exception excp, Object[] args)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDateFormatFunction()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDatePattern()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDateTostrFunction()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStrTodateFunction()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTimeFormatFunction()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTimePattern()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insert(String tableName, List<Object> columnValues) throws DAOException,
			SQLException
	{
		// TODO Auto-generated method stub
		
	}
}
