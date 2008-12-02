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
	 * strBuff.
	 */
	private final StringBuffer strBuff;
	/**
	 * @param columnName :
	 * @param sourceObjectName :
	 * @param values :
	 */
	public INClause(String columnName, String values ,String sourceObjectName )
	{
		strBuff = new StringBuffer(DAOConstants.TAILING_SPACES);

		String sourceObject = DAOUtility.getInstance().parseClassName(sourceObjectName);

		strBuff.append(sourceObject).append(DAOConstants.DOT_OPERATOR).append(columnName).
		append(DAOConstants.TAILING_SPACES).append(DAOConstants.IN_OPERATOR).
		append(DAOConstants.TAILING_SPACES);

		Object[] object = values.split(DAOConstants.SPLIT_OPERATOR);

		updateInclause(object);
	}

	/**
	 * @param columnName :
	 * @param sourceObjectName :
	 * @param object :
	 */
	public INClause(String columnName,
			Object[] object,String sourceObjectName)
	{
		strBuff = new StringBuffer(DAOConstants.TAILING_SPACES);

		String sourceObject = DAOUtility.getInstance().parseClassName(sourceObjectName);

		strBuff.append(sourceObject).append(DAOConstants.DOT_OPERATOR).append(columnName).
		append(DAOConstants.TAILING_SPACES).append("in").append(DAOConstants.TAILING_SPACES);

		updateInclause(object);
	}
	/**
	 * @param columnName :
	 * @param condition :
	 * @param values :
	 * @return :
	 *//*
	public String addCondition(String columnName, String condition, String values)
	{
		StringBuffer strBuff = new StringBuffer();
		strBuff.append(columnName).append("   ").append(condition);
		Object[] object = values.split(",");
		updateInclause(object);
		return strBuff.toString();
	}

	*//**
	 * @param columnName :
	 * @param condition :
	 * @param object :
	 * @return :
	 *//*
	public String addCondition(String columnName, String condition,
			Object[] object)
	{
		return null;
	}

*/

	/**
	 * @param object :
	 */
	private void updateInclause(Object[] object)
	{

		strBuff.append("(  ");
		for (int j = 0; j < object.length; j++)
		{

			if(object[j] instanceof String)
			{
				strBuff.append("'"+object[j]+"'");
			}
			else
			{
				strBuff.append(object[j]);
			}

			if((j+1) < object.length)
			{
				strBuff.append(", ");
			}
		}
		strBuff.append(") ");
	}

	/**
	 * Returns the string value.
	 * @return String:
	 */
	public String toString()
	{
		return strBuff.toString();
	}

}
