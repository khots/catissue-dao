/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/catissue-dao/LICENSE.txt for details.
 */

/*
 * This will hold the implementation specific to MySQL.
 */

package edu.wustl.dao;

import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;


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
	 * @param tableName :
	 * @throws DAOException :
	 */
	public void deleteTable(String tableName) throws DAOException
	{
		StringBuffer query;
		query = new StringBuffer("DROP TABLE IF EXISTS ").append(tableName.toString());
		executeUpdate(query.toString());

	}


	/**
	 * TODO Later
	 *This method will be called to format the SQL.
	 *@param tableName :
	 *@throws DAOException :Generic DAOException.
	 *@return SQLFormatter :
	 *//*
	public SQLFormatter getSQLFormatter(String tableName) throws DAOException
	{
		return new SQLFormatterMySQL(tableName);
	}


	*//**
	 * TODO Later.
	 * @param query :
	 * @param clobContent :
	 *//*
	public void updateClob(String query, String clobContent)
	{}


	*//**
	 * TODO : Later.
	 *@param sqlFormatter :
	 *@param sequenceName :
	 *@param columnName :
	 *@param columnTpe :
	 * @throws DAOException :
	 *//*
	public void insert(SQLFormatter sqlFormatter,String sequenceName,String columnName,
			int columnTpe) throws DAOException
	{
		executeUpdate(sqlFormatter.getInsertQuery());
		StringBuffer sqlBuff = new StringBuffer(DAOConstants.TAILING_SPACES);
		sqlBuff.append("select max(").append(columnName).append(") from").
		append(DAOConstants.TAILING_SPACES).append(sqlFormatter.getTableName());

		Object sequenceNo = getSequenceNumber(sqlBuff.toString());
		sqlFormatter.addColValBean(new ColumnValueBean(columnName,sequenceNo,columnTpe));
	}

*/
}
