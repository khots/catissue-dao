/**
 * <p>Title: DAOUtility Class>
 * <p>Description:	DAOUtility class holds utility methods for DAO .</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @version 1.00
 * @author kalpana_thakur
 */
package edu.wustl.dao.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.DBTypes;


/**
 * @author kalpana_thakur
 * This class holds utility methods for DAO.
 */
public final class DAOUtility
{
	/**
	 * GET_PARAMETERIZED_QUERIES_DETAILS String details of parameterized queries.
	 */
	 public static final String GET_PARAM_QUERIES_DETAILS = "getParameterizedQueriesDetails";

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
	 * @param jdbcDAO :
	 * @return :
     * @throws DAOException :
	 */
	public String getDisplayName(String tableName,JDBCDAO jdbcDAO) throws DAOException
	{
		String displayName="";
		String sql = "select DISPLAY_NAME from CATISSUE_QUERY_TABLE_DATA where TABLE_NAME='"+tableName+"'";
		try
		{
			ResultSet resultSet = jdbcDAO.getQueryResultSet(sql);
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
                  	message.append(str[i]).append(DAOConstants.TRAILING_SPACES);
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
	 * @return Collection containing details of query.
	 * @throws DAOException :
	 * TODO : need to check this method.have to remove this
	 */

	public Collection<Object> executeHQL(String queryName, List<Object> values) throws DAOException
	{
		DAO dao = null;
		try
		{
			String appName = CommonServiceLocator.getInstance().getAppName();
			dao = DAOConfigFactory.getInstance().
			getDAOFactory(appName).getDAO();
			dao.openSession(null);
			return((HibernateDAO)dao).executeNamedQuery(queryName,null);

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
		}
		catch(HibernateException excp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,excp,"DAOUtility.java :");
		}
		finally
		{
			dao.closeSession();
		}
	}
	/**
	 * Return the output of execution of query.
	 * @param queryName String name of query.
	 * @return Collection containing output of execution of query.
	 * @throws DAOException :
	 */
	public Collection executeHQL(String queryName)
	throws DAOException
	{
		return executeHQL(queryName, null);
	}

	/**
	 * Returns the list from RS.
	 * @param resultSet :RS
	 * @return :List
	 * @throws SQLException :
	 */
	public List getListFromRS(ResultSet resultSet)throws SQLException
	{
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();
		List list = new ArrayList();
		while (resultSet.next())
		{
			updateList(resultSet,list,columnCount,metaData);
		}

		return list;
	}

	/**
	 * This method will read the resultSet and update the list.
	 * @param list :list of data
	 * @param columnCount : number of columns
	 * @param metaData : meta data
	 * @param resultSet : resultSet
	 * @throws SQLException : exception
	 */
	private void updateList(ResultSet resultSet,List list,
			int columnCount,ResultSetMetaData metaData) throws SQLException
	{
		List rowDataList = new ArrayList();
		for (int i = 1; i <= columnCount; i++)
		{
			Object retObj;
			switch (metaData.getColumnType(i))
			{
			case Types.CLOB :
				retObj = resultSet.getObject(i);
				break;
			case Types.DATE :
			case Types.TIMESTAMP :
				retObj = resultSet.getTimestamp(i);
				if (retObj == null)
				{
					break;
				}
				SimpleDateFormat formatter = new SimpleDateFormat(
						DAOConstants.DATE_PATTERN_MM_DD_YYYY + " "
						+ DAOConstants.TIME_PATTERN_HH_MM_SS);
				retObj = formatter.format((java.util.Date) retObj);
				break;
			default :
				retObj = resultSet.getObject(i);
				if (retObj != null)
				{
					retObj = retObj.toString();
				}
			}
			if (retObj == null)
			{
				rowDataList.add("");
			}
			else
			{
				rowDataList.add(retObj);
			}
		}
		list.add(rowDataList);
	}
	/**
	 * Checks result set.
	 * @return :true if result set exists.
	 * @param resultSet : query resultSet
	 * @throws DAOException : DAOException
	 */
	public boolean isResultSetExists(ResultSet resultSet)throws DAOException
	{
		boolean isResultSetExists = false;
		try
		{

			if(resultSet.next())
			{
				isResultSetExists = true;
			}

		}
		catch(SQLException exp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"DatabaseConnectionParams.java :"+
					DAOConstants.RS_METADATA_ERROR);
		}
		return isResultSetExists;
	}

	/**
	 * @param query queryObject
	 * @param namedQueryParams : Query parameters to set
	 */
	public void substitutionParameterForQuery(Query query,
		Map<String, NamedQueryParam> namedQueryParams)
	{
		if(namedQueryParams != null && !namedQueryParams.isEmpty())
		{
			for (int counter = 0; counter < namedQueryParams.size(); counter++)
			{
				NamedQueryParam queryParam = (NamedQueryParam) namedQueryParams
						.get(counter + "");
				int objectType = queryParam.getType();
				if ( DBTypes.STRING == objectType)
				{
					query.setString(counter, queryParam.getValue() + "");
				}
				else if (DBTypes.INTEGER == objectType)
				{
					query.setInteger(counter, Integer.parseInt(queryParam.getValue() + ""));
				}
				else if (DBTypes.LONG == objectType)
				{
					query.setLong(counter, Long.parseLong(queryParam.getValue() + ""));
				}
				else if (DBTypes.BOOLEAN == objectType)
				{
					query.setBoolean(counter,
							Boolean.parseBoolean(queryParam.getValue() + ""));
				}
			}
		}
	}



}
