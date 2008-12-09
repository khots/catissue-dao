/*
 * TODO
 */
package edu.wustl.dao.condition;

import edu.wustl.dao.util.DAOConstants;
import edu.wustl.dao.util.DAOUtility;

/**
 * @author kalpana_thakur
 *
 */
public class NotNullClause implements Condition
{

	/**
	 * Name of the where Column.
	 */
	private final String columnName;


	/**
	 * Name of the class or table.
	 */
	private String sourceObjectName;
	/**
	 * @param columnName :
	 * @param sourceObjectName :
	 */
	public NotNullClause (String columnName ,String sourceObjectName)
	{
		this.columnName = columnName;
		this.sourceObjectName = sourceObjectName;
	}

	/**
	 * @param columnName :
	 */
	public NotNullClause (String columnName)
	{
		this.columnName = columnName;
	}

	/**
	 * This method will generate the not null clause of Query.
	 * @return String:
	 */
	public String buildSql()
	{
		StringBuffer strBuff = new StringBuffer(DAOConstants.TAILING_SPACES);

		String sourceObject = DAOUtility.getInstance().parseClassName(sourceObjectName);

		strBuff.append(sourceObject).append(DAOConstants.DOT_OPERATOR).
		append(columnName).append(DAOConstants.TAILING_SPACES).append(DAOConstants.NOT_NULL_CONDITION).
		append(DAOConstants.TAILING_SPACES);
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
