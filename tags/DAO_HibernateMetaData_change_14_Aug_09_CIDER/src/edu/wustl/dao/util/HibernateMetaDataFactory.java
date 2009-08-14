package edu.wustl.dao.util;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.cfg.Configuration;

/**
 * This class keeps a map of all the HibernateMetaData instances for all the
 * applications.
 * @author niharika_sharma
 *
 */
public class HibernateMetaDataFactory
{
	/**
	 * Map to cache all the instances of HibernateMetaData.
	 */
	public static final Map<String, HibernateMetaData> metaDataCache=new HashMap<String, HibernateMetaData>();
	
	/**
	 * This method adds the Configuration to the map for the given application.
	 * @param appName
	 * @param cfg
	 */
	public static final void setHibernateMetaData(String appName, Configuration cfg)
	{
		HibernateMetaData hibernateMetaData=metaDataCache.get(appName);
		if(hibernateMetaData==null)
		{
			hibernateMetaData=new HibernateMetaData(cfg);
			metaDataCache.put(appName, hibernateMetaData);
		}
	}
	
	/**
	 * This method returns the Configuration given the application name.
	 * @param appName
	 * @return
	 */
	public static HibernateMetaData getHibernateMetaData(String appName)
	{
		HibernateMetaData metadata;
		if(appName!=null)
		{
			metadata=metaDataCache.get(appName);
		}
		else
		{
			metadata=null;
		}
		return metadata;
	}
	
}
