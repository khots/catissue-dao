/*
 * This will hold the implementation specific to Oracle.
 */

package edu.wustl.dao;

import java.util.List;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.DAOConstants;
import edu.wustl.dao.util.DAOUtility;


/**
 * @author kalpana_thakur
 *
 */
public class OracleDAOImpl extends AbstractJDBCDAOImpl
{

	/**
	 * Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(OracleDAOImpl.class);

	/**
	 * Deletes the specified table.
	 * @param tableName : Name of table to be deleted.
	 * @throws DAOException : DAOException.
	 */
	public void deleteTable(String tableName) throws DAOException
	{
		try
		{

			StringBuffer query = new StringBuffer("select tname from tab where tname='"
					+ tableName.toString() + "'");
			boolean isTableExists = DAOUtility.isResultSetExists(getQueryResultSet(query.toString()));

			logger.debug("ORACLE :" + query.toString() + isTableExists);

			if (isTableExists)
			{

				logger.debug("Drop Table");
				executeUpdate("DROP TABLE " +
						tableName.toString() + " cascade constraints");
			}

		}
		catch(Exception sqlExp)
		{

			logger.error(sqlExp.getMessage(), sqlExp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,sqlExp,"OracleDAOImpl.java"+DAOConstants.DELETE_OBJ_ERROR);

		}
		finally
		{
			closeConnectionParams();
		}
	}


	/**
	 * TODO later.
	 * @param query :
	 * @param clobContent :
	 * @throws DAOException :Generic DAOException.
	 *//*
	public void updateClob(String query, String clobContent) throws DAOException
	{
		try
		{

			List list = executeQuery(query, null, false, null);

			CLOB clob = null;

			if (!list.isEmpty())
			{

				List columnList = (List) list.get(0);
				if (!columnList.isEmpty())
				{
					clob = (CLOB) columnList.get(0);
				}
			}
			//		get output stream from the CLOB object
			OutputStream os = clob.getAsciiOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);

			//	use that output stream to write character data to the Oracle data store
			osw.write(clobContent.toCharArray());
			//write data and commit
			osw.flush();
			osw.close();
			os.close();
			commit();
		}
		catch(Exception exp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"OracleDAOImpl.java :"+
					DAOConstants.EXECUTE_QUERY_ERROR);
		}

	}

	*//**
	 * TODO later.
	 *@param sqlFormatter :
	 *@param sequenceName :
	 *@param columnName :
	 *@param columnTpe :
	 * @throws DAOException :
	 *//*
	public void insert(SQLFormatter sqlFormatter,String sequenceName,String columnName,
			int columnTpe) throws DAOException
	{
		StringBuffer sqlBuff = new StringBuffer(DAOConstants.TAILING_SPACES);
		sqlBuff.append("select").append(DAOConstants.TAILING_SPACES).append(sequenceName).
		append(DAOConstants.DOT_OPERATOR).append("nextVal from dual");

		long sequenceNo = getSequenceNumber(sqlBuff.toString());
		sqlFormatter.addColValBean(new ColumnValueBean(columnName,sequenceNo,columnTpe));

		executeUpdate(sqlFormatter.getInsertQuery());


	}

	*//**
	 * TODO later.
	 *This method will be called to format the SQL.
	 *@param tableName :
	 *@throws DAOException :Generic DAOException.
	 *@return SQLFormatter :
	 *//*
	public SQLFormatter getSQLFormatter(String tableName) throws DAOException
	{
		return new SQLFormatterOracle(tableName);
	}
*/
}
