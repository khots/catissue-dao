package edu.wustl.dao.condition;

import edu.wustl.dao.util.DAOConstants;
import edu.wustl.dao.util.DAOUtility;

/**
 * @author kalpana_thakur
 *
 */
public class EqualClause implements Condition
{

	/**
	 * Name of the where Column.
	 */
	private String columnName;


	/**
	 * Value of the where column.
	 */
	private Object colValue;

	/**
	 * Name of the class or table.
	 */
	private String sourceObjectName;


	/**
	 * @param columnName :
	 * @param sourceObjectName :
	 * @param object :
	 */
	public EqualClause (String columnName ,Object object,String sourceObjectName)
	{
		this.columnName = columnName;
		this.colValue = object;
		this.sourceObjectName = sourceObjectName;
	}

	/**
	 * @param columnName :
	 * @param object :
	 */
	public EqualClause (String columnName ,Object object)
	{
		this.columnName = columnName;
		this.colValue = object;
	}

	/**
	 * This method will be called to build EqualClause.
	 * @return Query string.
	 */
	public String buildSql()
	{
		StringBuffer strBuff = new StringBuffer(DAOConstants.TAILING_SPACES);

		String sourceObject = DAOUtility.getInstance().parseClassName(sourceObjectName);

		strBuff.append(sourceObject).append(DAOConstants.DOT_OPERATOR).
		append(columnName).append(DAOConstants.TAILING_SPACES).append(DAOConstants.EQUAL_OPERATOR).
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
