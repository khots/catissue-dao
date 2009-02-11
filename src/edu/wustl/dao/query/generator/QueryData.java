package edu.wustl.dao.query.generator;

import java.util.Collection;
import java.util.HashSet;

import edu.wustl.dao.QueryWhereClause;


/**
 * @author kalpana_thakur
 *
 */
public class QueryData
{
	/**
	 *
	 */
   private Collection<ColumnValueBean> colValBeans
	= new HashSet<ColumnValueBean>();

  /**
   *	tableName.
   */
  private String tableName;

  /**
   *	queryWhereClause.
   */
  private QueryWhereClause queryWhereClause;


  /**
   * @return get table name.
   */
  public String getTableName()
  {
		return tableName;
   }
  /**
   * @return get queryWhereClause
   */
   public QueryWhereClause getQueryWhereClause()
   {
		return queryWhereClause;
   }

   /**
    *@param tableName name of table.
    */
   public void setTableName(String tableName)
   {
		this.tableName = tableName;
   }
   /**
    *@param queryWhereClause queryWhereClause
    */
   public void setQueryWhereClause(QueryWhereClause queryWhereClause)
   {
		this.queryWhereClause = queryWhereClause;
   }

   /**
	 *@param columnValueBean :
	 *@return SQLFormatter :
	 */
   public QueryData addColValBean(ColumnValueBean columnValueBean)
   {
	   colValBeans.add(columnValueBean);
		return this;
   }

   /**
    * @return column value beans.
    */
   public Collection<ColumnValueBean> getColumnValueBeans()
   {
	   return colValBeans;
   }

}
