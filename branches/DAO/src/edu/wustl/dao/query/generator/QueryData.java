package edu.wustl.dao.query.generator;

import edu.wustl.dao.QueryWhereClause;

/**
 * @author kalpana_thakur
 *
 */
public class QueryData
{

  private ColumnValueBean colValBean;
  private String tableName;
  private QueryWhereClause queryWhereClause;
  
  public ColumnValueBean getColValBean()
  {
		return colValBean;
  }
  public String getTableName()
  {
		return tableName;
   }
   public QueryWhereClause getQueryWhereClause()
   {
		return queryWhereClause;
   }
   public void setColValBean(ColumnValueBean colValBean)
   {
		this.colValBean = colValBean;
   }
   public void setTableName(String tableName)
   {
		this.tableName = tableName;
   }
   public void setQueryWhereClause(QueryWhereClause queryWhereClause)
   {
		this.queryWhereClause = queryWhereClause;
   }

}
