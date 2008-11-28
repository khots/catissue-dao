package edu.wustl.dao.util;

/**
 * @author kalpana_thakur
 *TODO
 */
public class DAOConstants
{
	/**
	 * OPEN_SESSION_ERROR.
	 */
	public static final String OPEN_SESSION_ERROR = "Problem occurred while opening session :";
	/**
	 * NO_CONNECTION_TO_DB.
	 */
	public static final String NO_CONNECTION_TO_DB ="Problem occurred during a database operation:" +
			" No connection to the database :";

	/**
	 * COMMIT_DATA_ERROR.
	 */
	public static final String COMMIT_DATA_ERROR = "Problem occurred while Commiting " +
			"changes to the database :";
	/**
	 * ROLLBACK_ERROR.
	 */
	public static final String ROLLBACK_ERROR = "Problem occurred while rollback :";
	/**
	 * CONNECTIONS_CLOSING_ISSUE.
	 */
	public static final String CONNECTIONS_CLOSING_ISSUE = "Problem Occurred while" +
			" closing database connections";
	/**
	 * EXECUTE_UPDATE_ERROR.
	 */
	public static final String EXECUTE_UPDATE_ERROR = "Problem Occurred while " +
			"executing static SQL query";
	/**
	 * DB_PARAM_INIT_ERROR.
	 */
	public static final String DB_PARAM_INIT_ERROR = "Problem Occured while creating " +
			"database connection parameters";
	/**
	 * METHOD_WITHOUT_IMPLEMENTATION.
	 */
	public static final String METHOD_WITHOUT_IMPLEMENTATION = "There is no implementation " +
			"for this method : " +
			"Check again before calling this method.";

	/**
	 * Dot operators.
	 */
	public static final String DOT_OPERATORS = ".";

	/**
	 * Space .
	 */
	public static final String TAILING_SPACES = " ";

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
	public static final String IN_OPERATOR = "in";

	/**
	 * is not null operator.
	 */
	public static final String NOT_NULL_OPERATOR = "is not null";


	/**
	 * is null operator.
	 */
	public static final String NULL_OPERATOR = "is null";

	/**
	 * Equal(=) operator.
	 */
	public static final String EQUAL_OPERATOR = "=";



}
