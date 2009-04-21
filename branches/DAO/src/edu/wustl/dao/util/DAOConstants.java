/**
 * <p>Title: DAOConstants Class>
 * <p>Description:	DAOConstants class holds the DAO specific constants .</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @version 1.00
 * @author kalpana_thakur
 */
package edu.wustl.dao.util;

/**
 * @author kalpana_thakur
 * This class holds all DAO specific constants.
 */
public final class DAOConstants
{

	/**
	 * creates a single object.
	 */
	private static DAOConstants daoConstant = new DAOConstants();;
	/**
	 * Private constructor.
	 */
	private DAOConstants()
	{

	}
	/**
	 * returns the single object.
	 * @return Utility object
	 */
	public static DAOConstants getInstance()
	{
		return daoConstant;
	}

	
	/**
	 * Dot operators.
	 */
	public static final String DOT_OPERATOR = ".";

	/**
	 * Space .
	 */
	public static final String TRAILING_SPACES = " ";

	/**
	 * And join condition.
	 */
	public static final String AND_JOIN_CONDITION = "AND";

	/**
	 * or join condition.
	 */
	public static final String OR_JOIN_CONDITION = "OR";

	/**
	 * Split operator.
	 */
	public static final String SPLIT_OPERATOR = ",";

	/**
	 * in operator.
	 */
	public static final String IN_CONDITION = "in";

	/**
	 * is not null operator.
	 */
	public static final String NOT_NULL_CONDITION = "is not null";


	/**
	 * is null operator.
	 */
	public static final String NULL_CONDITION = "is null";

	/**
	 * Equal(=) operator.
	 */
	public static final String EQUAL = "=";

	/**
	 * like operator.
	 */
	public static final String LIKE = "like";


	/**
	 * Greater (>) operator.
	 */
	public static final String GREATERTHEN = ">";

	/**
	 * Less then (<) operator.
	 */
	public static final String LESSTHEN = "<";


	/**
	 * Not Equal(!=) operator.
	 */
	public static final String NOT_EQUAL = "!=";

	/**
	 * index value operator"?".
	 */
	public static final String INDEX_VALUE_OPERATOR = "?";

	/**
	 * Security constant.
	 */
	public static final boolean SWITCH_SECURITY = true;


	/**
	 * Time pattern.
	 * TODO : need to confirm.
	 */
	public static final String TIME_PATTERN_HH_MM_SS = "HH:mm:ss";
	/**
	 * Date pattern.
	 */
	public static final String DATE_PATTERN_MM_DD_YYYY = "MM-dd-yyyy";

	/**
	 * Opening bracket.
	 *//*
	public static final String OPENING_BRACKET_OPERATOR = "(";


	*//**
	 * Closing bracket.
	 *//*
	public static final String CLOSING_BRACKET_OPERATOR = ")";
*/

}
