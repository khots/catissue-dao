package edu.wustl.dao.interceptor;

import edu.wustl.dao.exception.InterceptProcessorException;
import edu.wustl.dao.interceptor.SaveUpdateInterceptThread.eventType;

/**
 * Any class which is going to act as InterceptProcessor should implement this
 * interface.
 * @author Pavan
 *
 */
public interface InterceptProcessor {

	/**
	 * This method will be called when any DB operation is performed on the object which is added
	 * in the InterceptorObjects list via ApplicationDAOProperties.xml file.
	 * @param obj Object on which db operation was performed.
	 * @param type type of event occured i.e insert or edit.
	 * @throws InterceptProcessorException exception.
	 */
	void process(Object obj,eventType type)throws InterceptProcessorException;

	/**
	 * This is the method which will be called from the InterceptorFramework for all the
	 * objects for whom the process method has thrown some exception.
	 * @param objectType Class name of the Object for which this method is called.
	 * @param type event type, i.e. Insert or Edit.
	 * @param objectId Identifier of the Object of given Type for which error was occured.
	 * @throws InterceptProcessorException Exception.
	 */
	void onError(String objectType,eventType type,Long objectId)throws InterceptProcessorException;
}
