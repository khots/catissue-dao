package edu.wustl.dao.sqlformatter;

import java.util.Collection;

/**
 * @author kalpana_thakur
 *
 */
public interface SQLFormatter
{
	/**
	 *@param columnValueBean :
	 *@return SQLFormatter :
	 */
	SQLFormatter addColValBean(ColumnValueBean columnValueBean);

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
	 * @param tableName :
	 */
	void setTableName(String tableName);

}
