/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/catissue-dao/LICENSE.txt for details.
 */

/**
 * <p>Title: DAOConfigFactory class>
 * <p>Description: DAOConfigFactory is used to create DAOFactory objects as per the applications
 * It creates a Map having key as the application name and value as DAOFactory object
 * It reads the attribute default of ApplicationDAOProperties.xml and sets the Default DAOFactory object</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @version 1.0
 * @author kalpana_thakur
 */
package edu.wustl.dao.daofactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.wustl.common.util.logger.Logger;



/**
 * @author kalpana_thakur
 * It creates a Map having key as the application name and value as DAOFactory object
 * It reads the attribute default of ApplicationDAOProperties.xml and sets the Default DAOFactory object.
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
	 *Key of the Map is application name and it holds IDAOFactory type Object.
	 *TODO
	 */
	private static Map<String, IDAOFactory> daoFactoryMap = new HashMap<String, IDAOFactory>();

	static
	{
		daoConfigurationFactory = new DAOConfigFactory();
	}

	/**
	 * Constructor.
	 * It will call populateDaoFactoryMap which parses the
	 * ApplicationDAOProperties.xml and populates the Map.
	 */
	public DAOConfigFactory()
	{
		populateDaoFactoryMap();
	}

	/**
	 * It returns the daoConfigrationFactory instance.
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
	 * Default factory object will be set as per the default attribute of
	 * ApplicationDAOProperties.xml.
	 * @return default DAO factory object.
	 */
	public IDAOFactory getDAOFactory()
	{
		return defaultDAOFactory;
	}


	/**
	 * This method will parse the Application property file.
	 * It will create the Map having application name as key and DAO factory object as a value
	 * and it also sets default DAO factory object.
	 */
	public static void populateDaoFactoryMap()
	{
		try
		{
			ApplicationDAOPropertiesParser applicationPropertiesParser =
				new ApplicationDAOPropertiesParser();
			daoFactoryMap = applicationPropertiesParser.getDaoFactoryMap();
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
