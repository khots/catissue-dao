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
	 * This will hold the complete where clause of query.
	 */
	private final StringBuffer whereClauseBuff;

	/**
	 * Class name or table name.
	 */
	private String sourceObjectName = "";

	/**
	 * It will instantiate the whereClause buff.
	 * @param sourceObjectName : name of the class or table.
	 */
	public QueryWhereClause(String sourceObjectName)
	{
		whereClauseBuff = new StringBuffer();
		whereClauseBuff.append(DAOConstants.TAILING_SPACES)
		.append("WHERE").append(DAOConstants.TAILING_SPACES);
		this.sourceObjectName = sourceObjectName;
	}

	/**
	 * It will instantiate the whereClause buff.
	 */
	public QueryWhereClause()
	{
		whereClauseBuff = new StringBuffer();
		whereClauseBuff.append(DAOConstants.TAILING_SPACES)
		.append("WHERE").append(DAOConstants.TAILING_SPACES);
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
	public QueryWhereClause andOpr()
	{
		whereClauseBuff.append(DAOConstants.AND_JOIN_CONDITION).toString();
		return this;
	}

	/**
	 * @return :
	 */
	public QueryWhereClause orOpr()
	{
		whereClauseBuff.append(DAOConstants.OR_JOIN_CONDITION).toString();
		return this;
	}



	/**
	 * @param condition :
	 * @return the QueryWhereClause object.
	 */
	public QueryWhereClause addCondition(Condition condition)
	{
		if(condition.getSourceObjectName() == null)
		{
			condition.setSourceObjectName(sourceObjectName);
		}
		whereClauseBuff.append(condition.buildSql());
		return this;
	}

}
