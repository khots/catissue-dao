/**
 * <p>Title: JDBCDAO Interface</p>
 * <p>Description :This interface defines methods which are specific to JDBC operations</p>
 *  @author kalpana_thakur
 */

package edu.wustl.dao;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.querydatabean.QueryDataBean;
import edu.wustl.common.util.PagenatedResultData;
import edu.wustl.common.util.QueryParams;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.sqlformatter.SQLFormatter;

/** This interface defines methods which are specific to JDBC operations.*/
public interface JDBCDAO extends DAO
{

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
	 * This method will execute the SQL and modifies the database.
	 * @param sql sql statement.
	 * @throws DAOException generic DAOException.
	 */
	void executeUpdate(String sql) throws DAOException;

	/**
	 * @param excp : Exception Object.
	 * @param connection :
	 * @return : It will return the formated messages.
	 * @throws DAOException :Generic DAOException.
	 */
	String formatMessage(Exception excp,Connection connection)throws DAOException;

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
	 * This method will be called to set the size of the batch.
	 * @param batchSize batchSize
	 * @throws DAOException : Generic database exception.
	 */
	void setBatchSize(int batchSize)throws DAOException;

	/**
	 * Deletes the table from the database.
	 * @param tableName The table to be deleted.
	 * @throws DAOException generic DAOException.
	 */
	void deleteTable(String tableName) throws DAOException;

	/**
	 * @return :This will return the Date Pattern.
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
	 * This method will be called to set all the database specific properties.
	 * @param databaseProperties : database properties.
	 */
	void setDatabaseProperties(DatabaseProperties databaseProperties);

	/**
	 * This method will be called to get the database name.
	 * @return database name.
	 */
	String getDataBaseName();

}
