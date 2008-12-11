/**
 * <p>Title: DatabaseConnectionParams Class>
 * <p>Description:	DatabaseConnectionParams handles opening closing ,initialization of all database specific
 * parameters  .</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @version 1.00
 * @author kalpana_thakur
 */
package edu.wustl.dao.util;

import java.util.Iterator;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.proxy.HibernateProxy;

/**
 * @author kalpana_thakur
 * This class is specific to hibernate.
 */

public final class HibernateMetaData
{

	/**
	 * creates a single object.
	 */
	private static HibernateMetaData hibernateMData = new HibernateMetaData();;
	/**
	 * Private constructor.
	 */
	private HibernateMetaData()
	{

	}
	/**
	 * returns the single object.
	 * @return Utility object
	 */
	public static HibernateMetaData getInstance()
	{
		return hibernateMData;
	}

	/**
	 * Configuration file .
	 */
	private static Configuration cfg;
	/**
	 * Mappings.
	 *//*
	private static Set mappings = new HashSet();*/
	/**
	 *
	 * @param configuration :
	 */
	public static void initHibernateMetaData(Configuration configuration)
	{
		cfg = configuration;
	}

	/**
	 * This method will return domain object from proxy Object.
	 * @param domainObject :
	 * @return domain Object :
	 */
	public static Object getProxyObjectImpl(Object domainObject)
	{
		Object object = domainObject;
		if (domainObject instanceof HibernateProxy)
		{
			HibernateProxy hiberProxy  = (HibernateProxy)domainObject;
			object = hiberProxy.getHibernateLazyInitializer().getImplementation();
		}
        return object;
	}

	/**
	 * This method will be called to return table name for the given class.
	 * @param classObj : Class of the object
	 * @return tableName : It returns the table name associated to the class.
	 */
	public static String getTableName(Class classObj)
	{

		String tableName = "";
		Table tbl = cfg.getClassMapping(classObj.getName()).getTable();
		if(tbl!=null)
		{
			tableName = tbl.getName();
		}
		return tableName;

	}
	/**
	 * This method will be called to return root table name for the given class.
	 * @param classObj :Class of the object
	 * @return :It returns the root table name associated to the class.
	 */
	public static String getRootTableName(Class classObj)
	{
		String rootTableName = "";
		Table tbl = cfg.getClassMapping(classObj.getName()).getRootTable();
		if(tbl!=null)
		{
			rootTableName = tbl.getName();
		}
		return rootTableName;

	}

	/**
	 * This will return the column name mapped to given attribute of the given class.
	 * @param classObj : Class of the object
	 * @param attributeName :attribute of the given class
	 * @return returns the Column Name mapped to the attribute.
	 */
	public static String getColumnName(Class classObj, String attributeName)
	{
		String columnName = DAOConstants.TAILING_SPACES;
		boolean foundColName = false;
		Iterator<Object> iter = cfg.getClassMapping(classObj.getName()).getPropertyClosureIterator();
		while(iter.hasNext())
		{
			columnName = getColumnName(attributeName,iter);
			if(!DAOConstants.TAILING_SPACES.equals(columnName))
			{
				foundColName = true;
				break;
			}
		}

		if(!foundColName)
		{
			columnName = getColumName(classObj, attributeName);
		}
		return columnName;
	}
	/**
	 * This method will returns the column name associate to given property.
	 * @param iter : holds the property object.
	 * @param attributeName :attribute of the given class
	 * @return returns the Column Name mapped to the attribute.
	 */
	private static String getColumnName(String attributeName,
			Iterator<Object> iter)
	{
		String columnName = DAOConstants.TAILING_SPACES;
		Property property = (Property)iter.next();
		if(property!=null && property.getName().equals(attributeName))
		{
			Iterator<Object> colIt = property.getColumnIterator();
			if(colIt.hasNext())
			{
				Column col = (Column)colIt.next();
				columnName = col.getName();
			}
		}
		return columnName;
	}
	/**
	 *This method will iterate the Identified property file and returns the column name.
	 * @param classObj : Class of the object
	 * @param attributeName :attribute of the given class
	 * @return returns the Column Name mapped to the attribute.
	 */
	private static String getColumName(Class classObj, String attributeName)
	{
		String columnName = DAOConstants.TAILING_SPACES;
		Property property = cfg.getClassMapping(classObj.getName()).getIdentifierProperty();
		if(property.getName().equals(attributeName))
		{
			Iterator<Object> colIt = property.getColumnIterator();//y("id").getColumnIterator();
			if(colIt.hasNext())
			{
				Column col = (Column)colIt.next();
				columnName = col.getName();
			}
		}
		return columnName;
	}

