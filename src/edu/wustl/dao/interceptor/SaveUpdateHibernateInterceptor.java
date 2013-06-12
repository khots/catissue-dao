/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/catissue-dao/LICENSE.txt for details.
 */

package edu.wustl.dao.interceptor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

import edu.wustl.common.util.logger.Logger;

/**
 * This is the interceptor class which will get executed whenever a object is saved or updated by hibernate.
 * This class will then redirect the flow to respective processor classes according to the given configuration in ApplicationDAOProperties.xml
 * @author Pavan
 *
 */
public class SaveUpdateHibernateInterceptor extends EmptyInterceptor
{

	/**
	 * This set will contain all the Objects which are inserted in one session associated with the Object of this Class.
	 * These objects will be Added to the processing Queue once the Transaction is Commited.
	 */
	private final Set inserts = new HashSet();

	/**
	 * This set will contain all the Objects which are Updated in one session associated with the Object of this Class.
	 * These objects will be Added to the processing Queue once the Transaction is Commited.
	 */
	private final Set updates = new HashSet();

	/**
	 * Interceptor Thread which will maintain the Queue for processing Objects, Once the transaction is commited all the objects in the
	 * Inserts & Updates set will be added in the Queue of this Thread for further processing.
	 */
	private final SaveUpdateInterceptThread interceptThread = SaveUpdateInterceptThread
			.getInstance();
	/**
	 * logger Logger - Generic logger.
	 */
	private static final Logger LOGGER = Logger
			.getCommonLogger(SaveUpdateHibernateInterceptor.class);

	/**
	 * This method will be called whenever a object is inserted,It will validate whether or not the object inserted
	 * is to be monitored or not & if it is to be monitored then it will add that object to the inserted objects set.
	 * @param entity object which is inserted
	 * @param id id of the object
	 * @param state state of the Object
	 * @param propertyNames names of the properties of the object
	 * @param types types.
	 * @return is successfull
	 */
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames,
			Type[] types)
	{
		// TODO Auto-generated method stub
		if (interceptThread.isObjectTobeMonitored(entity))
		{
			LOGGER.info("SaveUpdateHibernateInterceptor : required object found in save ");
			inserts.add(entity);
		}

		return super.onSave(entity, id, state, propertyNames, types);
	}

	/**
	 * This method will be called whenever a transaction is committed or rollbacked.
	 * It will check if the transaction is commited then will add all the objects in the inserts & updates to the interceptProcessor Queue
	 * for further processing.
	 * @param tx transaction which is committed or rollbacked.
	 *
	 */
	public void afterTransactionCompletion(Transaction tx)
	{
		// TODO Auto-generated method stub
		if (tx.wasCommitted())
		{
			for (Object objInserted : inserts)
			{
				LOGGER
						.info("SaveUpdateHibernateInterceptor : Transaction commited which was containging the required objects in insert");
				interceptThread.addInsertedObject(objInserted);
				// all these are the objects that are inserted so create a message accordingly

			}
			for (Object objUpdated : updates)
			{
				LOGGER
						.info("SaveUpdateHibernateInterceptor : Transaction commited which was containging the required objects in update");
				interceptThread.addUpdatedObject(objUpdated);
				// all these are the objects which are updated so cretae message accrdingly
			}
			//this transaction was containing the specimen object so create a xml & send it to queue
		}
		super.afterTransactionCompletion(tx);
	}

	/**
	 * This method will be called whenever a object is updated,It will validate whether or not the object inserted
	 * is to be monitored or not & if it is to be monitored then it will add that object to the updated objects set.
	 * @param entity object which is inserted
	 * @param id id of the object
	 * @param currentState current state of the Object
	 * @param previousState previous state of the Object
	 * @param propertyNames names of the properties of the object
	 * @param types types.
	 * @return is successfull
	 */
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState,
			Object[] previousState, String[] propertyNames, Type[] types)
	{
		// TODO Auto-generated method stub
		if (interceptThread.isObjectTobeMonitored(entity))
		{
			LOGGER.info("SaveUpdateHibernateInterceptor : required object found in update ");
			updates.add(entity);
		}
		return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
	}

}
