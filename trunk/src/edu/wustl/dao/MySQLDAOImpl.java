/*
 * TODO
 */
package edu.wustl.dao;

import java.sql.Connection;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.PagenatedResultData;
import edu.wustl.common.util.QueryParams;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.formatmessage.MysqlFormatter;
import edu.wustl.dao.sqlformatter.SQLFormatter;
import edu.wustl.dao.sqlformatter.SQLFormatterMySQL;
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
	/*public void delete(String tableName) throws DAOException
	{
		StringBuffer query;
		query = new StringBuffer("DROP TABLE IF EXISTS ").append(tableName);
		executeUpdate(query.toString());
	}
*/
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

	/**
	 * @param columnCount count of the columns in results
	 * @param getSublistOfResult boolean for getting sublist
	 * @return column count
	 */
	public int getColumnCount(int columnCount,boolean getSublistOfResult)
	{
		return 0;
	}

	/**
	 * Gets sql for Like operator.
	 * @param attributeName name of the attribute
	 * @param value value
	 * @return String sql
	 */
	public String getSQLForLikeOperator(String attributeName, String value)
	{
		return DAOConstants.TAILING_SPACES;
	}

	/**
	 * Required for temporal query.
	 * @return Object of type either Database specific Primitive operation processor.
	 */
	public Object getPrimitiveOperationProcessor()
	{
		return DAOConstants.TAILING_SPACES;
	}

	/**
	 * This method fires a query to insert auditing details into audit tables.
	 * @param sql SQL to be fired
	 * @param sessionData session data to get userId and ip address
	 * @param comments comments to be inserted in the table
	 * @throws DAOException DaoException
	 */
	public void executeAuditSql(String sql, SessionDataBean sessionData,
			String comments) throws DAOException
	{

	}

	/**
	 *This method will be called to format the SQL.
	 *@param tableName :
	 *@throws DAOException :Generic DAOException.
	 *@return SQLFormatter :
	 */
	public SQLFormatter getSQLFormatter(String tableName) throws DAOException
	{

		return new SQLFormatterMySQL(tableName);
	}


	/**
	 * @param query :
	 * @param clobContent :
	 */
	public void updateClob(String query, String clobContent)
	{}


	/**
	 *@param sqlFormatter :
	 *@param sequenceName :
	 *@param columnName :
	 *@param columnTpe :
	 * @throws DAOException :
	 */
	public void insert(SQLFormatter sqlFormatter,String sequenceName,String columnName,
			int columnTpe) throws DAOException
	{/*
		executeUpdate(sqlFormatter.getInsertQuery());
		StringBuffer sqlBuff = new StringBuffer(DAOConstants.TAILING_SPACES);
		sqlBuff.append("select max(").append(columnName).append(") from").
		append(DAOConstants.TAILING_SPACES).append(sqlFormatter.getTableName());

		Object sequenceNo = getSequenceNumber(sqlBuff.toString());
		sqlFormatter.addColValBean(new ColumnValueBean(columnName,sequenceNo,columnTpe));
	*/}

	/**
	 * @return :
	 */
	public String getMaxBarcodeCol()
	{
		return DAOConstants.TAILING_SPACES;
	}

	/**
	 * @return :
	 */
	public String getMaxLabelCol()
	{
		return DAOConstants.TAILING_SPACES;
	}

	/**
	 * @param tableName :
	 * @throws DAOException :
	 */
	public void delete(Object tableName) throws DAOException
	{
		StringBuffer query;
		query = new StringBuffer("DROP TABLE IF EXISTS ").append(tableName.toString());
		executeUpdate(query.toString());

	}

}
