/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/catissue-dao/LICENSE.txt for details.
 */

package edu.wustl.dao.interceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.wustl.common.domain.InterceptorErrorObject;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.exception.InterceptProcessorException;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.dao.util.DAOConstants;

/**
 * This class will act as a thread, which will be running for the Lifetime of the application.
 * This thread will start on the intialization of the Dao by using ApplicationDAOProperties.xml file.
 * This will hold the two sets in which the dao will add the objects for which some processing needs to be done after completion
 * of hibernate Transaction. This class will consume those objects & will call the processors for those objects depending on the
 * configuration given in applicationDAOProperties.xml
 * @author pavan_kalantri
 *
 */
public class SaveUpdateInterceptThread implements Runnable
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static final Logger LOGGER = Logger.getCommonLogger(SaveUpdateInterceptThread.class);

	/**
	 * This is the set for all the inserted objects of class mentioned in config file, for which InterceptProcessor should be called.
	 */
	private final Set inserts = new HashSet();

	/**
	 * This is the set for all the Updated objects of class mentioned in config file, for which InterceptProcessor should be called.
	 */
	private final Set updates = new HashSet();

	private int sleepTime = (1000*60*10);

	/**
	 * self instance to make it singleton
	 */
	private static SaveUpdateInterceptThread processor;

	/**
	 * This set will maintain all InterceptorObjects
	 */
	private final static Set<InterceptorObject> monitoredClassList = new HashSet<InterceptorObject>();

	/**
	 * This is the enum which specifies the type of event occured for the Object.
	 * @author Pavan
	 *
	 */
	public enum eventType {
		onInsert(1)
		{
			public String getTypeString()
			{
				return "insert";
			}
		}
		, OnUpdate(2)
		{
			public String getTypeString()
			{
				return "insert";
			}
		};
		public String getTypeString()
		{
			return "";
		}
		private long eventCode;

		/**
		 * Consturctor
		 * @param code event code to be saved in DB.
		 */
		eventType(long code)
		{
			eventCode = code;
		}

		/**
		 * Return the event code.
		 * @return event code.
		 */
		public long getEventCode()
		{
			return eventCode;
		}
	}

	/**
	 * Inner class for maintaining the list of object types to be monitored & the processor classes for each event on these objects.
	 * @author Pavan
	 *
	 */
	class InterceptorObject
	{

		/**
		 * Class type that is to be monitored.
		 */
		private Class monitoredClass;
		/**
		 * Map of processor classed which will be invoked depending on the event type.
		 */
		private final Map<eventType, Class> eventVsProcessorClassMap = new HashMap<eventType, Class>();

		/**
		 * returns the type of class monitored by this object.
		 * @return type of class.
		 */
		public Class getMonitoredClass()
		{
			return monitoredClass;
		}

		/**
		 * Sets the monitored class.
		 * @param monitoredClass class.
		 */
		public void setMonitoredClass(Class monitoredClass)
		{
			this.monitoredClass = monitoredClass;
		}

		/**
		 * Returns the processor class for the given event.
		 * @param type event type
		 * @return processor to be invoked for the given event.
		 */
		public Class getProcessorClassForEvent(eventType type)
		{
			return eventVsProcessorClassMap.get(type);
		}

		/**
		 * This method will add the given processor class in the eventVsProcessorClassMap for the given event.
		 * @param type type of the event
		 * @param processorClass processor class used for the given event type.
		 * @throws ClassNotFoundException exception.
		 * @throws DAOException exception.
 		 */
		public void addProcessorClass(eventType type, String processorClass)
				throws ClassNotFoundException, DAOException
		{
			if (processorClass != null)
			{
				Class processor = Class.forName(processorClass);
				if (InterceptProcessor.class.isAssignableFrom(processor))
				{
					eventVsProcessorClassMap.put(type, processor);
				}
				else
				{
					throw new DAOException(ErrorKey.getErrorKey("error.interceptor.interface"), null, processorClass+DAOConstants.COLON+ InterceptProcessor.class.getName());
				}
			}

		}

	}

	/**
	 * private constructor to make it singleton.
	 */
	private SaveUpdateInterceptThread()
	{

	}

	/**
	 * This method returns the instance of SaveUpdateInterceptThread.
	 *
	 * @return the instance of SaveUpdateInterceptThread.
	 */
	public static synchronized SaveUpdateInterceptThread getInstance()
	{
		if (processor == null)
		{
			processor = new SaveUpdateInterceptThread();
			Thread thread = new Thread(processor);
			thread.setDaemon(true);
			thread.start();
		}

		return processor;
	}

	/**
	 * This method will read the configuration specified in the passed in xml element & will initialize the interceptor framework
	 * for list of class types to be monitores & the processor for each type to be invoked.
	 * @param root xml element from which to configure the framework.
	 * @throws DAOException exception.
	 */
	public synchronized void populateInterceptorObjectList(Element root) throws DAOException
	{
		NodeList rootChildren = root.getElementsByTagName(DAOConstants.NODE_NAME_CLASS);
		updateSleepTimeForThread(root);
		for (int i = 0; i < rootChildren.getLength(); i++)
		{
			Node applicationChild = rootChildren.item(i);

			if (applicationChild.getNodeName().equals(DAOConstants.NODE_NAME_CLASS))
			{
				String className = null;
				String insertProcessor = null;
				String updateProcessor = null;
				NamedNodeMap attributeMap = applicationChild.getAttributes();
				for (int j = 0; j < attributeMap.getLength(); j++)
				{
					Node attribute = attributeMap.item(j);

					if (attribute.getNodeName().equalsIgnoreCase(DAOConstants.NODE_ATTRIBUTE_NAME))
					{
						//object which is to be monitored
						className = attribute.getNodeValue();
					}
					if (attribute.getNodeName().equalsIgnoreCase(DAOConstants.NODE_ATTRIBUTE_ONINSERT))
					{
						//processor class for insert case
						insertProcessor = attribute.getNodeValue();
					}
					if (attribute.getNodeName().equalsIgnoreCase(DAOConstants.NODE_ATTRIBUTE_ONUPDATE))
					{
						//processor class for update scenario
						updateProcessor = attribute.getNodeValue();
					}
				}
				createInterceptorObject(className, insertProcessor, updateProcessor);
			}
		}
	}

	private void updateSleepTimeForThread(Element root)
	{
		NodeList rootChildren = root.getElementsByTagName(DAOConstants.NODE_NAME_INTERCEPTOROBJECT);
		if(rootChildren.getLength()>0)
		{
			Node period = rootChildren.item(0).getAttributes().getNamedItem(DAOConstants.NODE_ATTRIBUTE_PROCESSINGPERIOD);
			if(period!=null && !"".equals(period.getNodeValue()))
			{
				//objectProcessingPeriod will be given in minutes so convert it to mili seconds.
				sleepTime = Integer.parseInt(period.getNodeValue())*60*1000;
			}
		}

	}

	/**
	 * This method will create the InterceptorObject for the given className as monitored class & other two params as
	 * the processor classes to be called on insert & updates respectively.
	 * @param className class to be monitored.
	 * @param insertProcessor processor which should be invoked on insertion of object of above class type
	 * @param updateProcessor processor which should be invoked on updation of object of above class type
	 * @throws DAOException exceptin if any of the given classes does not exists or processor classes does not implements InterceptProcessor interface.
	 */
	private void createInterceptorObject(String className, String insertProcessor,
			String updateProcessor) throws DAOException
	{
		if (className != null && (insertProcessor != null || updateProcessor != null))
		{
			//valid entry for monitoring the objects.
			InterceptorObject interceptObject = new InterceptorObject();
			try
			{
				interceptObject.setMonitoredClass(Class.forName(className));
				interceptObject.addProcessorClass(eventType.onInsert, insertProcessor);
				interceptObject.addProcessorClass(eventType.OnUpdate, updateProcessor);
				monitoredClassList.add(interceptObject);
				LOGGER.info("Hibernate Interceptor monitor activated for objects of class "
						+ className);
			}
			catch (ClassNotFoundException e)
			{
				// TODO Auto-generated catch block
				throw new DAOException(ErrorKey.getErrorKey("db.app.prop.parsing.exp"), e,null
						);
			}
		}
		else
		{
			throw new DAOException(ErrorKey.getErrorKey("error.interceptor.insufficient.argument"), null,null);
		}
	}

	/**
	 * Add the obj to the inserted queue,
	 * this is synchronized operation on the queue to make it thread safe.
	 * @param obj object to be added.
	 */
	void addInsertedObject(Object obj)
	{
		synchronized (inserts)
		{
			inserts.add(obj);
		}

	}


	/**
	 * Add the obj to the updated queue,
	 * this is synchronized operation on the queue to make it thread safe.
	 * @param obj object to be added.
	 */
	void addUpdatedObject(Object obj)
	{
		synchronized (updates)
		{
			updates.add(obj);
		}

	}

	/**
	 * This method will return the processor class list for the given object & the type of event given as parameter.
	 * @param obj object for which processor classes are required.
	 * @param type type of event occured.
	 * @return
	 */
	List<Class> getProcessorClassForObject(Object obj, eventType type)
	{
		List<Class> processorClassList = new ArrayList<Class>();
		for (InterceptorObject interceptorObject : monitoredClassList)
		{
			if (interceptorObject.getMonitoredClass().isInstance(obj))
			{
				Class processorClass = interceptorObject.getProcessorClassForEvent(type);
				if (processorClass != null)
				{
					processorClassList.add(processorClass);
				}
			}
		}
		return processorClassList;
	}

	/*List<Class> getProcessorClassForObject(Class objType, eventType type)
	{
		List<Class> processorClassList = new ArrayList<Class>();
		for (InterceptorObject interceptorObject : monitoredClassList)
		{
			if (interceptorObject.getMonitoredClass().isAssignableFrom(objType))
			{
				Class processorClass = interceptorObject.getProcessorClassForEvent(type);
				if (processorClass != null)
				{
					processorClassList.add(processorClass);
				}
			}
		}
		return processorClassList;
	}*/

	/**
	 * Thiis method will check whether the current object is to be monitored, i.e whether any processor have been configured for this
	 * object or not.
	 * @param obj object for which to check the configuration.
	 * @return true if object is to be monitored else false.
	 */
	public boolean isObjectTobeMonitored(Object obj)
	{
		boolean isMonitored = false;
		if (!monitoredClassList.isEmpty())
		{
			for (InterceptorObject cls : monitoredClassList)
			{
				if (cls.getMonitoredClass().isInstance(obj))
				{
					isMonitored = true;
					break;
				}
			}
		}
		return isMonitored;
	}

	/**
	 * This is the method which will be running continuously in thread processing all the objects present in
	 * inserts & updates queues respectively.
	 */
	public void run()
	{
		while (true)
		{
			try
			{

				// process all the objects in insert queue.
				boolean isInsertsAvailable = processObjectsInSet(inserts, eventType.onInsert);
				//process all the objects in the update queue.
				boolean isUpdateAvailabel = processObjectsInSet(updates, eventType.OnUpdate);
				if (!isInsertsAvailable && !isUpdateAvailabel)
				{
					// thread should be slept for 7 to 10 mins
					Thread.sleep(sleepTime);
				}

			}
			catch (Exception e)
			{
				LOGGER.error("Error occured in hibernate interceptor thread.");
				LOGGER.error("Exception : ", e);
			}
		}

	}

	/**
	 * This method will fetch the object in the given setTobeProcessed collection & invoke the processor classes for that
	 * object. If set is empty then it will return false.
	 * @param setTobeProcessed set from which the objects should be processed.
	 * @param type type of event occured.
	 * @return true if any of the object is processed, else false if set is empty.
	 * @throws Exception exception.
	 */
	private boolean processObjectsInSet(Set setTobeProcessed, eventType type) throws Exception
	{

		Object objInserted = null;
		boolean isObjectAvailable = false;
		synchronized (setTobeProcessed)
		{
			if (!setTobeProcessed.isEmpty())
			{
				Iterator iterator = setTobeProcessed.iterator();
				objInserted = iterator.next();
				iterator.remove();
				isObjectAvailable = true;
			}
			// fetch the element from inserts set & do processing for it
		}
		invokeProcessorClass(objInserted, type);
		return isObjectAvailable;
	}

	/**
	 * This will invke the process method in the processor classes associated with given object for given event type.
	 * @param object object for which the processors should be called.
	 * @param type type of event.
	 * @throws Exception exception.
	 */
	private void invokeProcessorClass(Object object, eventType type) throws Exception
	{
		if (object != null)
		{

			//search for the interceptorObject for this class
			List<Class> processorClassList = getProcessorClassForObject(object, type);
			for (Class processorClass : processorClassList)
			{
				try
				{
					Object processor;

					processor = processorClass.newInstance();

					Method method = processorClass.getMethod(DAOConstants.INTERCEPT_PROCESS_METHOD, Object.class,
							eventType.class);
					method.invoke(processor, object, type);
				}
				catch (Exception e)
				{ // only this block will reach if some exception is actually throw from process method of preocessor.
					// all other exceptions above this block can occur only in calling method using reflection, which is not possible as we have validated it
					//against the interface at the initialization.

					//so now save the interceptErrorObject for executing error recovery later.
					createErrorObject(object, e, type, processorClass.getName());
				}

			}

		}
	}

	/**
	 * THis method will create a InterceptError object with the help of given params & will save it into the db for error recovery later.
	 * @param object object in which erorr was occured while processing
	 * @param e exception occured.
	 * @param type type of the event
	 * @param processorClassName processor class who threw the exception.
	 * @throws DAOException error in DB operation.
	 */
	private void createErrorObject(Object object, Exception e, eventType type,
			String processorClassName) throws DAOException
	{
		String query = "insert into INTERCEPTOR_ERROR_OBJ (IDENTIFIER,ERROR_TIMESTAMP,OBJECT_TYPE,ERROR_CODE,OBJECT_ID,RECOVERY_DONE,EVENT_CODE,NUMBER_OF_TRY,PROCESSOR_CLASS) values(?,?,?,?,?,?,?,?,?)";
		Long identifier = getNextIdentifierForErrorObject();
		LinkedList<ColumnValueBean> columnValueBean = new LinkedList<ColumnValueBean>();
		columnValueBean.add(new ColumnValueBean("IDENTIFIER", identifier));
		columnValueBean.add(new ColumnValueBean("ERROR_TIMESTAMP", new Timestamp(new Date()
				.getTime())));
		columnValueBean.add(new ColumnValueBean("OBJECT_TYPE", object.getClass().getName()));

		InterceptorErrorObject errorObject = new InterceptorErrorObject();
		errorObject.setEventCode(type.getEventCode());
		if (e.getCause() instanceof InterceptProcessorException)
		{
			InterceptProcessorException exc = (InterceptProcessorException) e.getCause();
			columnValueBean.add(new ColumnValueBean("ERROR_CODE", exc.getErrorCode()));

		}
		else
		{
			columnValueBean.add(new ColumnValueBean("ERROR_CODE", "0000"));
		}
		Long objId = getObjectIdentifier(object);

		columnValueBean.add(new ColumnValueBean("OBJECT_ID", objId));
		columnValueBean.add(new ColumnValueBean("RECOVERY_DONE", new Boolean(false)));
		columnValueBean.add(new ColumnValueBean("EVENT_CODE", type.getEventCode()));
		columnValueBean.add(new ColumnValueBean("NUMBER_OF_TRY", new Long(0)));
		columnValueBean.add(new ColumnValueBean("PROCESSOR_CLASS", processorClassName));
		errorObject.setObjectType(object.getClass().getName());

		if (objId != null)
		{

			saveErrorObject(query, columnValueBean);
		}
	}

	private Long getNextIdentifierForErrorObject() throws DAOException
	{
		JDBCDAO dao = null;
		Long identifier = 1l;
		try
		{
			dao = DAOConfigFactory.getInstance().getDAOFactory().getJDBCDAO();
			dao.openSession(null);
			List recordList =  dao.executeQuery("select max(IDENTIFIER)+1 from INTERCEPTOR_ERROR_OBJ",new ArrayList<ColumnValueBean>());
			List result = (List)recordList.get(0);
			Object id = result.get(0);
			if(id==null || "".equals(id))
			{
				identifier = 1l;
			}
			else
			{
				identifier = Long.valueOf(id.toString());
			}
		}
		catch (DAOException e)
		{
			LOGGER.error("Error while retrieving max identifier for error object in Interceptor thread", e);
		}
		finally
		{
			dao.closeSession();
		}
		return identifier;

	}

	/**
	 * This method will execute the given query & save the data.
	 * @param query query string
	 * @param columnValueBeans params list
	 * @throws DAOException
	 */
	private void saveErrorObject(String query, LinkedList<ColumnValueBean> columnValueBeans)
			throws DAOException
	{
		JDBCDAO dao = null;

		try
		{
			List<LinkedList<ColumnValueBean>> colValueBeanList = new LinkedList<LinkedList<ColumnValueBean>>();
			colValueBeanList.add(columnValueBeans);
			dao = DAOConfigFactory.getInstance().getDAOFactory().getJDBCDAO();
			dao.openSession(null);
			dao.executeUpdate(query, colValueBeanList);
			dao.commit();
		}
		catch (DAOException e)
		{
			LOGGER.error("Error while saving the error object in Interceptor thread", e);
		}
		finally
		{
			dao.closeSession();
		}

	}

	/**
	 * This method will invoke the getId() method on the given object using reflection.
	 * @param object object whose id is needed.
	 * @return identifier.
	 */
	private Long getObjectIdentifier(Object object)
	{
		Method method;
		Long id = null;
		try
		{
			method = object.getClass().getMethod("getId");

			id = (Long) method.invoke(object);
		}
		catch (NoSuchMethodException e)
		{
			LOGGER
					.error(
							"Error occured while fectching the identifier of the object in Interceptor thread",
							e);
		}
		catch (InvocationTargetException e)
		{
			LOGGER
					.error(
							"Error occured while fectching the identifier of the object in Interceptor thread",
							e);
		}
		catch (IllegalAccessException e)
		{
			LOGGER
					.error(
							"Error occured while fectching the identifier of the object in Interceptor thread",
							e);
		}
		return id;
	}

}
