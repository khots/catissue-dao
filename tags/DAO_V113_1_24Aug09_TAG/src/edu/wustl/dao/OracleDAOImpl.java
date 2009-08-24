/*
 * This will hold the implementation specific to Oracle.
 */

package edu.wustl.dao;

import java.sql.ResultSet;

import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;
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
		ResultSet rs = null;
		try
		{

			StringBuffer query = new StringBuffer("select tname from tab where tname='"
					+ tableName.toString() + "'");

			rs = getQueryResultSet(query.toString());

			boolean isTableExists = DAOUtility.getInstance().
			isResultSetExists(rs);

			logger.debug("ORACLE :" + query.toString() + isTableExists);

			if (isTableExists)
			{

				logger.debug("Drop Table");
				executeUpdate("DROP TABLE " +
						tableName.toString() + " cascade constraints");
			}

		}
		finally
		{
			closeStatement(rs);
		}
	}

}
