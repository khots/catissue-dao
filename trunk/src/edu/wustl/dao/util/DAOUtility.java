package edu.wustl.dao.util;

import edu.wustl.common.util.logger.Logger;


/**
 * @author kalpana_thakur
 *
 */
public final class DAOUtility
{

	/**
	 * creates a single obj.
	 */
	private static DAOUtility daoUtil = new DAOUtility();;
	/**
	 * Private constructor.
	 */
	private DAOUtility()
	{

	}
	/**
	 * returns the single obj.
	 * @return Utility obj
	 */
	public static DAOUtility getInstance()
	{
		return daoUtil;
	}

	/**
     * logger Logger - Generic logger.
     */
      private static org.apache.log4j.Logger logger =
           Logger.getLogger(DAOUtility.class);
	 /**
     * Constants that will appear in HQL for retreiving Attributes of the Collection data type.
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
     */
    public String parseClassName(String fullyQualifiedName)
    {
    	String qualifiedName = fullyQualifiedName;
        try
        {
        	qualifiedName = fullyQualifiedName.substring(fullyQualifiedName.
        			lastIndexOf(DAOConstants.DOT_OPERATORS) + 1);
        }
        catch (Exception e)
        {
        	logger.fatal("Problem while retrieving Fully Qualified class name.", e);
        }
        return qualifiedName;
    }

}
