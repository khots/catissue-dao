package edu.wustl.dao.sqlformatter;


/**
 * @author kalpana_thakur
 *
 */
public class SQLFormatterOracle extends AbstractSQLFormatter
{


	/**
	 * @param tableName :
	 */
	public SQLFormatterOracle(String tableName)
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
			case Types.DATE :
				appendDateValue(colValBean.getColumnValue(), valuePart);
				break;

			case Types.TIMESTAMP :
				break;

			case Types.NUMBER :
			case Types.INTEGER :
				appendNumericValue(colValBean.getColumnValue(),valuePart);
				break ;

			default :
				appendStringValue(colValBean.getColumnValue(),valuePart);
				break;

		}

	}

	/**

	 * @param colValue :
	 * @param valuePart :
	 */
	private void appendDateValue(Object colValue,StringBuffer valuePart)
	{
		valuePart.append("to_date ( '").append(colValue).append("','yyyy-mm-dd')");
	}

}
