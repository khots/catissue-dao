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
public class INClause implements Condition
{

	/**
	 * Name of the where Column.
	 */
	private final String columnName;


	/**
	 * Value of the where column.
	 */
	private final Object[] colValueArray;

	/**
	 * Name of the class or table.
	 */
	private String sourceObjectName;

	/**
	 * @param columnName :
	 * @param sourceObjectName :
	 * @param values :
	 */
	public INClause(String columnName, String values ,String sourceObjectName )
	{
		this.columnName = columnName;
		this.sourceObjectName = sourceObjectName;
		this.colValueArray = values.split(DAOConstants.SPLIT_OPERATOR);
	}

	/**
	 * @param columnName :
	 * @param values :
	 */
	public INClause(String columnName, String values)
	{
		this.columnName = columnName;
		this.colValueArray = values.split(DAOConstants.SPLIT_OPERATOR);


	}

	/**
	 * @param columnName :
	 * @param sourceObjectName :
	 * @param object :
	 */
	public INClause(String columnName,
			Object[] object,String sourceObjectName)
	{

		this.columnName = columnName;
		this.sourceObjectName = sourceObjectName;
		this.colValueArray = object;
	}

	/**
	 * @param columnName :
	 * @param object :
	 */
	public INClause(String columnName,
			Object[] object)
	{

		this.columnName = columnName;
		this.colValueArray = object;
	}



	/**
	 * This method will generate the in clause of Query.
	 * @return String:
	 */
	public String buildSql()
	{
		StringBuffer strBuff = new StringBuffer(DAOConstants.TAILING_SPACES);

		strBuff = new StringBuffer(DAOConstants.TAILING_SPACES);

		String sourceObject = DAOUtility.getInstance().parseClassName(sourceObjectName);

		strBuff.append(sourceObject).append(DAOConstants.DOT_OPERATOR).append(columnName).
		append(DAOConstants.TAILING_SPACES).append(DAOConstants.IN_CONDITION).
		append(DAOConstants.TAILING_SPACES);

		updateInclause(strBuff);

		return strBuff.toString();
	}

	/**
	 * This is called to append all the column values to in clause.
	 * @param strBuff :
	 */
	private void updateInclause(StringBuffer strBuff)
	{

		strBuff.append("(  ");
		for (int j = 0; j < colValueArray.length; j++)
		{

			if(colValueArray[j] instanceof String)
			{
				strBuff.append("'"+colValueArray[j]+"'");
			}
			else
			{
				strBuff.append(colValueArray[j]);
			}

			if((j+1) < colValueArray.length)
			{
				strBuff.append(", ");
			}
		}
		strBuff.append(") ");
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
