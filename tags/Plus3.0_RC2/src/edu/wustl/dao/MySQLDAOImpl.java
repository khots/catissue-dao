/*
 * This will hold the implementation specific to MySQL.
 */

package edu.wustl.dao;

import java.util.Iterator;
import java.util.List;

import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;


/**
 * @author kalpana_thakur
 *
 */
public class MySQLDAOImpl extends AbstractJDBCDAOImpl
{

	/**
	 * @param tableName :
	 * @throws DAOException :
	 */
	public void deleteTable(String tableName) throws DAOException
	{
		StringBuffer query;
		query = new StringBuffer("DROP TABLE IF EXISTS ").append(tableName);
		executeUpdate(query.toString());

	}

	public Iterator executeParamHQLIterator(String query,
			List<ColumnValueBean> columnValueBeans) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}
}
