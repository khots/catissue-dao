package edu.wustl.dao.util;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.cfg.Configuration;

/**
 * This class keeps a map of all the HibernateMetaData instances for all the
 * applications.
 * @author Shrishail_kalshetty
 */
public final class HibernateMetaDataFactory
{
	/**
	 * Map to cache all the instances of HibernateMetaData.
	 */
	public static final Map<String, HibernateMetaData> metaDataCache=
		new HashMap<String, HibernateMetaData>();
	/**
	 * This method adds the Configuration to the map for the given application.
	 * @param appName appName
	 * @param cfg cfg
	 */
	public static void setHibernateMetaData(String appName, Configuration cfg)
	{
		HibernateMetaData hibernateMetaData=metaDataCache.get(appName);
		if(hibernateMetaData==null)
		{
			hibernateMetaData=new HibernateMetaData(cfg);
			metaDataCache.put(appName, hibernateMetaData);
		}
	}
	/**
	 * private instance.
	 */
	private HibernateMetaDataFactory()
	{

	}

	/**
	 * This method returns the Configuration given the application name.
	 * @param appName appName
	 * @return HibernateMetaData HibernateMetaData
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
