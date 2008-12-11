/**
 * <p>Title: DAOUtility Class>
 * <p>Description:	DAOUtility class holds utility methods for DAO .</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @version 1.00
 * @author kalpana_thakur
 */
package edu.wustl.dao.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.exception.DAOException;


/**
 * @author kalpana_thakur
 * This class holds utility methods for DAO.
 */
public final class DAOUtility
{
	/**
	 * GET_PARAMETERIZED_QUERIES_DETAILS String details of parameterized queries.
	 */
	 public static final String GET_PARAMETERIZED_QUERIES_DETAILS = "getParameterizedQueriesDetails";

	/**
	 * creates a singleton object.
	 */
	private static DAOUtility daoUtil = new DAOUtility();;
	/**
	 * Private constructor.
	 */
	private DAOUtility()
	{

	}
	/**
	 * returns the single object.
	 * @return Utility object.
	 */
	public static DAOUtility getInstance()
	{
		return daoUtil;
	}

	/**
     * logger Logger - Generic logger.
     */
      private static org.apache.log4j.Logger logger =
           Logger.getLogger(DAOUtility.class);
	 /**
     * Constants that will appear in HQL for retrieving Attributes of the Collection data type.
     */
    private static final String ELEMENTS = "elements";

    /**
     * To Create the attribute name for HQL select part.
     * If the  selectColumnName is in format "elements((attributeName))" then
     * it will return String as "elements(className.AttributeName)"
     * else it will return String in format "className.AttributeName"
     * @param className The className
     * @param selectColumnName The select column name passed to form HQL.
     * either in format "elements(attributeName)" or "AttributeName"
     * @return The Select column name for the HQL.
     */
    public String createAttributeNameForHQL(String className, String selectColumnName)
    {
		String attribute;
		// Check whether the select Column start with "elements" & ends with ")" or not
		if (isColumnNameContainsElements(selectColumnName))
		{
			int startIndex = selectColumnName.indexOf('(')+1;
			attribute =  selectColumnName.substring(0,startIndex) +
			className + "." + selectColumnName.substring(startIndex);
		}
		else
		{
			attribute =  className + "." + selectColumnName;
		}
		return attribute;
	}

	/**
	 * Check whether the select Column start with "elements" & ends with ")" or not.
	 * @param nameOfColumn The columnName
	 * @return true if the select Column start with "elements" & ends with ")" or not
	 */
	public boolean isColumnNameContainsElements(String nameOfColumn)
	{
		String columnName = nameOfColumn;
		columnName = columnName.toLowerCase().trim();
		return columnName.startsWith(ELEMENTS) && columnName.endsWith(")");
	}

	/**
     * Parses the fully qualified class Name and returns only the class Name.
     * @param fullyQualifiedName The fully qualified class Name.
     * @return The className.
     */
    public String parseClassName(String fullyQualifiedName)
    {
    	String qualifiedName = fullyQualifiedName;
        try
        {
        	qualifiedName = fullyQualifiedName.substring(fullyQualifiedName.
        			lastIndexOf(DAOConstants.DOT_OPERATOR) + 1);
        }
        catch (Exception e)
        {
        	logger.fatal("Problem while retrieving Fully Qualified class name.", e);
        }
        return qualifiedName;
    }

    /**
	 * @param tableName :
	 * @param connection :
	 * @return :
     * @throws DAOException :
	 */
	public String getDisplayName(String tableName,Connection connection) throws DAOException
	{
		DatabaseConnectionParams databaseConnectionParams = new DatabaseConnectionParams();
		String displayName="";
		String sql = "select DISPLAY_NAME from CATISSUE_QUERY_TABLE_DATA where TABLE_NAME='"+tableName+"'";
		try
		{
			databaseConnectionParams.setConnection(connection);
			ResultSet resultSet = databaseConnectionParams.getResultSet(sql);
			while(resultSet.next())
			{
				displayName=resultSet.getString("DISPLAY_NAME");
				break;
			}
			resultSet.close();
		}
		catch(Exception ex)
		{
			logger.error(ex.getMessage(),ex);
		}
		finally
		{
			databaseConnectionParams.closeConnectionParams();
		}
		return displayName;
	}

	/**
	 * Generates error messages.
	 * @param exep :
	 * @return error message.
	 */
	public String generateErrorMessage(Exception exep)
	{
		String messageToReturn = "";
		if (exep instanceof HibernateException)
        {
            HibernateException hibernateException = (HibernateException) exep;
            StringBuffer message = new StringBuffer(messageToReturn);
            String[] str = hibernateException.getMessages();
            if (str == null)
            {
            	messageToReturn = "Unknown Error";
            }
            else
            {
            	  for (int i = 0; i < str.length; i++)
                  {
                  	message.append(str[i]).append(DAOConstants.TAILING_SPACES);
                  }
                  messageToReturn =  message.toString();
            }

        }
        else
        {
        	messageToReturn = exep.getMessage();
        }
		  return messageToReturn;
	}




	/** This method add details of query in a list.
	 * @param queryName String name of query.
	 * @param values List of type Object object list.
	 * @param applicationName : applicationName
	 * @return Collection containing details of query.
	 * @throws DAOException :
	 */

	public static Collection<Object> executeHQL(String queryName, List<Object> values,
			String applicationName) throws DAOException
	{
		Session session = null;
		try
		{
			session = DAOConfigFactory.getInstance().
				getDAOFactory(applicationName).getDAO().
				getConnectionManager().currentSession();
			Query query = session.getNamedQuery(queryName);

			/*if (values != null)
			{
				for (int counter = 0; counter < values.size(); counter++)
				{
					Object value = values.get(counter);
					String onlyClassName = value.getClass().getSimpleName();
					if (String.class.getSimpleName().equals(onlyClassName))
					{
						query.setString(counter, (String) value);
					}
					else if (Integer.class.getSimpleName().equals(onlyClassName))
					{
						query.setInteger(counter, Integer.parseInt(value.toString()));
					}
					else if (Long.class.getSimpleName().equals(onlyClassName))
					{
						query.setLong(counter, Long.parseLong(value.toString()));
					}

				}
			}*/
			return query.list();
		}
		catch(HibernateException excp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,excp,"DAOUtility.java :");
		}
		finally
		{
			session.close();
		}
	}
	/**
	 * Return the output of execution of query.
	 * @param queryName String name of query.
	 * @param applicationName : applicationName
	 * @return Collection containing output of execution of query.
	 * @throws DAOException :
	 */
	public static Collection executeHQL(String queryName,String applicationName)
	throws DAOException
	{
		return executeHQL(queryName, null,applicationName);
	}

}
