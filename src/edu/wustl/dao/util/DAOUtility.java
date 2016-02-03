/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/catissue-dao/LICENSE.txt for details.
 */

/**
 * <p>Title: DAOUtility Class>
 * <p>Description:	DAOUtility class holds utility methods for DAO .</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @version 1.00
 * @author kalpana_thakur
 */
package edu.wustl.dao.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
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
      private static Logger logger =
           Logger.getCommonLogger(DAOUtility.class);
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
	 * @throws DAOException database exception
     */
    public String parseClassName(String fullyQualifiedName) throws DAOException
    {
    	String qualifiedName = fullyQualifiedName;
        try
        {
        	qualifiedName = fullyQualifiedName.substring(fullyQualifiedName.
        			lastIndexOf(DAOConstants.DOT_OPERATOR) + 1);
        }
        catch (Exception exp)
        {
        	logger.fatal("Problem while retrieving Fully Qualified class name.", exp);
        	throw DAOUtility.getInstance().getDAOException(exp, "db.retrieve.data.error",
			"DAOUtility.java ");
        }
        return qualifiedName;
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
			throw DAOUtility.getInstance().getDAOException(excp,
					"db.update.data.error","DAOUtility.java :");
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
			throw DAOUtility.getInstance().getDAOException(exp,
        			"db.retrieve.data.error", "DatabaseConnectionParams.java ");
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
				NamedQueryParam queryParam = namedQueryParams.get(counter+"");

				int objectType = queryParam.getType();
				if ( DBTypes.STRING == objectType)
				{
					query.setString(counter, queryParam.getValue().toString());
				}
				else if (DBTypes.INTEGER == objectType)
				{
					query.setInteger(counter, Integer.parseInt
							(queryParam.getValue().toString()));
				}
				else if (DBTypes.LONG == objectType)
				{
					query.setLong(counter, Long.parseLong(queryParam.getValue().toString()));
				}
				else if (DBTypes.BOOLEAN == objectType)
				{
					query.setBoolean(counter,
							Boolean.parseBoolean(queryParam.getValue().toString()));
				}
				else if (DBTypes.DOUBLE == objectType)
				{
					query.setDouble(counter,
							Double.parseDouble(queryParam.getValue().toString()));
				}
			}
		}
	}

    /**
     * @param query queryObject
     * @param namedQueryParams : Query parameters to set
     * @throws SQLException 
     */
    public void substitutionParameterForQuery(PreparedStatement query, Map<String, NamedQueryParam> namedQueryParams)
            throws SQLException
    {
        if (namedQueryParams != null && !namedQueryParams.isEmpty())
        {
            for (int counter = 1; counter <= namedQueryParams.size(); counter++)
            {
                NamedQueryParam queryParam = namedQueryParams.get(counter + "");

                int objectType = queryParam.getType();
                if (DBTypes.STRING == objectType)
                {
                    query.setString(counter, queryParam.getValue().toString());
                }
                else if (DBTypes.INTEGER == objectType)
                {
                    query.setInt(counter, Integer.parseInt(queryParam.getValue().toString()));
                }
                else if (DBTypes.LONG == objectType)
                {
                    query.setLong(counter, Long.parseLong(queryParam.getValue().toString()));
                }
                else if (DBTypes.BOOLEAN == objectType)
                {
                    query.setBoolean(counter, Boolean.parseBoolean(queryParam.getValue().toString()));
                }
                else if (DBTypes.DOUBLE == objectType)
                {
                    query.setDouble(counter, Double.parseDouble(queryParam.getValue().toString()));
                }
                else if (DBTypes.DATE == objectType)
                {
                    query.setDate(counter, (java.sql.Date) queryParam.getValue());
                }
                else
                {
                    query.setObject(counter, queryParam.getValue());
                }
            }
        }
    }

	/**
	 * @param exception : DAOException thrown
	 * @param errorName : error key
	 * @param msgValues : Error message
	 * @return the DAOException instance.
	 */
	public DAOException getDAOException(Exception exception,String errorName, String msgValues)
	{
		if(exception != null)
		{
			logger.debug(exception.getMessage(),exception);
		}
		return new DAOException(ErrorKey.getErrorKey(errorName),exception,msgValues);

	}

	/**
	 * This method is used to get the token from the string.
	 * @param string string
	 * @param tokenSize size
	 * @return token
	 */
	public String getToken(String string , int tokenSize)
	{
		String tempString = string.trim();
		char[] dst = new char[tokenSize];
		tempString.getChars(0, tokenSize, dst, 0);
		return new String(dst);
	}

	/**
	 * This method will be called to check for invalid/malicious data.
	 * @param sql : Query having '?' as parameters
	 * @param beans : having column name, value and column type.
	 * @throws DAOException : database exception.
	 */
	public static void checkforInvalidData(String sql,
			List<ColumnValueBean> beans) throws DAOException
	{
		Iterator<ColumnValueBean> beansIter = beans.iterator();
		while(beansIter.hasNext())
		{
			ColumnValueBean bean = beansIter.next();
			for(int counter =0; counter < DAOConstants.INVALID_DATA.length;counter++)
			{
				if(bean.getColumnValue() instanceof String &&
					(bean.getColumnValue().toString()).trim()
					.toLowerCase()
					.contains(DAOConstants.INVALID_DATA[counter].trim().toLowerCase()))
				{

					logger.error("SQl : "+sql+
						"  Invalid data : "+ bean.getColumnValue().toString()+
				    	" Encountered invalid character:" +
				    	DAOConstants.INVALID_DATA[counter]);
					throw DAOUtility.getInstance().getDAOException(null,
					"db.malicious.data.encountered",bean.getColumnValue().toString()+
					":"+DAOConstants.INVALID_DATA[counter]);
				}
			}
		}
	}

	/**
	 * This method will be called to look for invalid data.
	 * @param dataValue dataValue
	 * @throws DAOException database exception.
	 */
	public static void checkforInvalidData(Object dataValue) throws DAOException
	{
		for(int counter =0; counter < DAOConstants.INVALID_DATA.length;counter++)
		{
			if(dataValue instanceof String &&
				(dataValue.toString()).trim()
				.toLowerCase()
				.contains(DAOConstants.INVALID_DATA[counter].trim().toLowerCase()))
			{

				logger.error("  Invalid data : "+ dataValue.toString()+
			    	" Encountered invalid character:" +
			    	DAOConstants.INVALID_DATA[counter]);
				throw DAOUtility.getInstance().getDAOException(null,
				"db.malicious.data.encountered",dataValue.toString()+
				":"+DAOConstants.INVALID_DATA[counter]);
			}
		}
	}

	public static Date getStartTimeForTodaysDate(String startTime)
	{
		int hours = 0, minutes = 0;
		if (startTime != null && startTime.length() == 5
		&& startTime.matches("([0-1][0-9]|2[0-3]):([0-5][0-9])"))
		{
			String timeTokens[] = startTime.split(DAOConstants.COLON);
			hours = Integer.parseInt(timeTokens[0]);
			minutes = Integer.parseInt(timeTokens[1]);
		}

		Calendar startDate = Calendar.getInstance();
		Calendar today = Calendar.getInstance();
		today.set(startDate.get(Calendar.YEAR),startDate.get(Calendar.MONTH), startDate.get(Calendar.DATE),	hours, minutes);
		if (today.before(startDate))
		{
			today.add(Calendar.DATE, 1);
		}
		return today.getTime();
	}

}
