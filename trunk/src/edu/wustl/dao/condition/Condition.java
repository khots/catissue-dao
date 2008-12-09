/*
 * TODO
 */
package edu.wustl.dao.condition;

/**
 * @author kalpana_thakur
 * TODO
 */
public interface Condition
{



	/**
	 * @return class name or table name.
	 */
	String getSourceObjectName();

	/**
	 * @param sourceObjectName set the class name or table name.
	 */
	void setSourceObjectName(String sourceObjectName);



	/**
	 * Returns the string value.
	 * @return String:
	 */
	String buildSql();

}
