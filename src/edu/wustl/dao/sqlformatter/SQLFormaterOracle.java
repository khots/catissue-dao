package edu.wustl.dao.sqlformatter;


/**
 * @author kalpana_thakur
 *
 */
public class SQLFormaterOracle extends AbstractSQLFormatter
{


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

			default :
				appendColumnValue(colValBean.getColumnValue(),valuePart);
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


	/**

	 * @param colValue :
	 * @param valuePart :
	 */
	private void appendColumnValue(Object colValue,StringBuffer valuePart)
	{
		valuePart.append("' ").append(colValue).append("' ");

	}

}
