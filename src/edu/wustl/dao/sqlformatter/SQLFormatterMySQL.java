package edu.wustl.dao.sqlformatter;



/**
 * @author kalpana_thakur
 *
 */
public class SQLFormatterMySQL extends AbstractSQLFormatter
{


	/**
	 * @param tableName :
	 */
	public SQLFormatterMySQL(String tableName)
	{
		super(tableName);
	}

	/**
	 * @param colValBean :
	 * @param valuePart :
	 */
	protected void appendColumnValue(ColumnValueBean colValBean,StringBuffer valuePart)
	{

		switch(colValBean.getColumnType())
		{
			case Types.BIGINT :
			case Types.BIT :
			case Types.FLOAT :
			case Types.TINYINT :
				appendNumericValue(colValBean.getColumnValue(),valuePart);
			break;

			default :
				appendStringValue(colValBean.getColumnValue(),valuePart);
			break;

		}

	}


}