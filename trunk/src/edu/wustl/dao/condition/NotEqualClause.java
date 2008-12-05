package edu.wustl.dao.condition;

import edu.wustl.dao.util.DAOConstants;
import edu.wustl.dao.util.DAOUtility;

/**
 * @author kalpana_thakur
 *
 */
public class NotEqualClause implements Condition
{

	/**
	 * Name of the where Column.
	 */
	private final String columnName;


	/**
	 * Value of the where column.
	 */
	private final Object colValue;

	/**
	 * Name of the class or table.
	 */
	private String sourceObjectName;

	/**
	 * It will be called to instantiate columnName,
	 * columnValue and sourceObject Name.
	 * @param columnName :
	 * @param sourceObjectName :
	 * @param object :
	 */
	public NotEqualClause (String columnName ,Object object,String sourceObjectName)
	{

		this.columnName = columnName;
		this.colValue = object;
		this.sourceObjectName = sourceObjectName;

	}
	/**
	 * It will be called to instantiate columnName,
	 * columnValue.
	 * @param columnName :
	 * @param object :
	 */
	public NotEqualClause (String columnName ,Object object)
	{

		this.columnName = columnName;
		this.colValue = object;

	}


	/**
	 * This method will be called to build NotEqualClause.
	 * @return Query string.
	 */
	public String buildSql()
	{

		StringBuffer strBuff = new StringBuffer(DAOConstants.TAILING_SPACES);
		String sourceObject = DAOUtility.getInstance().parseClassName(sourceObjectName);

		strBuff.append(sourceObject).append(DAOConstants.DOT_OPERATOR).
		append(columnName).append(DAOConstants.TAILING_SPACES).append(DAOConstants.NOT_EQUAL_OPERATOR).
		append(DAOConstants.TAILING_SPACES);

		if(colValue instanceof String)
		{
			strBuff.append("'"+colValue+"'");
		}
		else
		{
			strBuff.append(colValue);
		}
		strBuff.append(DAOConstants.TAILING_SPACES);

		return strBuff.toString();
	}

	/**
	 * @return class name or table name.
	 */
	public String getSourceObjectName()
	{
		return sourceObjectName;
	}

	/**
	 * @param sourceObjectName set the class name or table name.
	 */
	public void setSourceObjectName(String sourceObjectName)
	{
		this.sourceObjectName = sourceObjectName;
	}





}