	/**
	 * This method will be called to obtained column width of attribute field of given class.
	 * @param classObj Name of the class.
	 * @param attributeName Name of the attribute.
	 * @return The width of the column. Returns width of the column or zero.
	 */
	public static int getColumnWidth(Class classObj, String attributeName)
	{
		Iterator iterator = cfg.getClassMapping(classObj.getName()).getPropertyClosureIterator();
		int colLength = 50;
		while(iterator.hasNext())
		{
			Property property = (Property)iterator.next();

			if(property!=null && property.getName().equals(attributeName))
			{
				Iterator colIt = property.getColumnIterator();
				while(colIt.hasNext())
				{
					Column col = (Column)colIt.next();
					colLength = col.getLength();
				}
			}
		}
		// if attribute is not found than the default width will be 50.
		return colLength;
	} // getColumnWidth


	/**
	 * This method will be called to obtained the
	 * domain class name mapped to given table.
	 * @param tableName :Name of table.
	 * @return the class name
	 */
	public static  String getClassName(String tableName)
	{
		Iterator it = cfg.getClassMappings();
		PersistentClass persistentClass;
		String className = DAOConstants.TAILING_SPACES;
		while(it.hasNext())
		{
			persistentClass = (PersistentClass) it.next();
			if(tableName.equalsIgnoreCase(persistentClass.getTable().getName()))
			{
				className = persistentClass.getClassName();
			}
		}

		return className;
	}


	/**
	 *  This function returns the attributeName related to classObj1 and classObj2.
	 * @param classObj1 :
	 * @param classObj2 :
	 * @param attName :
	 * @return attName
		getFullyQulifiedRoleAttrName
	 */
	/**
	 * @param classObj1
	 * @param classObj2
	 * @param attName
	 * @return
	 */
	/*public static String getFullyQualifiedRoleAttName(Class classObj1,Class classObj2,String attName)
	{
		Iterator itr=mappings.iterator();
		String roleAttribute = null;
		while(itr.hasNext())
		{
			ClassRelationshipData crd=(ClassRelationshipData)itr.next();
			if(classObj1.getName().equals(crd.getClassName())&&classObj2.getName().
			equals(crd.getRelatedClassName())&& crd.getRoleAttribute().indexOf(attName)!=-1)
			{
				roleAttribute = crd.getRoleAttribute();
			}
		}
		return roleAttribute;
	}

	*//**
	 *  This function checks weather relation is Many-Many.
	 * @param classObj1 :
	 * @param classObj2 :
	 * @param roleAttributeName :
	 * @return true is relation is Many-Many otherwise false
	 *//*
	public static boolean isRelationManyToMany(Class classObj1,Class classObj2,String roleAttributeName)
	{
		ClassRelationshipData crd=new ClassRelationshipData(classObj1.getName(),
				classObj2.getName(),roleAttributeName);
		Iterator itr = mappings.iterator();
		boolean isRelManyToMany = false;

		while(itr.hasNext())
		{
			ClassRelationshipData crd1=(ClassRelationshipData)itr.next();
			if(crd1.equals(crd)&& crd1.getRelationType().equals("ManyToMany"))
			{
				isRelManyToMany = true;
			}
		}
		return isRelManyToMany;
	}
*/
	/**
	 * This method returns the list of subclasses of the className
	 * @author aarti_sharma
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 *//*
	public static List getSubClassList(String className) throws ClassNotFoundException
	{
		List list = new ArrayList();
		//System.out.println("className :::"+ className);
		Class classObj = Class.forName(className);
		//System.out.println("classObj :::"+ classObj);
		PersistentClass classobj1 = cfg.getClassMapping(classObj.getName());
		Iterator it  =classobj1.getDirectSubclasses();
		//Iterator it = cfg.getClassMapping(classObj).getDirectSubclasses();
		while(it.hasNext())
		{
			Subclass subClass = (Subclass)it.next();

			System.out.println(subClass.getClass().getName());
			//System.out.println("Name "+subClass.getName());
			list.add(subClass.getClassName());
		}
		return list;
	}*/

