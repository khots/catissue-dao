package edu.wustl.dao.util;

import java.sql.Connection;
import java.util.Map;

import edu.wustl.common.beans.QueryResultObjectDataBean;
import edu.wustl.common.beans.SessionDataBean;

/**
 * @author kalpana_thakur
 *TODO
 */
public class QueryParams
{

	/**
	 * query.
	 */
	private String query;
	/**
	 * connection.
	 */
	private Connection connection;
	/**
	 * sessionDataBean.
	 */
	private	SessionDataBean sessionDataBean;
	/**
	 * secureToExecute.
	 */
	private boolean secureToExecute;
	/**
	 * hasConditionOnIdentifiedField.
	 */
	private boolean hasConditionOnIdentifiedField;
	/**
	 * queryResultObjectDataMap.
	 */
	private Map<Object,QueryResultObjectDataBean> queryResultObjectDataMap;
	/**
	 * startIndex.
	 */
	private int startIndex;
	/**
	 * noOfRecords.
	 */
	private int noOfRecords;

	/**
	 * @return query
	 */
	public String getQuery()
	{
		return query;
	}
	/**
	 * @param query :
	 */
	public void setQuery(String query)
	{
		this.query = query;
	}
	/**
	 * @return connection
	 */
	public Connection getConnection()
	{
		return connection;
	}
	/**
	 * @param connection :
	 */
	public void setConnection(Connection connection)
	{
		this.connection = connection;
	}
	/**
	 * @return sessionDataBean
	 */
	public SessionDataBean getSessionDataBean()
	{
		return sessionDataBean;
	}
	/**
	 * @param sessionDataBean :
	 */
	public void setSessionDataBean(SessionDataBean sessionDataBean)
	{
		this.sessionDataBean = sessionDataBean;
	}
	/**
	 * @return secureToExecute
	 */
	public boolean isSecureToExecute()
	{
		return secureToExecute;
	}
	/**
	 * @param isSecureExecute :
	 */
	public void setSecureToExecute(boolean isSecureExecute)
	{
		this.secureToExecute = isSecureExecute;
	}
	/**
	 * @return hasConditionOnIdentifiedField
	 */
	public boolean isHasConditionOnIdentifiedField()
	{
		return hasConditionOnIdentifiedField;
	}
	/**
	 * @param hasConditionOnIdentifiedField :
	 */
	public void setHasConditionOnIdentifiedField(
			boolean hasConditionOnIdentifiedField)
	{
		this.hasConditionOnIdentifiedField = hasConditionOnIdentifiedField;
 	}
	/**
	 * @return queryResultObjectDataMap
	 */
	public Map<Object,QueryResultObjectDataBean> getQueryResultObjectDataMap()
	{
		return queryResultObjectDataMap;
	}
	/**
	 * @param queryResultObjectDataMap :
	 */
	public void setQueryResultObjectDataMap(Map<Object,QueryResultObjectDataBean> queryResultObjectDataMap)
	{
		this.queryResultObjectDataMap = queryResultObjectDataMap;
	}
	/**
	 * @return startIndex
	 */
	public int getStartIndex()
	{
		return startIndex;
	}
	/**
	 * @param startIndex :
	 */
	public void setStartIndex(int startIndex)
	{
		this.startIndex = startIndex;
	}
	/**
	 * @return noOfRecords
	 */
	public int getNoOfRecords()
	{
		return noOfRecords;
	}
	/**
	 * @param noOfRecords :
	 */
	public void setNoOfRecords(int noOfRecords)
	{
		this.noOfRecords = noOfRecords;
	}


}
