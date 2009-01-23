package edu.wustl.dao;

/**
 * @author kalpana_thakur
 */
public class DatabaseProperties
{

	private String dataBaseName;
	private String datePattern;
	private String timePattern;
	private String dateFormatFunction;
	private String timeFormatFunction;
	private String dateTostrFunction;
	private String strTodateFunction;
	private String dataSource;
	
	private String exceptionFormatterName;
	

	private String queryExecutorName;

	public String getDataBaseName()
	{
		return dataBaseName;
	}

	public void setDataBaseName(String dataBaseName)
	{
		this.dataBaseName = dataBaseName;
	}

	
	public String getExceptionFormatterName() 
	{
		return exceptionFormatterName;
	}

	public String getQueryExecutorName()
	{
		return queryExecutorName;
	}

	public void setExceptionFormatterName(String exceptionFormatterName)
	{
		this.exceptionFormatterName = exceptionFormatterName;
	}

	public void setQueryExecutorName(String queryExecutorName) {
		this.queryExecutorName = queryExecutorName;
	}
	
	
	/**
	 * @return :This will return the Date Pattern.
	 */
	public String getDatePattern()
	{
		return datePattern;
	}

	/**
	 * @return :This will return the Time Pattern.
	 */
	public String getTimePattern()
	{
		return timePattern;
	}
	/**
	 * @return :This will return the Date Format Function.
	 */
	public String getDateFormatFunction()
	{
		return dateFormatFunction;
	}
	/**
	 * @return :This will return the Time Format Function.
	 */
	public String getTimeFormatFunction()
	{
		return timeFormatFunction;
	}

	/**
	 * @return :This will return the Date to string function
	 */
	public String getDateTostrFunction()
	{
		return dateTostrFunction;
	}
	/**
	 * @return :This will return the string to Date function
	 */
	public String getStrTodateFunction()
	{

		return strTodateFunction;
	}

	public void setDatePattern(String datePattern) 
	{
		this.datePattern = datePattern;
	}

	public void setTimePattern(String timePattern)
	{
		this.timePattern = timePattern;
	}

	public void setDateFormatFunction(String dateFormatFunction)
	{
		this.dateFormatFunction = dateFormatFunction;
	}

	public void setTimeFormatFunction(String timeFormatFunction)
	{
		this.timeFormatFunction = timeFormatFunction;
	}

	public void setDateTostrFunction(String dateTostrFunction)
	{
		this.dateTostrFunction = dateTostrFunction;
	}

	public void setStrTodateFunction(String strTodateFunction)
	{
		this.strTodateFunction = strTodateFunction;
	}

	 /**
	 * This method will be called to get the data source.
	 * @return dataSource
	 */
	 public String getDataSource()
	 {
		 return dataSource;
	 }

	 /**
	 * This method will be called to set the data source.
	 * @param dataSource : JDBC connection name.
	 */
	 public void setDataSource(String dataSource)
	 {
		 this.dataSource = dataSource;
	 }
}
