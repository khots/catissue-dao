/*
 * TODO
 */
package edu.wustl.dao.daofactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.wustl.common.util.logger.Logger;



/**
 * @author kalpana_thakur
 *
 */
public class DAOConfigFactory
{
	/**
	 * Singleton instance.
	 */
	private static DAOConfigFactory daoConfigurationFactory;
	/**
	 *This will hold the default DAO factory.
	 */
	private static IDAOFactory defaultDAOFactory;
	/**
	 *Class Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(DAOConfigFactory.class);
	/**
	 *This Map holds the DAO factory object as per the application.
	 *TODO
	 */
	private static Map<String, IDAOFactory> daoFactoryMap = new HashMap<String, IDAOFactory>();

	static
	{
		daoConfigurationFactory = new DAOConfigFactory();
	}

	/**
	 * Constructor.
	 */
	public DAOConfigFactory()
	{
		populateDaoFactoryMap();
	}

	/**
	 * Getter method in singleton class is to setup mock unit testing.
	 * @return factory
	 */
	public static DAOConfigFactory getInstance()
	{
		return daoConfigurationFactory;
	}

	/**
	 * @param applicationName :Name of the application
	 * @return DAO factory object.
	 */
	public IDAOFactory getDAOFactory(String applicationName)
	{
		return (IDAOFactory)daoFactoryMap.get(applicationName);
	}

	/**
	 * @return default DAO factory object.
	 */
	public IDAOFactory getDAOFactory()
	{
		return defaultDAOFactory;
	}


	/**
	 * This method will parse the Application property file.
	 * It will create the Map having application name as key and DAO factory object as a value.
	 */
	public static void populateDaoFactoryMap()
	{
		try
		{
			ApplicationDAOPropertiesParser applicationPropertiesParser =
				new ApplicationDAOPropertiesParser();
			daoFactoryMap = applicationPropertiesParser.getDaoFactoryMap();
			/*TODO
			 * Is this right approach ...have to confirm with abhijit
			 * or we can keep default field in xml doc and save it
			 * in Map with key as defaul tapplication.
			 */
			Iterator<String> mapKeySetIterator = daoFactoryMap.keySet().iterator();
			while(mapKeySetIterator.hasNext())
			{
				IDAOFactory daoFactory = (IDAOFactory)daoFactoryMap.get(mapKeySetIterator.next());
				if(daoFactory.getIsDefaultDAOFactory())
				{
					defaultDAOFactory = daoFactory;
				}
			}
		}
		catch (Exception expc)
		{
			logger.error(expc.getMessage(), expc);
		}
	}


}
