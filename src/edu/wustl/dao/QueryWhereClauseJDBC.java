package edu.wustl.dao;

import edu.wustl.common.util.global.Constants;

/**
 * @author kalpana_thakur
 *
 */
public class QueryWhereClauseJDBC extends QueryWhereClause
{

	/**
	 * This will check query Conditions.
	 * @return true if query Conditions will be satisfied
	 *//*
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

	*//**
	 * This will check WhereColumnValue array.
	 * @return true if whereColumnName is not null.
	 *//*
	private boolean isWhereColumnValue()
	{

		return whereColumnValue != null ;//&& whereColumnName.length == whereColumnValue.length;
	}*/


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

		 for (int i = 0; i < whereColumnName.length; i++)
	     {
	        	setClausesOfWherePart(sourceObjectName,queryStrBuff, i);

	        	if (i < (whereColumnName.length - 1))
	        	{
	        		queryStrBuff.append(" " + joinCondition + " ");
	    		}
	      }


		//jdbcQueryWhereCondition(sourceObjectName, queryStrBuff);

		return queryStrBuff.toString();
	}

	/**
	 * This will append the appropriate where condition.
	 * @param sourceObjectName  Holds the table name.
	 * @param queryStrBuff query string.
	 * @param index :index
	 */
	protected void setClausesOfWherePart(String sourceObjectName,
			StringBuffer queryStrBuff,int index)
	{

		queryStrBuff.append(sourceObjectName + "." + whereColumnName[index] + " ");
		if(whereColumnCondition[index].contains("in"))
		{
			inClauseOfWhereQuery(whereColumnCondition, whereColumnValue,
					queryStrBuff, index);
		}
		else if(whereColumnCondition[index].contains("is not null") ||
				whereColumnCondition[index].contains("is null"))
		{
			queryStrBuff.append(whereColumnCondition[index]);
		}
		else
		{
			//Object[] valArr = (Object [])whereColumnValue[index];
			//TODO array of column values are not handled
			queryStrBuff.append(whereColumnCondition[index]).append("'"+whereColumnValue[index]+"'");
		}

	}

	/**
	 * This will set the in clause of where clause.
	 * @param whereColumnCondition : Array of where column conditions
	 * @param whereColumnValue : Array of where column value
	 * @param sqlBuff : Where clause
	 * @param index index to the whereColumnValue Array.
	 */
	protected void inClauseOfWhereQuery(String[] whereColumnCondition,
			Object[] whereColumnValue, StringBuffer sqlBuff, int index)
	{

		sqlBuff.append(whereColumnCondition[index]).append("(  ");
		Object[] valArr = (Object [])whereColumnValue[index];
		for (int j = 0; j < valArr.length; j++)
		{
			//Logger.out.debug(sqlBuff);
			sqlBuff.append(valArr[j]);
			if((j+1) < valArr.length)
			{
				sqlBuff.append(", ");
			}
		}
		sqlBuff.append(") ");
	}

}
