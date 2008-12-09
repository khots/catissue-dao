package edu.wustl.dao.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kalpana_thakur
 *
 */
public final class QueryConditions
{
	/**
	 * creates a single object.
	 */
	private static QueryConditions queryCondition = new QueryConditions();;
	/**
	 * Private constructor.
	 */
	private QueryConditions()
	{

	}
	/**
	 * returns the single object.
	 * @return Utility object
	 */
	public static QueryConditions getInstance()
	{
		return queryCondition;
	}

	/**
	 * @return Map having condition name as the key and
	 * condition fully class name as the value.
	 */
	public static Map<String, String> getWhereClauseCondMap()
	{
		Map<String, String> queryCondMap = new HashMap<String, String>();
		queryCondMap.put(DAOConstants.IN_CONDITION, "edu.wustl.dao.condition.INClause");
		queryCondMap.put(DAOConstants.EQUAL_CONDITION, "edu.wustl.dao.condition.EqualClause");
		queryCondMap.put(DAOConstants.NOT_EQUAL_CONDITION, "edu.wustl.dao.condition.NotEqualClause");
		queryCondMap.put(DAOConstants.NOT_NULL_CONDITION, "edu.wustl.dao.condition.NotNullClause");
		queryCondMap.put(DAOConstants.NULL_CONDITION, "edu.wustl.dao.condition.NullClause");

		return queryCondMap;

	}

}
