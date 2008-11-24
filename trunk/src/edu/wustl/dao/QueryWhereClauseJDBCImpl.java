package edu.wustl.dao;

import edu.wustl.common.util.global.Constants;

/**
 * @author kalpana_thakur
 *
 */
public class QueryWhereClauseJDBCImpl extends QueryWhereClauseImpl
{

	/**
	 * This will check query Conditions.
	 * @return true if query Conditions will be satisfied
	 */
	public boolean isConditionSatisfied()
	{

		boolean isConditionSatisfied = false;
		if ((isWhereColumnName())
                && (isWhereColumnCondition())
                && (isWhereColumnValue()))
        {
			isConditionSatisfied = true;
        }

		return isConditionSatisfied;
	}

	/**
	 * This will check WhereColumnValue array.
	 * @return true if whereColumnName is not null.
	 */
	private boolean isWhereColumnValue()
	{

		return whereColumnValue != null && whereColumnName.length == whereColumnValue.length;
	}


	/**
	 * @param sourceObjectName :Object Name
	 * @return query where clause.
	 */
	public String jdbcQueryWhereClause(String sourceObjectName)
	{
		StringBuffer queryStrBuff = new StringBuffer();
		if (joinCondition == null)
		{
			joinCondition = Constants.AND_JOIN_CONDITION;
		}

		queryStrBuff.append(" WHERE ");
		int index;
		for (index = 0; index < (whereColumnName.length - 1); index++)
		{
			queryStrBuff.append(sourceObjectName + "." + whereColumnName[index] + " "
					+ whereColumnCondition[index] + " " + whereColumnValue[index]);
			queryStrBuff.append(" " + joinCondition + " ");
		}
		queryStrBuff.append(sourceObjectName + "." + whereColumnName[index] + " "
				+ whereColumnCondition[index] + " " + whereColumnValue[index]);

		return queryStrBuff.toString();
	}
}
