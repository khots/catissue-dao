package edu.wustl.dao.util;
import java.util.ResourceBundle;

import edu.wustl.common.util.global.TextConstants;
import edu.wustl.common.util.logger.Logger;


public class DaoProperties
{

	
	
	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(DaoProperties.class);

	private static ResourceBundle bundle;
	static
	{
		try
		{
			bundle = ResourceBundle.getBundle("Dao");
			logger.debug("File Loaded !!!");
		}
		catch (Exception e)
		{
			logger.error(e.getMessage() + " " + e);
		}
	}
	
	/*public DaoProperties()
	{
		// TODO Auto-generated constructor stub
	}
	
	DaoProperties (IConnectionManager icon)
	{
//		System.out.println(" DaoProperties :  >>>"+icon);
		//System.out.println("-----" +icon.getGenericInterfaces());
	}*/


	/**
	 * @param theKey key in a application property file
	 * @return the value of key.
	 */
	public static String getValue(String theKey)
	{
		String val = TextConstants.EMPTY_STRING;
		if (bundle == null)
		{
			logger.fatal("Resource bundle is null cannot return value for key " + theKey);
		}
		else
		{
			val = bundle.getString(theKey);
		}
		return val;
	}
	
	
	
	public static void main (String[] argv)
	{
		//text
	}


	
}
