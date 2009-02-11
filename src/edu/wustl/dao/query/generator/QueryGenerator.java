package edu.wustl.dao.query.generator;


/**
 * @author kalpana_thakur
 *
 */
public interface QueryGenerator
{

	/**
	 * @return :
	 */
	String getInsertQuery();

	/**
	 * @return :
	 */
	String getUpdateQuery();

	/**
	 * @param queryData queryData.
	 */
	void setQueryData(QueryData queryData);

}
