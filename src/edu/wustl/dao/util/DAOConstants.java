package edu.wustl.dao.util;

public class DAOConstants 
{

	public static final String OPEN_SESSION_ERROR = "Problem occurred while opening session :";
	public static final String NO_CONNECTION_TO_DB ="Problem occurred during a database operation: No connection to the database :";
	public static final String COMMIT_DATA_ERROR = "Problem occurred while Commiting changes to the database :";
	public static final String ROLLBACK_ERROR = "Problem occurred while rollback :";
	public static final String CONNECTIONS_CLOSING_ISSUE = "Problem Occurred while closing database connections";
	public static final String EXECUTE_UPDATE_ERROR = "Problem Occurred while executing static SQL query";
	public static final String DB_PARAM_INIT_ERROR = "Problem Occured while creating database connection parameters";
	public static final String METHOD_WITHOUT_IMPLEMENTATION = "There is no implementation for this method : " +
			"Check again before calling this method.";
	
}
