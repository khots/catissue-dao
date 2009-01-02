package edu.wustl.dao.sqlformatter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import edu.wustl.dao.util.DAOConstants;

/**
 * @author kalpana_thakur
 *
 */
public abstract class AbstractSQLFormatter implements SQLFormatter
{


	/**
	 *
	 */
	protected Collection<ColumnValueBean> colValBeanColl
	= new HashSet<ColumnValueBean>();


	/**
	 *
	 */
	protected String tableName;

	/**
	 * @param tableName :
	 */
	public AbstractSQLFormatter(String tableName)
	{
		this.tableName = tableName;
	}


	/**
	 * @param columnValueBean :
	 * @return SQLFormatter :
	 */
	public SQLFormatter addColValBean(ColumnValueBean columnValueBean)
	{
		colValBeanColl.add(columnValueBean);
		return this;
	}

	/**
	 * @see edu.wustl.dao.sqlformatter.SQLFormatter#getInsertQuery(java.lang.String)
	 * @return :
	 */
	public String getInsertQuery()
	{/*

		boolean isColumnMoreThnOne = false;
		StringBuffer insertSql = new StringBuffer(DAOConstants.TAILING_SPACES);
		StringBuffer valuePart = new StringBuffer(DAOConstants.TAILING_SPACES);
		insertSql.append("insert into").append(DAOConstants.TAILING_SPACES).append(tableName).append(" (");
		valuePart.append("values (");
		Iterator<ColumnValueBean> colValBeanItr =  colValBeanColl.iterator();
		while(colValBeanItr.hasNext())
		{
			ColumnValueBean colValBean = colValBeanItr.next();
			insertSql.append(colValBean.getColumnName()).append(DAOConstants.SPLIT_OPERATOR);

			if(isColumnMoreThnOne)
			{
				valuePart.append(DAOConstants.SPLIT_OPERATOR);
				isColumnMoreThnOne = true;
			}
			appendColumnValue(colValBean,valuePart);
		}
		insertSql.append(" )");
		valuePart.append(" )");
		insertSql.append(valuePart.toString());

		return insertSql.toString();
	*/
		return null;
	}

	/**
	 * @param colValBean :
	 * @param valuePart :
	 */
	protected abstract void appendColumnValue(ColumnValueBean colValBean,StringBuffer valuePart);


	/**
	 * @return :
	 */
	public String getUpdateQuery()
	{

			return "";
	}

	/**

	 * @param colValue :
	 * @param valuePart :
	 */
	protected void appendStringValue(Object colValue,StringBuffer valuePart)
	{
		valuePart.append("' ").append(colValue).append("' ");

	}

	/**

	 * @param colValue :
	 * @param valuePart :
	 */
	protected void appendNumericValue(Object colValue,StringBuffer valuePart)
	{
		valuePart.append(colValue);

	}

	/**
	 * @return :
	 */
	public String getTableName()
	{
		return tableName;
	}

	/**
	 * @param tableName :
	 */

	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	/**
	 *@return collection :
	 */
	public Collection<ColumnValueBean> getColValBeans()
	{
		return colValBeanColl;
	}



}
