package edu.wustl.dao.sqlformatter;

/**
 * @author kalpana_thakur
 *
 */
public interface SQLFormatter
{
	/**
	 *@param columnValueBean :
	 */
	void addColValBean(ColumnValueBean columnValueBean);

	/**
	 * @param tableName :
	 * @return :
	 */
	String insertQuery(String tableName);

	/**
	 * @param tableName :
	 * @return :
	 */
	String updateQuery(String tableName);

}
