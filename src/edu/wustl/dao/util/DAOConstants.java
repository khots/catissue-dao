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
	 * NEW_SESSION_ERROR.
	 */
	public static final String NEW_SESSION_ERROR = " Problem in creating new session ";

	/**
	 * OPEN_SESSION_ERROR.
	 */
	public static final String OPEN_SESSION_ERROR = " Open Hibernate session error: ";

	/**
	 * CLOSE_SESSION_ERROR.
	 */
	public static final String CLOSE_SESSION_ERROR = " Close Hibernate session error: ";

	/**
	 * COMMIT_DATA_ERROR.
	 */
	public static final String COMMIT_DATA_ERROR = " Data commit error: ";
	/**
	 * ROLLBACK_ERROR.
	 */
	public static final String ROLLBACK_ERROR = " Data Rollback error: ";

	/**
	 * Disabling objects.
	 */
	public static final String DISABLE_RELATED_OBJ = "Object disabling error:";


	/**
	 * insert objects.
	 */
	public static final String INSERT_OBJ_ERROR = " Object insert error: ";


	/**
	 * update object.
	 */
	public static final String UPDATE_OBJ_ERROR = "Object update error:";


	/**
	 * delete object.
	 */
	public static final String DELETE_OBJ_ERROR = " Delete object error: ";

	/**
	 * Retrieve attribute error.
	 */
	public static final String RETRIEVE_ERROR = " retrieve error: ";


	/**
	 * Audit error.
	 */
	public static final String AUDIT_ERROR = " Auditing error: ";


	/**
	 * Problem in executing query.
	 */
	public static final String EXECUTE_QUERY_ERROR = " Error while executing SQL query ";

	/**
	 * Default DAO.
	 */
	public static final String DEFAULTDAO_INIT_ERR = " Problem while retrieving the default DAO ";

	/**
	 * Default JDBCDAO.
	 */
	public static final String JDBCDAO_INIT_ERR = " Problem while retrieving the JDBC DAO ";

	/**
	 * Error while closing connections.
	 */
	public static final String CLOSE_CONN_ERR = " Problem while closing connections : ";

	/**
	 * Error while building session factory.
	 */
	public static final String BUILD_SESS_FACTORY_ERR = " Problem while building Sessoin Factory : ";

	/**
	 * Error while instantiating connection manager.
	 */
	public static final String CONN_MANAGER_INIT_ERR =
		" Problem while instantiating connection manager: ";


	/**
	 * Error while instantiating connection manager.
	 */
	public static final String CONFIG_FILE_PARSE_ERROR =
		" Problem while configuring or parsing configuration file: ";

	/**
	 * Problem while parsing Application DAO properties file..
	 */
	public static final String FILE_PARSE_ERROR = "Problem while parsing application DAO properties file";

	/**
	 * Database Statement creation error.
	 */
	public static final String STMT_CREATION_ERROR = " Problem Occurred while obtaining database statement. ";
	/**
	 * Database ResultSet creation Error.
	 */
	public static final String RESULTSET_CREATION_ERROR =
		" Problem Occurred while obtaining database resultSet. ";
	/**
	 * Database RS meta data creation error.
	 */
	public static final String RS_METADATA_ERROR =
		" Problem Occurred while obtaining database ResultSetMetaData. ";

	/**
	 * Problem while batch update insert.
	 */
	public static final String BATCH_UPDATE_ERROR =
		" Problem Occurred while batch update. ";

	/**
	 * Problem while batch update insert.
	 */
	public static final String BATCH_SIZE_ERROR =
		" Batch size exceeded then specified. ";

	/**
	 * Problem while batch update insert.
	 */
	public static final String CLEAR_BATCH_ERROR =
		" Error while clearing batch ";

	/**
	 * Database prepared statement creation error.
	 */
	public static final String PRPDSTMT_CREATION_ERROR =
		" Problem Occurred while obtaining database PreparedStatement. ";

	/**
	 * No parameters assigned to prepared statement.
	 */
	public static final String NO_PARAMETERS_TO_STMT =
		"No parameter value set to statement";

	/**
	 * NO_CONNECTION_TO_DB.
	 */
	public static final String NO_CONNECTION_TO_DB ="Problem occurred during a database operation:" +
			" No connection to the database :";


	/**
	 * METHOD_WITHOUT_IMPLEMENTATION.
	 */
	public static final String METHOD_WITHOUT_IMPLEMENTATION = "There is no implementation " +
			"for this method : " +
			"Check again before calling this method.";

	/**
	 * error message.
	 */
	public static final String CONSTRAINT_VOILATION_ERROR =" Submission failed since a " +
			"{0} with the same {1} already exists";

	/**
	 *  The unique key error message is "Duplicate entry %s for key %d".
	 *  This string is used for searching " for key " string in the above error message
	 */
	public static final String MYSQL_DUPL_KEY_MSG = " for key ";

	/**
	 * Dot operators.
	 */
	public static final String DOT_OPERATOR = ".";

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
