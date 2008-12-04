package edu.wustl.dao.util;

/**
 * @author kalpana_thakur
 *TODO
 */
public class DAOConstants
{
	/**
	 * NEW_SESSION_ERROR.
	 */
	public static final String NEW_SESSION_ERROR = "Problem in creating new session";

	/**
	 * OPEN_SESSION_ERROR.
	 */
	public static final String OPEN_SESSION_ERROR = "Open Hibernate session error:";

	/**
	 * CLOSE_SESSION_ERROR.
	 */
	public static final String CLOSE_SESSION_ERROR = "Close Hibernate session error:";

	/**
	 * COMMIT_DATA_ERROR.
	 */
	public static final String COMMIT_DATA_ERROR = "Data commit error:";
	/**
	 * ROLLBACK_ERROR.
	 */
	public static final String ROLLBACK_ERROR = "Data Rollback error:";

	/**
	 * Disabling objects.
	 */
	public static final String DISABLE_RELATED_OBJ = "Object disabling error:";


	/**
	 * insert objects.
	 */
	public static final String INSERT_OBJ_ERROR = "Object insert error:";


	/**
	 * update object.
	 */
	public static final String UPDATE_OBJ_ERROR = "Object update error:";


	/**
	 * delete object.
	 */
	public static final String DELETE_OBJ_ERROR = " Delete object error:";


	/**
	 * Default DAO.
	 */
	public static final String DEFAULT_DAO_INSTANTIATION_ERROR = "Problem while retrieving the default DAO";

	/**
	 * Default JDBCDAO.
	 */
	public static final String JDBCDAO_INSTANTIATION_ERROR = "Problem while retrieving the JDBC DAO";


	/**
	 * Error while building session factory.
	 */
	public static final String BUILD_SESSION_FACTORY_ERROR = "Problem while building Sessoin Factory :";

	/**
	 * Error while instantiating connection manager.
	 */
	public static final String CONN_MANAGER_INSTANTIATION_ERROR =
		"Problem while instantiating connection manager:";


	/**
	 * Error while instantiating connection manager.
	 */
	public static final String CONFIG_FILE_PARSE_ERROR =
		"Problem while configuring or parsing configuration file:";


	/**
	 * Database Statement creation error.
	 */
	public static final String STMT_CREATION_ERROR = "Problem Occurred while obtaining database statement.";
	/**
	 * Database ResultSet creation Error.
	 */
	public static final String RESULTSET_CREATION_ERROR =
		"Problem Occurred while obtaining database resultSet.";
	/**
	 * Database RS meta data creation error.
	 */
	public static final String RS_METADATA_ERROR =
		"Problem Occurred while obtaining database ResultSetMetaData.";
	/**
	 * Database prepared statement creation error.
	 */
	public static final String PRPD_STMT_ERROR =
		"Problem Occurred while obtaining database PreparedStatement.";


	/**
	 * NO_CONNECTION_TO_DB.
	 */
	public static final String NO_CONNECTION_TO_DB ="Problem occurred during a database operation:" +
			" No connection to the database :";


	/**
	 * EXECUTE_UPDATE_ERROR.
	 */
	public static final String EXECUTE_QUERY_ERROR = "Problem Occurred while " +
			"executing static SQL query";

	/**
	 * METHOD_WITHOUT_IMPLEMENTATION.
	 */
	public static final String METHOD_WITHOUT_IMPLEMENTATION = "There is no implementation " +
			"for this method : " +
			"Check again before calling this method.";

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


	/**
	 * Not Equal(!=) operator.
	 */
	public static final String NOT_EQUAL_OPERATOR = "!=";

	/**
	 * index value operator"?".
	 */
	public static final String INDEX_VALUE_OPERATOR = "?";

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
