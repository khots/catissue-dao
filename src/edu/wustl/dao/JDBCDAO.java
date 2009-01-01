/**
 * <p>Title: JDBCDAO Interface</p>
 * <p>Description :This interface defines methods which are specific to JDBC operations</p>
 *  @author kalpana_thakur
 */

package edu.wustl.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.util.PagenatedResultData;
import edu.wustl.common.querydatabean.QueryDataBean;
import edu.wustl.common.util.QueryParams;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.sqlformatter.SQLFormatter;

/** This interface defines methods which are specific to JDBC operations.*/
public interface JDBCDAO extends DAO
{

	/**
	 * Creates a table with the query specified.
	 * @param query Query create table.
	 * @throws DAOException generic DAOException.
	 */
	void createTable(String query) throws DAOException;

	/**
	* Returns the ResultSet containing all the rows according to the columns specified
	* from the table represented in sourceObjectName.
	* @param sourceObjectName The table name.
	* @param selectColumnName The column names in select clause.
	* @param onlyDistinctRows true if only distict rows should be selected
	* @return The ResultSet containing all the rows according to the columns specified
	* from the table represented in sourceObjectName.
	* @throws DAOException generic DAOException.
	*/
	List<Object> retrieve(String sourceObjectName, String[] selectColumnName, boolean onlyDistinctRows)
			throws DAOException;

	/**
	   * Retrieves the records for class name in sourceObjectName
	   * according to field values passed in the passed session.
	   * @param sourceObjectName The table name.
	   * @param selectColumnName An array of field names in select clause.
	   * @param queryWhereClauseImpl : This will hold following :
	   * 1.whereColumnName An array of field names in where clause.
	   * 2.whereColumnCondition The comparison condition for the field values.
	   * 3.whereColumnValue An array of field values.
	   * 4.joinCondition The join condition.
	   * @param onlyDistinctRows true if only distinct rows should be selected.
	   * @return The ResultSet containing all the rows according to the columns specified
	   * from the table represented in sourceObjectName.
	   * @throws DAOException generic DAOException.
	   */
	List<Object> retrieve(String sourceObjectName,
			String[] selectColumnName, QueryWhereClause queryWhereClauseImpl,
			boolean onlyDistinctRows) throws DAOException;

	/**
	   * Executes the query.
	   * @param query query to be execute.
	   * @param sessionDataBean session specific Data.
	   * @param isSecureExecute is Secure Execute.
	   * @param queryResultObjectDataMap query Result Object Data Map.
	   * @return list.
	   * @throws ClassNotFoundException Class Not Found Exception.
	   * @throws DAOException generic DAOException.
	   */
	List<Object> executeQuery(String query, SessionDataBean sessionDataBean, boolean isSecureExecute,
			Map<Object,QueryDataBean> queryResultObjectDataMap)
			throws ClassNotFoundException, DAOException;

	/**
	 * Description: Query performance issue. Instead of saving complete query results in session,
	 * resulted will be fetched for each result page navigation.object of class
	 * QuerySessionData will be saved session,which will contain the required information
	 * for query execution while navigating through query result pages.
	 * @param queryParams : This will hold the Query related information. TODO
	 * @return PagenatedResultData : Paginated data.
	 * @throws ClassNotFoundException :ClassNotFoundException
	 * @throws DAOException generic DAOException
	 */
	PagenatedResultData executeQuery(QueryParams  queryParams)
	throws ClassNotFoundException, DAOException;

	/**
	 * Inserts records in given table.
	 * @param tableName Name of the table in which record to be inserted
	 * @param columnValues column data
	 * @param columnNames (optional)column names- if not not provided,
	 * @throws DAOException generic DAOException
	 * @throws SQLException SQL Exception.
	 */
	void insertHashedValues(String tableName, List<Object> columnValues,List<String>
	columnNames) throws DAOException, SQLException;

