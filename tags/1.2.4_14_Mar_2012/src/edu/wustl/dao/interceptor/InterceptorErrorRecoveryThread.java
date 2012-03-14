
package edu.wustl.dao.interceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

import edu.wustl.common.domain.InterceptorErrorObject;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.interceptor.SaveUpdateInterceptThread.eventType;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.dao.util.DAOConstants;

public class InterceptorErrorRecoveryThread extends TimerTask
{

	private static final String ALL_ERROR_OBJ_QUERY = "select IDENTIFIER, ERROR_TIMESTAMP,OBJECT_TYPE,ERROR_CODE,OBJECT_ID,EVENT_CODE,NUMBER_OF_TRY,PROCESSOR_CLASS from INTERCEPTOR_ERROR_OBJ where RECOVERY_DONE = ?";
	private static final String UPDATE_ERROR_OBJ_QUERY="update INTERCEPTOR_ERROR_OBJ set RECOVERY_DONE = ? , NUMBER_OF_TRY=? where identifier =?";
	/**
	 * logger Logger - Generic logger.
	 */
	private static final Logger LOGGER = Logger.getCommonLogger(SaveUpdateInterceptThread.class);

	/**
	 * This is the Method which will be executed once per day to do the error recovery for all the
	 * objects which have been failed in doing processing while interception.
	 */
	public void run()
	{
		try
		{
			//fetch all error objects for which error recovery is not done yet.
			List<InterceptorErrorObject> interceptorErrorObjects = getAllErrorObjects();

			for (InterceptorErrorObject errorObj : interceptorErrorObjects)
			{
				// Invoke processor class for each error object.
				invokeProcessorClassForErrorRecovery(errorObj);
			}
		}
		catch (DAOException e)
		{
			LOGGER.error("Error occured in Interceptor Error recovery thread.", e);
		}

	}

	/**
	 * This method will check whether the processor should be called for this error object depending on number of tries
	 * aleady done, if its valid then it will search its processor classes & invoke the onError method from each processor.
	 * @param errorObj object for which to do the error recovery.
	 * @throws DAOException exception in db operation.
	 */
	private void invokeProcessorClassForErrorRecovery(InterceptorErrorObject errorObj)
			throws DAOException
	{
		if (errorObj.isValidForTryAgain())
		{
			try
			{

				eventType type;
				if (errorObj.getEventCode().equals(1))
				{
					type = eventType.onInsert;
				}
				else
				{
					type = eventType.OnUpdate;
				}
				errorObj.incrementNumberOfTry();
				//get the processor in which error was occured
				Class processorClass = Class.forName(errorObj.getProcessorClass());

				Object processor = processorClass.newInstance();

				Method method = processorClass.getMethod(DAOConstants.INTERCEPT_ONERROR_METHOD,
						errorObj.getObjectType().getClass(), eventType.class, errorObj
								.getObjectId().getClass());
				// call onError method of the processor.
				method.invoke(processor, errorObj.getObjectType(), type, errorObj.getObjectId());

				// now update the error object saying error recovery done.
				errorObj.setRecoveryDone(true);
				updateErrorObject(true, errorObj.getNumberOfTry() , errorObj.getObjectId(),
						errorObj.getId());

			}
			catch (Exception e)
			{
				// exception is occured so error Recovery is still not done hence dont update that flag & also increment the number of tries.
				updateErrorObject(false, errorObj.getNumberOfTry(), errorObj.getObjectId(),
						errorObj.getId());
				LOGGER
						.error("Error occured in Interceptor Error recovery thread for errod object id"
								+ errorObj.getId(),e);
			}
		}
	}

	/**
	 * This method will update the error object with the given identifier setting the properties as given in the
	 * parameters
	 * @param recoverDone indicates whether the recovery was successfull or not.
	 * @param numberOfTry number tries taken to do the recovery
	 * @param objId object id for which this error has occured.
	 * @param identifier identifier of the erre object for which to update the details.
	 * @throws DAOException exception.
	 */
	private void updateErrorObject(Boolean recoverDone, Integer numberOfTry, Long objId,
			Long identifier) throws DAOException
	{
		// TODO Auto-generated method stub
		JDBCDAO defaultDao = null;
		try
		{
			LinkedList<ColumnValueBean> columnValueBean = new LinkedList<ColumnValueBean>();
			columnValueBean.add(new ColumnValueBean("RECOVERY_DONE", recoverDone));
			columnValueBean.add(new ColumnValueBean("NUMBER_OF_TRY", numberOfTry));
			columnValueBean.add(new ColumnValueBean("identifier", identifier));
			defaultDao = DAOConfigFactory.getInstance().getDAOFactory().getJDBCDAO();
			defaultDao.openSession(null);
			List<LinkedList<ColumnValueBean>> colValueBeanList = new ArrayList<LinkedList<ColumnValueBean>>();
			colValueBeanList.add(columnValueBean);
			defaultDao.executeUpdate(UPDATE_ERROR_OBJ_QUERY, colValueBeanList);
			defaultDao.commit();
		}
		catch (DAOException e)
		{
			LOGGER
					.error("failed to update the recevery done property of interceptor error object for id = "
							+ objId);
		}
		finally
		{
			defaultDao.closeSession();
		}
	}

	/**
	 * This method will return all the error objects for which the error recovery needs to be performed.
	 * @return list of error objects.
	 * @throws DAOException exception in DB operations.
	 */
	private List<InterceptorErrorObject> getAllErrorObjects() throws DAOException
	{
		DAO defaultDao = null;
		List<InterceptorErrorObject> interceptorErrorObjects = new ArrayList<InterceptorErrorObject>();
		try
		{
			defaultDao = DAOConfigFactory.getInstance().getDAOFactory().getJDBCDAO();
			defaultDao.openSession(null);
			ArrayList<ColumnValueBean> colValueBeanList = new ArrayList<ColumnValueBean>();
			colValueBeanList.add(new ColumnValueBean(new Boolean(false)));
			List resultList = defaultDao.executeQuery(ALL_ERROR_OBJ_QUERY,
					colValueBeanList);
			interceptorErrorObjects.addAll(getErrorObjectList(resultList));
		}
		catch (DAOException e)
		{
			LOGGER
					.error("Error occured while retrieving all the interceptor error objects in Intercept error recovery thread.");
		}
		finally
		{
			defaultDao.closeSession();
		}
		return interceptorErrorObjects;
	}

	/**
	 * This method will create the error objects from the retrieved DB info & return it as list.
	 * @param resultList list retrieved from DB.
	 * @return List of Interceptor Error objects.
	 */
	private Collection<? extends InterceptorErrorObject> getErrorObjectList(List resultList)
	{
		List<InterceptorErrorObject> resultObjectList = new ArrayList<InterceptorErrorObject>();
		for (Object result : resultList)
		{
			ArrayList resultArray = (ArrayList) result;
			Long identifier = Long.valueOf((String) resultArray.get(0));
			String objectType = (String) resultArray.get(2);
			String errorCode = (String) resultArray.get(3);
			Long objId = Long.valueOf((String) resultArray.get(4));
			Long eventCode = Long.valueOf((String) resultArray.get(5));
			Integer numberOfTry = Integer.valueOf((String) resultArray.get(6));
			String processorClass = (String) resultArray.get(7);
			resultObjectList.add(new InterceptorErrorObject(identifier, objectType, errorCode,
					objId, eventCode, numberOfTry, processorClass));
		}
		return resultObjectList;
	}

}
