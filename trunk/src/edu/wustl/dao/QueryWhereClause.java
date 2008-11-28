package edu.wustl.dao;

import edu.wustl.dao.condition.Condition;
import edu.wustl.dao.util.DAOConstants;

/**
 * @author kalpana_thakur
 *
 */

public class QueryWhereClause
{
	/**
	 * TODO.
	 */
	private final StringBuffer whereClauseBuff;

	/**
	 * Default Cons.
	 */
	public QueryWhereClause()
	{
		whereClauseBuff = new StringBuffer();
		whereClauseBuff.append(" WHERE ");
	}


	/**
	 * @return :
	 */
	public String toWhereClause()
	{
		return whereClauseBuff.toString();
	}


	/**
	 * @return :
	 */
	public String operatorAnd()
	{
		return whereClauseBuff.append(DAOConstants.AND_JOIN_CONDITION).toString();
	}

	/**
	 * @return :
	 */
	public String operatorOr()
	{
		return whereClauseBuff.append(DAOConstants.OR_JOIN_CONDITION).toString();
	}



	/**
	 * @param condition :
	 */
	public void addCondition(Condition condition)
	{
		whereClauseBuff.append(condition.toString());
	}

/*


	*//**
	 * TODO.
	 *//*
	protected String[] whereColumnName;

	*//**
	 * TODO.
	 *//*
	protected String[] whereColumnCondition;

	*//**
	 * TODO.
	 *//*
	protected Object[] whereColumnValue;

	*//**
	 * TODO.
	 *//*
	protected String joinCondition;

	*//**
	 * This will be called to set where clause parameters.
	 * @param whereColumnName :
	 * @param whereColumnCondition :
	 * @param whereColumnValue :
	 * @param joinCondition :
	 * TODO
	 *//*
	public void setWhereClause(String[] whereColumnName,String[] whereColumnCondition,
			Object[] whereColumnValue,String joinCondition)
	{
		this.joinCondition = joinCondition;
		this.whereColumnCondition = whereColumnCondition;
		this.whereColumnName = whereColumnName;
		this.whereColumnValue = whereColumnValue;
	}

	*//**
	 * This method will return the string representing where clause.
	 * @param className : Name of class
	 * @return String :Where clause.
	 *//*
	protected String queryWhereClause(String className)
	{
		StringBuffer sqlBuff = new StringBuffer();

		 if (joinCondition == null)
		 {
			 joinCondition = Constants.AND_JOIN_CONDITION;
		 }

		 sqlBuff.append(" where ");
         //Adds the column name and search condition in where clause.
        for (int i = 0; i < whereColumnName.length; i++)
        {
        	setClausesOfWherePart(className,sqlBuff, i);

        	if (i < (whereColumnName.length - 1))
        	{
    			sqlBuff.append(" " + joinCondition + " ");
    		}
        }

		return sqlBuff.toString();
	}




	*//**
	 * This will set various condition clause of where clause.
	 * @param className Name of the class.
	 * @param sqlBuff where clause string.
	 * @param index index to the wherecolumnCondition Array.
	 *//*
	protected void setClausesOfWherePart(String className,
			StringBuffer sqlBuff, int index)
	{

		sqlBuff.append(className + "." + whereColumnName[index] + " ");
		//TODO check this twice with original
		if(whereColumnCondition[index].contains("in"))
		{
			inClauseOfWhereQuery(whereColumnCondition, whereColumnValue,
					sqlBuff, index);
		}
		else if(whereColumnCondition[index].contains("is not null"))
		{
			sqlBuff.append(whereColumnCondition[index]);
		}
		else if(whereColumnCondition[index].contains("is null"))
		{
			sqlBuff.append(whereColumnCondition[index]);
		}
		else
		{
			sqlBuff.append(whereColumnCondition[index]).append("  ? ");
		}
	}

	*//**
	 * This will set the in clause of where clause.
	 * @param whereColumnCondition : Array of where column conditions
	 * @param whereColumnValue : Array of where column value
	 * @param sqlBuff : Where clause
	 * @param index index to the whereColumnValue Array.
	 *//*
	protected void inClauseOfWhereQuery(String[] whereColumnCondition,
			Object[] whereColumnValue, StringBuffer sqlBuff, int index)
	{

		sqlBuff.append(whereColumnCondition[index]).append("(  ");
		Object[] valArr = (Object [])whereColumnValue[index];
		for (int j = 0; j < valArr.length; j++)
		{
			//Logger.out.debug(sqlBuff);
			sqlBuff.append("? ");
			if((j+1)<valArr.length)
			{
				sqlBuff.append(", ");
			}
		}
		sqlBuff.append(") ");
	}

	*//**
	 * This method will be called to get where clause string.
	 * @param className : Name of class
	 * @return : where clause.
	 *//*
	public String toString(String className)
	{
		return queryWhereClause(className);
	}

	*//**
	 * This will check query Conditions.
	 * @return true if query Conditions will be satisfied
	 *//*
	public boolean isConditionSatisfied()
	{
		boolean isConditionSatisfied = false;
		if ((isWhereColumnName())
                && (isWhereColumnCondition())
                && (whereColumnValue != null))
        {
			isConditionSatisfied = true;
        }
		return isConditionSatisfied;
	}

	*//**
	 * This will check where Column conditions.
	 * @return true if whereColumnCondition is not null and
	 * length of whereColumnCondition array will be equal to whereColumnName array.
	 *//*
	protected boolean isWhereColumnCondition()
	{
		return whereColumnCondition != null && whereColumnCondition.length == whereColumnName.length;
	}

	*//**
	 * This will check WhereColumnName .
	 * @return true if whereColumnName is not null.
	 *//*
	protected boolean isWhereColumnName()
	{
		return whereColumnName != null && whereColumnName.length > 0;
	}

	*//**
	 * This method will set the query parameters.
	 * @param query Query Object
	 *//*
	public void setParametersToQuery(Query query)
	{
		int index = 0;
		//Adds the column values in where clause
		for (int i = 0; i < whereColumnValue.length; i++)
		{
		    //Logger.out.debug("whereColumnValue[i]. " + whereColumnValue[i]);
			if (!(whereColumnCondition[i].equals("is null") ||
					whereColumnCondition[i].equals("is not null")))
			{
			  Object obj = whereColumnValue[i];
		        if(obj instanceof Object[])
		        {
		        	index = setParametersForArray(query, index, obj);
		        }
		        else
		        {
		        	query.setParameter(index, obj);
		        	index++;
		        }
		    }
		}
	}

	*//**
	 * This method will set the query parameters.
	 * @param query Query Object
	 * @param indexVal index
	 * @param obj Array Object having query parameters.
	 * @return updated index.
	 *//*
	protected int setParametersForArray(Query query, int indexVal, Object obj)
	{
		int index = indexVal;
		Object[] valArr = (Object[])obj;
		for (int j = 0; j < valArr.length; j++)
		{
			query.setParameter(index, valArr[j]);
			index++;
		}
		return index;
	}
*/
}
