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
	 * strBuff.
	 */
	private final StringBuffer strBuff;

	/**
	 * @param columnName :
	 * @param sourceObjectName :
	 * @param object :
	 */
	public EqualClause (String columnName ,Object object,String sourceObjectName)
	{
		strBuff = new StringBuffer(DAOConstants.TAILING_SPACES);

		String sourceObject = DAOUtility.getInstance().parseClassName(sourceObjectName);

		strBuff.append(sourceObject).append(DAOConstants.DOT_OPERATOR).
		append(columnName).append(DAOConstants.TAILING_SPACES).append(DAOConstants.EQUAL_OPERATOR).
		append(DAOConstants.TAILING_SPACES);

		if(object instanceof String)
		{
			strBuff.append("'"+object+"'");
		}
		else
		{
			strBuff.append(object);
		}
		strBuff.append(DAOConstants.TAILING_SPACES);
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
