package edu.wustl.dao.query.generator;

import java.util.Collection;

import edu.wustl.dao.sqlformatter.ColumnValueBean;

/**
 * @author kalpana_thakur
 *
 */
public interface QueryGenerator
{
	/**
	 *@param columnValueBean :
	 *@return SQLFormatter :
	 */
	QueryGenerator addColValBean(ColumnValueBean columnValueBean);

	/**
	 *@return collection :
	 */
	Collection<ColumnValueBean> getColValBeans();

	/**
	 * @return :
	 */
	String getInsertQuery();

	/**
	 * @return :
	 */
	String getUpdateQuery();

	/**
	 * @return tableName :
	 */
	String getTableName();

}
