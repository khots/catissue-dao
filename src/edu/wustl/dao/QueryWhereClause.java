/*
 * TODO
 */
package edu.wustl.dao;

import java.lang.reflect.Constructor;
import java.util.Map;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.condition.Condition;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.DAOConstants;
import edu.wustl.dao.util.QueryConditions;

/**
 * @author kalpana_thakur
 *
 */

public class QueryWhereClause
{
	/**
	 * Class Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(AbstractJDBCDAOImpl.class);
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

	/**
	 * @param whereColumnName :
	 * @param whereColumnCondition :
	 * @param whereColumnValue :
	 * @param joinCondition :
	 * @return :
	 * @throws DAOException :
	 */
	public QueryWhereClause getWhereCondition(String[] whereColumnName, String[]
	       whereColumnCondition, Object[] whereColumnValue,	String joinCondition) throws DAOException
	{	Map<String,String> queryConMap = QueryConditions.getWhereClauseCondMap();
		boolean isJoinConSet = false;
		try
		{
			for(int index=0 ;index<whereColumnCondition.length;index++)
			{
				Condition condition = null;
				Class conditionClass = Class.forName((queryConMap.get(whereColumnCondition[index]))
						.toString());
				if(whereColumnCondition[index].contains(DAOConstants.IN_CONDITION))
				{
					Constructor constructor =	conditionClass.getConstructor(new Class[]
					                         	       {String.class  ,Object[].class} );
					condition = (Condition)constructor.newInstance(new Object[] {
							whereColumnName[index],whereColumnValue[index] } );
				}
				else if(whereColumnCondition[index].contains(DAOConstants.EQUAL_CONDITION) ||
					whereColumnCondition[index].contains(DAOConstants.NOT_EQUAL_CONDITION))
				{
					Constructor constructor = conditionClass.getConstructor(new Class[]
					                                  {String.class  ,Object.class } );
					condition = (Condition)constructor.newInstance(new Object[] {
							whereColumnName[index],whereColumnValue[index]  } );
				}
				else
				{
					Constructor constructor = conditionClass.getConstructor(new Class[]
					                               {String.class } );
					condition = (Condition)constructor.newInstance(new Object[] {
								whereColumnName[index]} );
				}
				if(condition.getSourceObjectName() == null)
				{
					condition.setSourceObjectName(sourceObjectName);
				}
				whereClauseBuff.append(condition.buildSql());
				if(!isJoinConSet)
				{
					whereClauseBuff.append(joinCondition);
				}
			}
		}
		catch(Exception exp)
		{
			logger.error(exp.getMessage(), exp);
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"QueryWhereClause.java :");
		}
		return this;
	}

}
