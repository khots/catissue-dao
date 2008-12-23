package edu.wustl.dao.sqlformatter;

/**
 * @author kalpana_thakur
 *
 */
public class ColumnValueBean
{
	/**
	 *
	 */
	private String columnName;

	/**
	 *
	 */
	private Object columnValue;
	/**
	 *
	 */
	private int columnType;

	/**
	 * @param columnName :
	 * @param columnValue :
	 * @param columnType :
	 */
	public ColumnValueBean(String columnName,
			Object columnValue,int columnType)
	{
		this.columnName = columnName;
		this.columnValue = columnValue;
		this.columnType = columnType;

	}

	/**
	 * @return :
	 */
	public String getColumnName()
	{
		return columnName;
	}
	/**
	 * @return :
	 */
	public Object getColumnValue()
	{
		return columnValue;
	}

	/**
	 * @return :
	 */
	public int getColumnType()
	{
		return columnType;
	}
	/**
	 * @param columnName :
	 */
	public void setColumnName(String columnName)
	{
		this.columnName = columnName;
	}
	/**
	 * @param columnValue :
	 */
	public void setColumnValue(Object columnValue)
	{
		this.columnValue = columnValue;
	}
	/**
	 * @param columnType :
	 */
	public void setColumnType(int columnType)
	{
		this.columnType = columnType;
	}

}
