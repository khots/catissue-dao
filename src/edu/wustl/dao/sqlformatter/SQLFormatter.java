/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/catissue-dao/LICENSE.txt for details.
 */

package edu.wustl.dao.sqlformatter;

import java.util.Collection;

/**
 * @author kalpana_thakur
 *
 */
public interface SQLFormatter
{
	/**
	 *@param columnValueBean :
	 *@return SQLFormatter :
	 */
	SQLFormatter addColValBean(ColumnValueBean columnValueBean);

	/**
	 *@return collection :
	 */
	Collection<ColumnValueBean> getColValBeans();

	/**
	 * @return :
	 */
	String getInsertQuery();

	/**
	 * @return :
	 */
	String getUpdateQuery();

	/**
	 * @return tableName :
	 */
	String getTableName();

}
