package edu.wustl.dao.query.generator;

import java.util.Iterator;

import edu.wustl.dao.util.DAOConstants;

/**
 * @author kalpana_thakur
 *
 */
public abstract class AbstractQueryGenerator implements QueryGenerator
{


	/**
	 * QueryData.
	 */
	protected QueryData queryData;

	/**
	 * @param queryData : queryData
	 *//*
	public AbstractQueryGenerator(QueryData queryData)
	{
		this.queryData = queryData;
	}*/

	/**
	 * @see edu.wustl.dao.query.generator.QueryGenerator#getInsertQuery(java.lang.String)
	 * @return :
	 */
	public String getInsertQuery()
	{

		StringBuffer insertSql = new StringBuffer(DAOConstants.TAILING_SPACES);
		StringBuffer valuePart = new StringBuffer(DAOConstants.TAILING_SPACES);
		insertSql.append("insert into").append(DAOConstants.TAILING_SPACES).
		append(queryData.getTableName()).append(" (");
		valuePart.append("values (");
		Iterator<ColumnValueBean> colValBeanItr = queryData.getColumnValueBeans().iterator();
		while(colValBeanItr.hasNext())
		{
			ColumnValueBean colValBean = colValBeanItr.next();
			insertSql.append(colValBean.getColumnName());
			valuePart.append(fetchColumnValue(colValBean));
			if(colValBeanItr.hasNext())
			{
				insertSql.append(DAOConstants.SPLIT_OPERATOR);
				valuePart.append(DAOConstants.SPLIT_OPERATOR);
			}

		}
		insertSql.append(" )");
		valuePart.append(" )");
		insertSql.append(valuePart.toString());

		return insertSql.toString();
	}

	/**
	 * @param colValBean :
	 * @return Object :
	 */
	protected abstract Object fetchColumnValue(ColumnValueBean colValBean);

	/**
	 * @return update query.
	 */
	public String getUpdateQuery()
	{
		StringBuffer updateSql = new StringBuffer(DAOConstants.TAILING_SPACES);
		updateSql.append("update").append(DAOConstants.TAILING_SPACES).append(queryData.getTableName()).
		append(DAOConstants.TAILING_SPACES).append("set").append(DAOConstants.TAILING_SPACES);

		Iterator<ColumnValueBean> colValBeanItr = queryData.getColumnValueBeans().iterator();
		while(colValBeanItr.hasNext())
		{
			ColumnValueBean colValBean = colValBeanItr.next();
			updateSql.append(colValBean.getColumnName()).append(DAOConstants.EQUAL)
			.append(fetchColumnValue(colValBean));
			if(colValBeanItr.hasNext())
			{
				updateSql.append(DAOConstants.SPLIT_OPERATOR);
			}
		}
		updateSql.append(queryData.getQueryWhereClause().toWhereClause());
		return updateSql.toString();
	}


	/**
	 * @param queryData : queryData
	 */
	public void setQueryData(QueryData queryData)
	{
		this.queryData = queryData;
	}

	/*public static void main(String[] args)
	{
		QueryData queryData = new QueryData();
		queryData.setTableName("Temp");

		QueryWhereClause queryWhereClause = new QueryWhereClause("Temp");
		queryWhereClause.addCondition(new EqualClause("identifier",1));

		queryData.setQueryWhereClause(queryWhereClause);
		queryData.addColValBean(new ColumnValueBean("name","kt",DBTypes.STRING)).
		addColValBean(new ColumnValueBean("date","5feb09",DBTypes.DATE));
		AbstractQueryGenerator query = new OracleQueryGenerator();
		query.setQueryData(queryData);
		System.out.println("-----"+query.getUpdateQuery());
		System.out.println("-----"+query.getInsertQuery());
	}*/

}