	/**
	 * This method returns the super class of the obj passed
	 * @author aarti_sharma
	 * @param objClass
	 * @return
	 */
	/*public static Class getSuperClass(Object  obj)
	{
		Class objClass = obj.getClass();
		PersistentClass persistentClass = cfg.getClassMapping(objClass.getName());
		PersistentClass superClass = persistentClass.getSuperclass();
		return superClass.getClass();
	}
*/
	/**
	 * This method returns the supermost class
	 * of the class passed that is in the same package as class
	 * @author aarti_sharma
	 * @param objClass
	 * @return
	 */
	/*public static Class getSupermostClassInPackage(Object obj) {
		Class objClass = obj.getClass();
		Package objPackage = objClass.getPackage();
		Logger.out.debug("Input Class: " + objClass.getName()+" Package:"+objPackage.getName());

		PersistentClass persistentClass = cfg.getClassMapping(objClass.getName());
		if (persistentClass != null && persistentClass.getSuperclass()!=null) {

			Logger.out.debug(objPackage.getName()+" "+persistentClass.getClassName()+"*********"
			+persistentClass.getSuperclass().getMappedClass().getPackage().getName()
					);
			Logger.out.debug("!!!!!!!!!!! "+persistentClass.getSuperclass().
			getMappedClass().getPackage().getName()
					.equals(objPackage.getName()));
			do {
				persistentClass = persistentClass.getSuperclass();
			}while(persistentClass !=null);
			Logger.out.debug("Supermost class in the same package:"
					+ persistentClass.getMappedClass().getName());
		} else {
			return objClass;
		}
		return persistentClass.getMappedClass();
	}
*/



	/*public static void getDATA(Class classObj)
	{
		org.hibernate.mapping.Collection coll = cfg.getCollectionMapping("edu.wustl.catissuecore.domain.
		CollectionProtocolEvent.specimenRequirementCollection");
		//System.out.println(map);

		System.out.println(coll.getCollectionTable().getName());
		System.out.println(coll.getTable().getName());
		//System.out.println();

		Iterator it = coll.getColumnIterator();

		while(it.hasNext())
		{
			//org.hibernate.mapping.Set set = (org.hibernate.mapping.Set)it.next();
			System.out.println(it.next());

		}
	}*/

	/**
	 * This Function finds all the relations in i.e Many-To-Many and One-To-Many
	 * All the relations are kept in HashMap where key is formed as table1@table2@table_name@attributeName
	 * and value is Many-To-Many or One-To-Many
	 *
	 * @return Map
	 */
	/*private static void findRelations()
	{
		try
		{
			Iterator itr1=cfg.getCollectionMappings();

			while(itr1.hasNext())
			{
				Collection col=(Collection)itr1.next();

				if(col.getElement().getClass().getName().equals(ManyToOne.class.getName()))
				{
					saveRelations(col,"ManyToMany");
				}
				else
				{
					saveRelations(col,"OneToMany");
				}
			}
		}
		catch (Exception e)
		{
			//This line is commented because logger when not initialized
			 *  properly throws NullPointerException
			//Logger.out.info("Error occured in fildAllRelations Function:"+e);
		}

	}*/