	/**
	* Inserts records in given table.
	* @param tableName Name of the table in which record to be inserted
	* @param columnValues column data
	* @param columnNames (optional)column names- if not not provided,
	* all column names of the table are added to the list of column names
	* @throws DAOException generic DAOException
	* @throws SQLException SQL Exception.
	*/
	/*void insert(String tableName, List<Object> columnValues, List<String>... columnNames)
			throws DAOException, SQLException;*/

	/**
	   * Creates a table with the name and columns specified.
	   * @param tableName Name of the table to create.
	   * @param columnNames Columns in the table.
	   * @throws DAOException generic DAOException
	   */
	void createTable(String tableName, String[] columnNames) throws DAOException;

	/**
	   * Deletes the specified table.
	   * @param tableName Name of the table to delete.
	   * @throws DAOException generic DAOException.
	   */
	void delete(String tableName) throws DAOException;

	/**
	 * This method gets Activity Status.
	 * @param sourceObjectName The table name.
	 * @param identifier identifier
	 * @return Activity Status.
	 * @throws DAOException generic DAOException.
	 *//*
	String getActivityStatus(String sourceObjectName, Long identifier) throws DAOException;*/

	/**
	 * This method will execute the SQL and modifies the database.
	 * @param sql sql statement.
	 * @throws DAOException generic DAOException.
	 */
	void executeUpdate(String sql) throws DAOException;
	/**
	 *@return :This will return the Date Pattern.
	 */
	String getDatePattern();

	/**
	 * @return :This will return the Time Pattern.
	 */
	String getTimePattern();
	/**
	 * @return :This will return the Date Format Function.
	 */
	String getDateFormatFunction();

	/**
	 * @return :This will return the Time Format Function.
	 */
	String getTimeFormatFunction();

	/**
	 * @return :This will return the Date to string function
	 */
	String getDateTostrFunction();

	/**
	 * @return :This will return the string to Date function
	 */
	String getStrTodateFunction();

	/**
	 * @param excp : Exception Object.
	 * @param connection :
	 * @return : It will return the formated messages.
	 */
	String formatMessage(Exception excp,Connection connection);
	/**
	 * @param columnCount count of the columns in results
	 * @param getSublistOfResult boolean for getting sublist
	 * @return int column count
	 */
	int getColumnCount(int columnCount,boolean getSublistOfResult);
	/**
	 * Gets SQL for Like operator.
	 * @param attributeName name of the attribute
	 * @param value value
	 * @return String SQL
	 */
	String getSQLForLikeOperator(String attributeName, String value);
	/**
	 * Required for temporal query.
	 * @return Object of type either MySQL or Oracle Primitive operation processor.
	 */
	Object getPrimitiveOperationProcessor();
	/**
	 * This method fires a query to insert auditing details into audit tables.
	 * @param sql SQL to be fired
	 * @param sessionData session data to get userId and ip address
	 * @param comments comments to be inserted in the table
	 * @throws DAOException DaoException
	 */
	void executeAuditSql(String sql, SessionDataBean sessionData, String comments) throws DAOException;

	/**
	 *This method will be called to format the SQL.
	 *@param tableName :
	 *@throws DAOException :Generic DAOException.
	 *@return SQLFormatter :
	 */
	SQLFormatter getSQLFormatter(String tableName) throws DAOException;

	/**
	 * @param sqlFormatter :
	 * @param sequenceName :
	 * @param columnName :
	 * @param columnTpe :
	 * @throws DAOException :Generic DAOException.
	 */
	void insert(SQLFormatter sqlFormatter,String sequenceName,String columnName,
			int columnTpe) throws DAOException;

	/**
	 * @param query :
	 * @param clobContent :
	 * @throws DAOException :Generic DAOException.
	 */
	void updateClob(String query,String clobContent)throws DAOException;

	/**
	 * This method will be called to obtained max barcode.
	 * @return max Barcode column.
	 */
	String getMaxBarcodeCol();

}
