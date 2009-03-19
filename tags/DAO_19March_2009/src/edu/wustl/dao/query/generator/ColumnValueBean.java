package edu.wustl.dao.query.generator;

/**
 * @author kalpana_thakur
 * This class creates the bean having column name and column value.
 */
public class ColumnValueBean
{
	/**
	 * Name of the Column.
	 */
	private String columnName;

	/**
	 * Value of the Column.
	 */
	private Object columnValue;
	/**
	 * Type of column.
	 */
	private int columnType;

	/**
	 * @param columnName : column name
	 * @param columnValue : column value
	 * @param columnType : column type
	 */
	public ColumnValueBean(String columnName,
			Object columnValue,int columnType)
	{
		this.columnName = columnName;
		this.columnValue = columnValue;
		this.columnType = columnType;

	}

	/**
	 * @return : the column name
	 */
	public String getColumnName()
	{
		return columnName;
	}
	/**
	 * @return : the column value.
	 */
	public Object getColumnValue()
	{
		return columnValue;
	}

	/**
	 * @return : the column type.
	 */
	public int getColumnType()
	{
		return columnType;
	}
	/**
	 * @param columnName : set the column name.
	 */
	public void setColumnName(String columnName)
	{
		this.columnName = columnName;
	}
	/**
	 * @param columnValue : set the column value.
	 */
	public void setColumnValue(Object columnValue)
	{
		this.columnValue = columnValue;
	}
	/**
	 * @param columnType : set the column type.
	 */
	public void setColumnType(int columnType)
	{
		this.columnType = columnType;
	}

}