	/**This function saves the relation data in HashSet.
	 * @param col this is the collection which contains all data
	 * @param rel_type this is Many-To-Many ot Many-To-One
	 * @throws Exception
	 *//*
	private static void saveRelations(Collection col,String rel_type) throws Exception
	{
		String className=col.getOwner().getClassName();
		String relatedClassName=col.getElement().getType().getName();
		String roleAttribute=col.getRole();
		String relationType=rel_type;
		String relationTable=col.getElement().getTable().getName();
		String keyId=getKeyId(roleAttribute);
		String roleId=getRoleKeyId(roleAttribute);

		ClassRelationshipData hmc=new ClassRelationshipData(className,relatedClassName,
		roleAttribute,relationType,relationTable,keyId,roleId);
		mappings.add(hmc);

		List list1=HibernateMetaData.getSubClassList(col.getOwner().getClassName());
		for(int i=0;i<list1.size();i++)
		{
			hmc=new ClassRelationshipData(list1.get(i).
			toString(),relatedClassName,roleAttribute,relationType,
					relationTable,keyId,roleId);
			mappings.add(hmc);
		}

		List list2=HibernateMetaData.getSubClassList(col.getElement().getType().getName());
		for(int i=0;i<list2.size();i++)
		{
			hmc=new ClassRelationshipData(className,list2.get(i).toString(),roleAttribute,relationType,
					relationTable,keyId,roleId);
			mappings.add(hmc);
		}
	}
*/


/*	public static ClassRelationshipData getClassRelationshipData(Class classObj1,Class classObj2,
 *  String roleAttributeName)
	{
		ClassRelationshipData crd=new ClassRelationshipData(classObj1.getName(),classObj2.getName(),
		roleAttributeName);

		Iterator itr=mappings.iterator();

		while(itr.hasNext())
		{
			ClassRelationshipData crd1=(ClassRelationshipData)itr.next();
			if(crd1.equals(crd))
			{
				return crd1;

			}
		}
		return null;

	}
	*//**This function returns the RoleClass for given attName
	 * @param attName
	 * @return RoleClass
	 *//*
	public static Class getRoleClass(String attName)
	{
		Iterator itr=mappings.iterator();

		while(itr.hasNext())
		{
			ClassRelationshipData crd=(ClassRelationshipData)itr.next();
			if(crd.getRoleAttribute().indexOf(attName)!=-1)
			{
				return Utility.getClassObject(crd.getRelatedClassName());

			}
		}
		return null;
	}
*/



	/**
	 * This function gets the key Id.
	 * from hibernate mappings and returns the value
	 * @param attributeName :
	 * @return key Id
	 *
	 *//*
	public static String getKeyId(String attributeName)
	{
		org.hibernate.mapping.Collection col1 = cfg.getCollectionMapping(attributeName);
		Iterator keyIt = col1.getKey().getColumnIterator();
		while (keyIt.hasNext())
		{
			Column col = (Column) keyIt.next();
			return(col.getName());
		}

		return "";
	}
	*//** This function returns the role Id.
	 * from hibernate mapping and returns the value
	 * @param attributeName :
	 * @return roleKeyId
	 *
	 *//*
	public static String getRoleKeyId(String attributeName)
	{
		org.hibernate.mapping.Collection col1 = cfg.getCollectionMapping(attributeName);
		Iterator colIt = col1.getElement().getColumnIterator();
		while (colIt.hasNext())
		{
			Column col = (Column) colIt.next();
			return(col.getName());
		}
		return "";
	}
*/
	//Mandar:26-apr-06 start
//	Mandar : 26-Apr-06 : 872 : Column width


	//	Mandar:26-apr-06 end

	/*public static void main(String[] args) throws Exception
	{
		Variables.applicationHome = System.getProperty("user.dir");
		Logger.out = org.apache.log4j.Logger.getLogger("");
		PropertyConfigurator.configure(Variables.applicationHome+"\\WEB-INF\\src\\"+
		"ApplicationResources.properties");

		Logger.out.debug("here");

		DBUtil.currentSession();
		System.out.println(mappings.size());

	}*/
}