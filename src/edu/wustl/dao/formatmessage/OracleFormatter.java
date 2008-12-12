/*
 * TODO
 */
package edu.wustl.dao.formatmessage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;

import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.DAOConstants;
import edu.wustl.dao.util.DAOUtility;
import edu.wustl.dao.util.DatabaseConnectionParams;
/**
 * @author kalpana_thakur
 *
 */
public class OracleFormatter implements IDBFormatter
{

	/**
	 * Class Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(OracleFormatter.class);

	/**
	 * This will generate the formatted error messages.
	 * @param excp :Exception.
	 * @param connection : connection.
	 * @return the formated messages.
	 */
	public String getFormatedMessage(Exception excp,Connection connection)
	{

		Exception objExcp = excp;
        StringBuffer columnNameBuff = new StringBuffer(DAOConstants.TAILING_SPACES);
        String formattedErrMsg = ""; // Formatted Error Message return by this method
	    if(objExcp instanceof gov.nih.nci.security.exceptions.CSTransactionException)
        {
            objExcp = (Exception)objExcp.getCause();
        }
        try
        {

        	if(connection == null)
            {
        		logger.fatal(DAOConstants.NO_CONNECTION_TO_DB);
            }

        	// Get Constraint Name from messages
        	String sqlMessage = DAOUtility.getInstance().generateErrorMessage(objExcp);
        	int tempstartIndexofMsg = sqlMessage.indexOf('(');

            String temp = sqlMessage.substring(tempstartIndexofMsg);
            int startIndexofMsg = temp.indexOf(DAOConstants.SPLIT_OPERATOR);
            int endIndexofMsg = temp.indexOf(')');
            String strKey =temp.substring((startIndexofMsg+1),endIndexofMsg);
            startIndexofMsg = strKey.indexOf(".");
            String key =strKey.substring((startIndexofMsg+1));

            formattedErrMsg = getFormatedMessage(
					columnNameBuff, connection,
					key);

         }
        catch(Exception e)
        {
            logger.error(e.getMessage(),e);
        }
        return formattedErrMsg;
    }

/**
 * @param columnNameBuff ;
 * @param connection :
 * @param key :
 * @return :
 * @throws DAOException :
 * @throws SQLException :
 */
	private String getFormatedMessage(StringBuffer columnNameBuff,
			Connection connection, String key)throws DAOException, SQLException

	{
		String formattedErrMsg = "";
		String tableName = "";

		DatabaseConnectionParams databaseConnectionParams = new DatabaseConnectionParams();
		try
		{
			databaseConnectionParams.setConnection(connection);
			String query = "select COLUMN_NAME,TABLE_NAME from user_cons_columns" +
			" where constraint_name = '"+key+"'";

			ResultSet resultSet = databaseConnectionParams.getResultSet(query);
			while(resultSet.next())
			{
				columnNameBuff.append(resultSet.getString("COLUMN_NAME")).
				append(DAOConstants.SPLIT_OPERATOR);
				tableName = resultSet.getString("TABLE_NAME");
			}
			if(columnNameBuff.length()>0 && tableName.length()>0)
			{
				String columnName = columnNameBuff.toString().
				substring(0,columnNameBuff.toString().length()-1);
				String displayName = DAOUtility.getInstance().getDisplayName(tableName,connection);
				Object[] arguments = new Object[]{displayName,columnName};
				formattedErrMsg = MessageFormat.format(DAOConstants.
						CONSTRAINT_VOILATION_ERROR,arguments);
			}
		}
		catch (Exception exp)
		{
			logger.fatal(exp.getMessage(), exp);
		}
		finally
		{
			databaseConnectionParams.closeConnectionParams();
		}
		return formattedErrMsg;
	}

}
