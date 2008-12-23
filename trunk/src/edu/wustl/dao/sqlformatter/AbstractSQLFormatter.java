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
	 * @param columnValueBean :
	 */
	public void addColValBean(ColumnValueBean columnValueBean)
	{
		colValBeanColl.add(columnValueBean);
	}

	/**
	 * @see edu.wustl.dao.sqlformatter.SQLFormatter#insertQuery(java.lang.String)
	 * @param tableName :
	 * @return :
	 */
	public String insertQuery(String tableName)
	{

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
	}

	/**
	 * @param colValBean :
	 * @param valuePart :
	 */
	protected abstract void appendColumnValue(ColumnValueBean colValBean,StringBuffer valuePart);


	/**
	 * @param tableName :
	 * @return :
	 */
	public String updateQuery(String tableName)
	{

			return null;
	}



}
