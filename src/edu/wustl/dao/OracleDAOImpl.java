/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/catissue-dao/LICENSE.txt for details.
 */

/*
 * This will hold the implementation specific to Oracle.
 */

package edu.wustl.dao;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;

import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
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
		ResultSet resultSet = null;
		try
		{

			StringBuffer query = new StringBuffer("select tname from tab where tname='"
					+ tableName + "'");

			resultSet = getQueryResultSet(query.toString());

			boolean isTableExists = DAOUtility.getInstance().
			isResultSetExists(resultSet);

			logger.debug("ORACLE :" + query.toString() + isTableExists);

			if (isTableExists)
			{

				logger.debug("Drop Table");
				executeUpdate("DROP TABLE " +
						tableName + " cascade constraints");
			}

		}
		finally
		{
			closeStatement(resultSet);
		}
	}

	public Iterator executeParamHQLIterator(String query,
			List<ColumnValueBean> columnValueBeans) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

}
