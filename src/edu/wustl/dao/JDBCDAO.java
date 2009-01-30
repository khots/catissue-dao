/**
 * <p>Title: JDBCDAO Interface</p>
 * <p>Description :This interface defines methods which are specific to JDBC operations</p>
 *  @author kalpana_thakur
 */

package edu.wustl.dao;

import java.sql.Connection;
import java.sql.ResultSet;

import edu.wustl.dao.exception.DAOException;

/** This interface defines methods which are specific to JDBC operations.*/
public interface JDBCDAO extends DAO
{

	/**
	 * This method will execute the SQL and modifies the database.
	 * @param sql sql statement.
	 * @return (1) the row count for INSERT,UPDATE or DELETE statements
	 * or (2) 0 for SQL statements that return nothing
	 * @throws DAOException generic DAOException.
	 */
	int executeUpdate(String sql) throws DAOException;

	/**
	 * This method will be called to get the result set.
	 * @param sql sql statement.
	 * @throws DAOException generic DAOException.
	 * @return ResultSet : ResultSet
	 */
	ResultSet getQueryResultSet(String sql)throws DAOException;

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
	 *//*
	SQLFormatter getSQLFormatter(String tableName) throws DAOException;

	*//**
	 * @param sqlFormatter :
	 * @param sequenceName :
	 * @param columnName :
	 * @param columnTpe :
	 * @throws DAOException :Generic DAOException.
	 *//*
	void insert(SQLFormatter sqlFormatter,String sequenceName,String columnName,
			int columnTpe) throws DAOException;

	*//**
	 * @param query :
	 * @param clobContent :
	 * @throws DAOException :Generic DAOException.
	 *//*
	void updateClob(String query,String clobContent)throws DAOException;*/

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
	 * Insert the Object in the database.
	 * @param sql Object to be inserted in database
	 * @param isAuditable is Auditable.
	 * @throws DAOException generic DAOException
	 */
	void insert(String sql,boolean isAuditable) throws DAOException;


}
