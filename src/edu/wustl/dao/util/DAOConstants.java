package edu.wustl.dao.util;

/**
 * @author kalpana_thakur
 *
 */
public class DAOConstants
{
	/**
	 * OPEN_SESSION_ERROR.
	 * TODO
	 */
	public static final String OPEN_SESSION_ERROR = "Problem occurred while opening session :";
	/**
	 * NO_CONNECTION_TO_DB.
	 * TODO
	 */
	public static final String NO_CONNECTION_TO_DB ="Problem occurred during a database operation:" +
			" No connection to the database :";

	/**
	 * COMMIT_DATA_ERROR.
	 * TODO
	 */
	public static final String COMMIT_DATA_ERROR = "Problem occurred while Commiting " +
			"changes to the database :";
	/**
	 * ROLLBACK_ERROR.
	 * TODO
	 */
	public static final String ROLLBACK_ERROR = "Problem occurred while rollback :";
	/**
	 * CONNECTIONS_CLOSING_ISSUE.
	 * TODO
	 */
	public static final String CONNECTIONS_CLOSING_ISSUE = "Problem Occurred while" +
			" closing database connections";
	/**
	 * EXECUTE_UPDATE_ERROR.
	 * TODO
	 */
	public static final String EXECUTE_UPDATE_ERROR = "Problem Occurred while " +
			"executing static SQL query";
	/**
	 * DB_PARAM_INIT_ERROR.
	 * TODO
	 */
	public static final String DB_PARAM_INIT_ERROR = "Problem Occured while creating " +
			"database connection parameters";
	/**
	 * METHOD_WITHOUT_IMPLEMENTATION.
	 * TODO
	 */
	public static final String METHOD_WITHOUT_IMPLEMENTATION = "There is no implementation " +
			"for this method : " +
			"Check again before calling this method.";

}
