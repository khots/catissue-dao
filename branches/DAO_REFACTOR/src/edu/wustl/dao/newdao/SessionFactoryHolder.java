
package edu.wustl.dao.newdao;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.io.DOMWriter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.util.XMLHelper;
import org.xml.sax.InputSource;

import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.daofactory.ApplicationDAOPropertiesParser;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.util.HibernateMetaDataFactory;

public class SessionFactoryHolder
{

	/**
	* LOGGER Logger - class logger.
	*/
	private static final Logger logger = Logger.getCommonLogger(SessionFactoryHolder.class);
	
	private Map<String, SessionFactory> sessionFactoryMap;

	private static SessionFactoryHolder sessionFactoryHolderInstance = new SessionFactoryHolder();
	
	private SessionFactoryHolder()
	{
		try
		{
			initialize(initializeConfigurationMap());
		}
		catch (Exception expc)
		{
			logger.error(expc.getMessage(), expc);
		}
	}

	public static synchronized SessionFactoryHolder getInstance()
	{
		if(sessionFactoryHolderInstance==null)
		{
			sessionFactoryHolderInstance = new SessionFactoryHolder();
		}
		return sessionFactoryHolderInstance;
	}
	
//	public SessionFactoryHolder(Map<String, String> hbmCfgsMap) throws DAOException
//	{
//		initialize(hbmCfgsMap);
//	}

	public SessionFactory getSessionFactory(String name)
	{
		return sessionFactoryMap.get(name);
	}

	private void initialize(Map<String, String> hbmCfgsMap) throws Exception
	{
		for (Map.Entry<String, String> entry : hbmCfgsMap.entrySet())
		{
			createSessionFactory(entry.getKey(), entry.getValue());
		}
	}

	private void createSessionFactory(String applicationName, String hbmCfg) throws Exception
	{
		Configuration configuration = createConfiguration(hbmCfg);
		SessionFactory sessionFactory = configuration.buildSessionFactory();
		HibernateMetaDataFactory.setHibernateMetaData(applicationName,
				configuration);
		sessionFactoryMap.put(applicationName, sessionFactory);
	}
	
	private Configuration createConfiguration(String configurationfile) throws Exception
	{

		Configuration configuration = new Configuration();
		InputStream inputStream = Thread.currentThread().getContextClassLoader().
		getResourceAsStream(configurationfile);
	    List<Object> errors = new ArrayList<Object>();
	    // hibernate api to read configuration file and convert it to
	    // Document(dom4j) object.
	    XMLHelper xmlHelper = new XMLHelper();
	    Document document = xmlHelper.createSAXReader(configurationfile, errors, XMLHelper.DEFAULT_DTD_RESOLVER).read(
	            new InputSource(inputStream));
	    // convert to w3c Document object.
	    DOMWriter writer = new DOMWriter();
	    org.w3c.dom.Document doc = writer.write(document);
	    // configure
	    configuration.configure(doc);
	    return configuration;
  
	}
	
	private Map<String, String> initializeConfigurationMap() throws Exception
	{
		Map<String, String> configurationMap = new HashMap<String, String>();
		ApplicationDAOPropertiesParser applicationPropertiesParser =
			new ApplicationDAOPropertiesParser();
		Map<String, IDAOFactory> daoFactoryMap = applicationPropertiesParser.getDaoFactoryMap();
		
		Iterator<String> mapKeySetIterator = daoFactoryMap.keySet().iterator();
		while(mapKeySetIterator.hasNext())
		{
			String appName = mapKeySetIterator.next();
			IDAOFactory daoFactory = (IDAOFactory)daoFactoryMap.get(appName);
			configurationMap.put(appName, daoFactory.getConfigurationFile());
		}
		return configurationMap;
	}
}
