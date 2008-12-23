package edu.wustl.dao.sqlformatter;


/**
 * @author kalpana_thakur
 *
 */
public class SQLFormatterMySQL extends AbstractSQLFormatter
{



	/**
	 * @param colValBean :
	 * @param valuePart :
	 */
	protected void appendColumnValue(ColumnValueBean colValBean,StringBuffer valuePart)
	{

		switch(colValBean.getColumnType())
		{
			default :
				appendColumnValue(colValBean.getColumnValue(),valuePart);
				break;

		}

	}

	/**

	 * @param colValue :
	 * @param valuePart :
	 */
	private void appendColumnValue(Object colValue,StringBuffer valuePart)
	{
		valuePart.append("' ").append(colValue).append("' ");

	}

}
