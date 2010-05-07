/*
 *
 */

package edu.wustl.common.audit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import edu.wustl.common.audit.util.AuditUtil;
import edu.wustl.common.audit.util.MetadataParser;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.domain.AuditDataEventLog;
import edu.wustl.common.domain.AuditEvent;
import edu.wustl.common.domain.AuditEventDetails;
import edu.wustl.common.domain.LoginDetails;
import edu.wustl.common.domain.LoginEvent;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.AuditException;
import edu.wustl.dao.util.DAOConstants;
import edu.wustl.dao.util.HibernateMetaData;


/**
 * AuditManager is an algorithm to figure out the changes with respect to database due to
 * insert, update or delete data from/to database.
 * @author kalpana_thakur
 */
public class AuditManager // NOPMD
{
	/**
	 * LOGGER Logger - Generic LOGGER.
	 */
	private static final Logger LOGGER = Logger.getCommonLogger(AuditManager.class);
	/**
	 *  Instance of Audit event.
	 * All the change under one database session are added under this event.
	 *
	 */
	private AuditEvent auditEvent;

	/**
	 * Instance of login event.
	 * Login event changes.
	 *
	 */
	private LoginEvent loginEvent;

	/**
	 * @return the loginEvent.
	 */
	public LoginEvent getLoginEvent()
	{
		return loginEvent;
	}


	/**
	 * @param loginEvent the loginEvent to set.
	 */
	public void setLoginEvent(final LoginEvent loginEvent)
	{
		this.loginEvent = loginEvent;
	}

	/**
	 * @param auditEvent the auditEvent to set
	 */
	public void setAuditEvent(final AuditEvent auditEvent)
	{
		this.auditEvent = auditEvent;
	}


	/**
	 * This method is called to obtain Audit event.
	 * @return Audit event.
	 */
	public AuditEvent getAuditEvent()
	{
		return auditEvent;
	}

	/**
	 * It holds the hibernate metadata for the application.
	 */
	private HibernateMetaData  hibernateMetaData;


	/**
	 * Collection of all the Object classes.
	 */
	private static Collection<AuditableClass>  auditableClasses = new ArrayList<AuditableClass>();

	/**
	 * Initializes the auditable Metadata.
	 * @param metadataCfg : metadataCfg.
	 * @throws AuditException throws if auditablemetadata.xml not found or unable to read.
	 */
	public static void init(final String... metadataCfg) throws AuditException
	{
		//Get the instance of AuditableMetaData to read the
		//auditable properties of the domain objects
		MetadataParser parser = null;
		if(metadataCfg.length > 0)
		{
			parser = new MetadataParser(metadataCfg[0]);
		}
		else
		{
			parser = new MetadataParser();
		}

		final AuditableMetaData metadata = parser.getAuditableMetaData();

		auditableClasses.addAll(metadata.getAuditableClass());
	}

	/**
	 * Instantiate a new instance of AuditManager.
	 * */
	public AuditManager()
	{
		auditEvent = new AuditEvent();
	}

	/**
	 * Instantiate a new instance of AuditManager.
	 * @param hibernateMetaData application specific hibernate metadata.
	 * @param sessionDataBean Bean holding session details like IP address,
	 * user Id, application name.
	 * */
	public AuditManager(final SessionDataBean sessionDataBean,
			final HibernateMetaData hibernateMetaData)
	{
		this();
		this.hibernateMetaData = hibernateMetaData;
		initializeAuditManager(sessionDataBean);

	}

	/**
	 * Set the id of the logged-in user who performed the changes.
	 * @param userId System identifier of logged-in user who performed the changes.
	 * */
	public void setUserId(final Long userId)
	{
		auditEvent.setUserId(userId);
	}
	/**
	 * Set the IP address of the machine from which the event was performed.
	 * @param iPAddress IP address of the machine to set.
	 * */
	public void setIpAddress(final String iPAddress)
	{
		auditEvent.setIpAddress(iPAddress);
	}

	/**
	 * This method returns the object value.
	 * @param obj method object.
	 * @return Object returns the value of the object.
	 * @throws AuditException Exception occurred while auditing.
	 */
	private String getObjectValue(final Object obj) throws AuditException
	{
		Object reqValue = null;
		if (AuditUtil.isVariable(obj))
		{
			reqValue = obj;
		}
		else
		{
			reqValue = getObjectId(obj);
		}
		return reqValue.toString();
	}


	/**
	 * Compares the contents of two objects.
	 * @param currentObj Current state of object.
	 * @param previousObj Previous state of object.
	 * @param eventType This method is called to set the event Type.
	 * @throws AuditException Audit Exception.
	 */
	public void audit(final Object currentObj, final Object previousObj,final String eventType)
	throws AuditException
	{
			auditEvent.setEventType(eventType);
		   	LOGGER.debug("Inside isObjectAuditable method.");
			if (currentObj == null)
			{
				throw new AuditException(ErrorKey.getErrorKey
						("problem.in.audit.invalid.instance"),null, "");

			}
			// Set the table name of the current class.
			final Object currentAuditableObject  =
				HibernateMetaData.getProxyObjectImpl(currentObj);

			if (previousObj != null &&
					!currentAuditableObject.getClass().equals(previousObj.getClass()))
			{
				throw new AuditException
				(ErrorKey.getErrorKey("problem.in.audit.diff.class.type"),null,
					previousObj.getClass().getName()+":"+currentAuditableObject
					.getClass().getName());
			}

			obtainAuditableEventLog(currentAuditableObject, previousObj);
	}


	/**
	 * Process each getter Methods to find the change from previous value to current value.
	 * @param obj current Object.
	 * @param previousObj previous Object.
	 * @return audit Event Details Collection.
	 * @throws AuditException Audit Exception.
	 */
	private AuditDataEventLog obtainAuditableEventLog(final Object obj,
			final Object previousObj) throws AuditException
	{
		LOGGER.debug("Inside obtainAuditableEventLog method.");
		// An audit event will contain many logs.
		final AuditDataEventLog auditEventLog = new AuditDataEventLog();

		// Get instance of the Castor class of the object being audited
		//AuditableClass auditableClass = null;

		boolean isClassFound = false;
		AuditableClass auditableClass = null;
		if (auditableClasses != null)
		{
			final Iterator<AuditableClass> classListIterator = auditableClasses.iterator();
			while (classListIterator.hasNext())
			{
				auditableClass = classListIterator.next();
				if (obj.getClass().getName()
					.equals(auditableClass.getClassName()))
				{
					isClassFound = true;
					if(auditableClass.getIsAuditable())
					{
						startAuditing(obj, previousObj, auditEventLog,
						auditableClass);
					}
					break;
				}
			}
			//Class not exist.
			if(!isClassFound)
			{
				LOGGER.error("Class "+ obj.getClass().getName()+
						" missing in auditablemetadata.xml.");
				throw new AuditException(ErrorKey.getErrorKey
							("class.missing"),null, obj.getClass().getName());
			}

		}//auditablemetadata.xml not loaded, if AuditManager.init() not called.
		else
		{
			LOGGER.error("auditablemetadata.xml is not loaded. Please initialize it before auditing." +
					" Call AuditManager.init()");
			throw new AuditException(ErrorKey.getErrorKey
				("metadata.not.loaded"),null, "");
		}

		if(auditableClass != null && auditableClass.getIsAuditable())
		{
		 auditEventLog.setAuditEvent(auditEvent);
		 auditEvent.getAuditEventLogCollection().add(auditEventLog);
		}
		return auditEventLog;
	}

	/**
	 * This method will be called to get the Auditable class.
	 * @param obj Object to audit
	 * @return AuditableClass.
	 * @throws AuditException AuditException.
	 */
	public Long getObjectId(final Object obj) throws AuditException
	{
		LOGGER.info("obj.getClass().getName() :"+obj.getClass().getName());
		boolean isClassFound = false;
		Long auditableClassId = Long.valueOf(-1);
		if(obj != null)
		{
			if (auditableClasses != null)
			{
				final Iterator<AuditableClass> classListIterator = auditableClasses.iterator();
				while (classListIterator.hasNext())
				{
					final AuditableClass auditableClass = classListIterator.next();
					if (obj.getClass().getName()
							.equals(auditableClass.getClassName()))
					{
						isClassFound = true;
						auditableClassId = (Long)auditableClass.invokeGetterForId(obj);
						//need to remove this check in future !!
						if(auditableClassId == null)
						{
							auditableClassId = Long.valueOf(-1);
						}
						break;
					}
				}
			}

			//Class not exist.
			if(!isClassFound)
			{
				LOGGER.error("Class "+ obj.getClass().getName()+
				" missing in auditablemetadata.xml.");

				throw new AuditException(ErrorKey.getErrorKey
						("class.missing"),null, obj.getClass().getName());

			}
		}
		return auditableClassId;
	}

	/**
	 * Audits the given object according to the mappings specified by AuditableClass instance.
	 * @param obj AuditableObject.
	 * @param previousObj AuditableObject.
	 * @param auditEventLog AuditDataEventLog object.
	 * @param auditableClass AuditableClass object.
	 * @throws AuditException throw AuditException.
	 */
	private void startAuditing(final Object obj, final Object previousObj,
			final AuditDataEventLog auditEventLog, final AuditableClass auditableClass) throws AuditException
	{
		LOGGER.debug("Inside startAuditing method.");

		if(obj instanceof AbstractDomainObject)
		{
		    final AbstractDomainObject domainObject=(AbstractDomainObject)obj;
		    auditEventLog.setObjectIdentifier(domainObject.getId());
		}

		auditEventLog.setObjectName(hibernateMetaData
				.getTableName(obj.getClass()));

		final Object currentObj = HibernateMetaData
				.getProxyObjectImpl(obj);

		//Audit simple attributes of the object
		auditSimpleAttributes(previousObj, auditEventLog, auditableClass,
				currentObj);

		//Audit reference associations of the object
		auditReferenceAssociations(previousObj, auditEventLog, auditableClass, currentObj);

		//Audit containment associations of the object
		auditContainmentAssociation(previousObj, auditEventLog, auditableClass,
				currentObj);
	}
	/**
	 * Audits containment relations defined for the object,
	 * as mentioned in the auditableMetadata.xml.
	 * It audits the complete object within collection
	 * and also previous and current identifiers list.
	 * @param previousObj AuditableObject.
	 * @param auditEventLog AuditDataEventLog object.
	 * @param auditableClass AuditableClass object.
	 * @param currentObj Object.
	 * @throws AuditException throw AuditException.
	 */
	private void auditContainmentAssociation(final Object previousObj, final AuditDataEventLog auditEventLog,
			final AuditableClass auditableClass, final Object currentObj) throws AuditException
	{
		LOGGER.debug("Inside auditContainmentAssociation method.");
		if (auditableClass.getContainmentAssociationCollection() != null
				&& !auditableClass.getContainmentAssociationCollection().isEmpty())
		{
			final Iterator<AuditableClass> containmentItert =
				auditableClass.getContainmentAssociationCollection()
			.iterator();

			while (containmentItert.hasNext())
			{
				final AuditableClass containmentClass = containmentItert.next();
				final Object currentAuditableObject = auditableClass.invokeGetterMethod(
						containmentClass.getRoleName(), currentObj);

				//Case of Insert : when previous object is null.
				if(previousObj == null)
				{
					//To audit the ids.
					auditRefrenceAssociationforNewEntry(auditEventLog,
							currentAuditableObject);
					//To audit the other entries.
					auditContainmentsforNewEntry(auditEventLog,
							currentAuditableObject);
				}
				else if(currentAuditableObject != null)
				{
					//case of update
					final Object previousAuditableObject = auditableClass.
					invokeGetterMethod(containmentClass.getRoleName(),previousObj);
					//To audit the ids.
					auditRefrenceAssociationforExistingEntries(auditEventLog,
							currentAuditableObject, previousAuditableObject);
					//To audit the other entries.
					auditContainmentsforExistingEntries(auditEventLog,
							currentAuditableObject, previousAuditableObject);
				}

			}
		}
	}
	/**
	 * This method will be called while auditing new entry for containment association..
	 * @param auditEventLog AuditDataEventLog
	 * @param currentAuditableObject  current auditable object
	 * @param previousAuditableObject previous auditable object.
	 * @throws AuditException Throws audit Exception.
	 */
	private void auditContainmentsforExistingEntries(
			final AuditDataEventLog auditEventLog, final Object currentAuditableObject,
			final Object previousAuditableObject) throws AuditException
			{
		LOGGER.debug("Inside auditContainmentsforExistingEntries method.");
		//for one to many containment Associations.
		if ((currentAuditableObject instanceof Collection)
				&& (previousAuditableObject instanceof Collection))
		{

			//Audit collection entries.
			auditEventLog.getAuditDataEventLogs().addAll(
					auditContainment(currentAuditableObject, previousAuditableObject)) ;

		}//for one to one containment Associations.
		else
		{

			final AuditDataEventLog childAuditEventLog = obtainAuditableEventLog(
					currentAuditableObject, previousAuditableObject);
			auditEventLog.getAuditDataEventLogs().add(
					childAuditEventLog);

		}
	}
	/**
	 * This method will be called while auditing new entry for containment association..
	 * @param auditEventLog AuditDataEventLog
	 * @param currentAuditableObject  currentAuditableObject
	 * @throws AuditException Throws audit Exception.
	 */
	private void auditContainmentsforNewEntry(final AuditDataEventLog auditEventLog,
			final Object currentAuditableObject) throws AuditException
	{
		LOGGER.debug("Inside auditContainmentsforNewEntry method.");
		//for one to many containment Associations.
		if (currentAuditableObject instanceof Collection)
		{
			for (final Object object : (Collection) currentAuditableObject)
			{

					//Call to obtainAuditableEventLog to audit the object of collection.
					final AuditDataEventLog childAuditEventLog = obtainAuditableEventLog(
							object, null);
					auditEventLog.getAuditDataEventLogs().add(
							childAuditEventLog);

			}
		}//for one to one containment Associations.
		else if(currentAuditableObject != null)
		{

			//Call to obtainAuditableEventLog to audit the object of collection.
			final AuditDataEventLog childAuditEventLog = obtainAuditableEventLog(
					 currentAuditableObject, null);
			auditEventLog.getAuditDataEventLogs().add(
					childAuditEventLog);

		}
	}

	/**
	 * Returns the collection values of objects having association.
	 * relationship with AuditableObject.
	 * @param currentContainedObj Object.
	 * @return List of String values.
	 * @throws AuditException AuditException.
	 */
	private String getColonSeparatedIds(final Collection<Object> currentContainedObj)
	throws AuditException
	{
		final StringBuffer colonSeparatedIds = new StringBuffer("");
		final Iterator<Object> itr = currentContainedObj.iterator();
		while(itr.hasNext())
		{
			final Object auditableObject = itr.next();
			final Object objectId = getObjectId(auditableObject);
			if(objectId != null)
			{
				colonSeparatedIds.append(objectId.toString());
				if(itr.hasNext())
				{
					colonSeparatedIds.append(":");
				}
			}

		}
		return colonSeparatedIds.toString();
	}



	/**
	 * Audits reference relations defined for the object,
	 * as mentioned in the AuditableMetadata.xml.
	 * Only the identifier get audited.
	 * @param previousObj AuditableObject.
	 * @param auditEventLog AuditDataEventLog object.
	 * @param auditableClass AuditableClass object.
	 * @param currentObj Object.
	 * @throws AuditException throw AuditException.
	 */
	private void auditReferenceAssociations(final Object previousObj, final AuditDataEventLog auditEventLog,
			final AuditableClass auditableClass, final Object currentObj) throws AuditException
	{
		LOGGER.debug("Inside auditReferenceAssociations method.");
		if (auditableClass.getReferenceAssociationCollection() != null
				&& !auditableClass.getReferenceAssociationCollection().isEmpty())
		{

			final Iterator<AuditableClass> associationItert =
				auditableClass.getReferenceAssociationCollection().iterator();
			while (associationItert.hasNext())
			{
				final AuditableClass refrenceAssociation = associationItert.next();

				final Object currentAuditableObject = auditableClass.invokeGetterMethod(
						refrenceAssociation.getRoleName(), currentObj);

	            //Case of Insert : when previous object is null.
				if(previousObj == null)
				{

					auditRefrenceAssociationforNewEntry(auditEventLog,
							currentAuditableObject);
				}
				else if(currentAuditableObject != null)
					// Case of update : having both current and previous objects:
				{
					final Object prevAuditableObject = auditableClass.
					invokeGetterMethod(refrenceAssociation.getRoleName(),previousObj);

					auditRefrenceAssociationforExistingEntries(auditEventLog,
							currentAuditableObject, prevAuditableObject);
				}

			}
		}
	}
	/**
	 * This method will be called while auditing new entry for reference association..
	 * @param auditEventLog AuditDataEventLog
	 * @param currentAuditableObject  current auditable object
	 * @param prevAuditableObject previous auditable object.
	 * @throws AuditException AuditException
	 */
	private void auditRefrenceAssociationforExistingEntries(
			final AuditDataEventLog auditEventLog, final Object currentAuditableObject,
			final Object prevAuditableObject) throws AuditException
	{
		//for one to many reference association.
		if ((currentAuditableObject instanceof Collection) &&(prevAuditableObject instanceof Collection))
		{
			if(!(((Collection)currentAuditableObject).isEmpty() &&
					((Collection)prevAuditableObject).isEmpty()))
			{
				//Audit identifiers of current and previous objects of collections.
				final String containmentCollectionObjectName =
					AuditUtil.getAssociationCollectionObjectName(
						(Collection)currentAuditableObject,
						(Collection)prevAuditableObject);

				final AuditEventDetails auditEventDetails =
					auditRefrenceAssociationsIds(getColonSeparatedIds
							((Collection)currentAuditableObject),
						getColonSeparatedIds((Collection)prevAuditableObject),
						containmentCollectionObjectName);
				auditEventDetails.setAuditEventLog(auditEventLog);
				auditEventLog.getAuditEventDetailsCollection().add(auditEventDetails);
			}

		}//for one to one reference association.
		else
		{
			//Audit identifiers of current and previous objects.
			LOGGER.info("currentAuditableObject.getClass().getName()"
					+currentAuditableObject.getClass().getName());
            String previousAuditableObjectId = DAOConstants.EMPTY_STRING;
			if(prevAuditableObject != null)
			{
				previousAuditableObjectId = getObjectId(prevAuditableObject).toString();
			}
			final AuditEventDetails auditEventDetails =
			auditRefrenceAssociationsIds((getObjectId(currentAuditableObject)).toString(),
				previousAuditableObjectId,currentAuditableObject.getClass().getName());
			auditEventDetails.setAuditEventLog(auditEventLog);
			auditEventLog.getAuditEventDetailsCollection().add(auditEventDetails);

		}
	}
	/**
	 * This method will be called while auditing new entry for refrence association..
	 * @param auditEventLog AuditDataEventLog
	 * @param currentAuditableObject  currentAuditableObject
	 * @throws AuditException AuditException;
	  */
	private void auditRefrenceAssociationforNewEntry(
			final AuditDataEventLog auditEventLog, final Object currentAuditableObject) throws AuditException
	{
		//for one to many Reference Associations.
		if (currentAuditableObject instanceof Collection)
		{
			final String associationObjectName = AuditUtil.getAssociationCollectionObjectName(
					(Collection)currentAuditableObject,null);
			if(!(((Collection)currentAuditableObject).isEmpty()))
			{
				//Audit identifiers of current and previous objects of collections.
				final AuditEventDetails auditEventDetails =
					auditRefrenceAssociationsIds(getColonSeparatedIds
						((Collection)currentAuditableObject),null,associationObjectName);
				auditEventDetails.setAuditEventLog(auditEventLog);
				auditEventLog.getAuditEventDetailsCollection().add(auditEventDetails);
			}
		}//for one to one Reference Associations.
		else if(currentAuditableObject != null)
		{
			//Audit identifiers of current and previous objects.
			LOGGER.info("currentAuditableObject.getClass().getName()"
					+currentAuditableObject.getClass().getName());
			final AuditEventDetails auditEventDetails =
			auditRefrenceAssociationsIds((getObjectId(currentAuditableObject))
				.toString(),	null,
				currentAuditableObject.getClass().getName());
			auditEventDetails.setAuditEventLog(auditEventLog);
			auditEventLog.getAuditEventDetailsCollection().add(auditEventDetails);

		}
	}


	/**
	 * Audits the simple attributes of the auditableObject.
	 * defined in the auditableMetadata.xml.
	 * @param previousObj AuditableObject.
	 * @param auditEventLog AuditDataEventLog object.
	 * @param auditableClass AuditableClass object.
	 * @param currentObj Object.
	 * @throws AuditException throw AuditException.
	 */
	private void auditSimpleAttributes(final Object previousObj, final AuditDataEventLog auditEventLog,
			final AuditableClass auditableClass, final Object currentObj) throws AuditException
	{
		if (auditableClass.getAttributeCollection() != null
				&& !auditableClass.getAttributeCollection().isEmpty())
		{
			for (final Attribute attribute : auditableClass.getAttributeCollection())
			{
				// Get the old value of the attribute from previousObject
				final Object prevVal = auditableClass.invokeGetterMethod(
						attribute.getName(), previousObj);
				//prevVal = getObjectValue(null, prevVal);
				// Get the current value of the attribute from currentObject
				final Object currVal = auditableClass.invokeGetterMethod(
						attribute.getName(), currentObj);
				//currVal = getObjectValue(null, currVal);

				final String columnName = getColumnName(previousObj, currentObj,
						attribute);

				// Case of transient object
				if (!(DAOConstants.EMPTY_STRING.equals(columnName)))
				{
					if(columnName.equalsIgnoreCase("IDENTIFIER") && currVal != null )
					{
					 auditEventLog.setObjectIdentifier(Long.valueOf(currVal.toString()));
					}
					// Compare the old and current value
					final AuditEventDetails auditEventDetails = compareValue(prevVal, currVal);
					if (auditEventDetails != null)
					{
					  auditEventDetails.setElementName(columnName);
					  auditEventDetails.setAuditEventLog(auditEventLog);
					  auditEventLog.getAuditEventDetailsCollection().add(auditEventDetails);
					}
				}
			}
		}
	}

	/**
	 * This method will be called to retrieve the column name.
	 * @param previousObj previous object to audit.
	 * @param currentObj current object to audit
	 * @param attribute column name and value.
	 * @return the column name.
	 */
	private String getColumnName(final Object previousObj, final Object currentObj,
			final Attribute attribute)
	{
		// Find the corresponding column in the database
		String columnName = "";
		if(currentObj == null)
		{
			columnName = hibernateMetaData.getColumnName(
					previousObj.getClass(),attribute.getName());
		}
		else
		{
			columnName = hibernateMetaData.getColumnName(
					currentObj.getClass(), attribute.getName());
		}
		return columnName;
	}



	/**
	 * This function compares the prevVal object and currVal object
	 * and if there is any change in value then create the AuditEvenDetails object.
	 * @param prevObject previous value.
	 * @param currentObject current value.
	 * @return AuditEventDetails. Audit event details.
	 * @throws AuditException  Exception while auditing.
	 */
	private AuditEventDetails compareValue(final Object prevObject, final Object currentObject) throws AuditException
	{
		//here is the problem.
		AuditEventDetails auditEventDetails = null;
		if (prevObject == null && currentObject != null)
		{
			auditEventDetails = new AuditEventDetails();
			auditEventDetails.setCurrentValue(getObjectValue(currentObject));
		}
		else if(currentObject == null && prevObject != null)
		{
			auditEventDetails = new AuditEventDetails();
			auditEventDetails.setPreviousValue(getObjectValue(prevObject));
		}
		else if(prevObject != null && currentObject != null)
		{
			final String previousVal = getObjectValue(prevObject);
			final String currentVal = getObjectValue(currentObject);
			if(!previousVal.equals(currentVal))
			{
				auditEventDetails = new AuditEventDetails();
				auditEventDetails.setPreviousValue(previousVal);
				auditEventDetails.setCurrentValue(currentVal);
			}
		}

		return auditEventDetails;
	}

	/**
	 * This method returns the String representation of Collection values with.
	 * Previous Collection values and Current Collection values.
	 * @param currentCollectionIds Current Collection values.
	 * @param prevCollectionIds Previous Collection values.
	 * @param attributeName Name of the attribute
	 * @return AuditEventDetails Object.
	 */
	private AuditEventDetails auditRefrenceAssociationsIds
		(final String currentCollectionIds,final String prevCollectionIds, final String attributeName)
	{
		final AuditEventDetails auditEventDetails = new AuditEventDetails();
		auditEventDetails.setElementName(attributeName+"_PREV_CURR_IDS_LIST");
		if(currentCollectionIds == null)
		{
			auditEventDetails.setCurrentValue("");
		}
		else
		{
			auditEventDetails.setCurrentValue(currentCollectionIds);
		}
		if(prevCollectionIds == null)
		{
			auditEventDetails.setPreviousValue("");
		}
		else
		{
			auditEventDetails.setPreviousValue(prevCollectionIds);
		}


		return auditEventDetails;
	}
	/**
	 * Audit entities within containment collection.
	 * @param currentObjColl Current Contained Object collection.
	 * @param prevObjColl Previous object Collection.
	 * @return AuditDataEventLog object which represents current values and previous values.
	 * @throws AuditException throw AuditException.
	 */
	private Collection<AuditDataEventLog> auditContainment(final Object currentObjColl,
			final Object prevObjColl) throws AuditException
	{
		final AuditDataEventLog auditEventLog = new AuditDataEventLog();

		for (final Object currentObject : (Collection) currentObjColl)
		{
			boolean isExists = false ;
			for(final Object previousObject : (Collection)prevObjColl)
			{
				//Call to obtainAuditableEventLog to audit the object of collection.
				final Object currentObjectId = getObjectId(currentObject);
			 if(currentObjectId != null && currentObjectId.equals(getObjectId(previousObject)))
			 {

					auditEventLog.getAuditDataEventLogs().
					add(obtainAuditableEventLog(currentObject,
							previousObject));
					isExists = true;
					break;

			 }
			}// If it is new entry in collection then add it to DB with previous value as NULL.
			if(!isExists)
			{
				//Call to obtainAuditableEventLog to audit the new object of collection.
				auditEventLog.getAuditDataEventLogs().
				add(obtainAuditableEventLog( currentObject,
						null));
			}
		}
		return auditEventLog.getAuditDataEventLogs() ;
	}

	/**
	 * Sets the LoginDetails.
	 * @param loginDetails LoginDetails object to set.
	 */
	public void setLoginDetails(final LoginDetails loginDetails)
	{
		loginEvent = new LoginEvent();
		loginEvent.setIpAddress(loginDetails.getIpAddress());
		loginEvent.setSourceId(loginDetails.getSourceId());
		loginEvent.setUserLoginId(loginDetails.getUserLoginId());
	}
	/**
	 * This method will be called to return the Audit manager.
	 * @param sessionDataBean SessionDataBean sessionDataBean object
	 */
	public void initializeAuditManager(final SessionDataBean sessionDataBean)
	{
		if (sessionDataBean != null)
		{
			setUserId(sessionDataBean.getUserId());
			setIpAddress(sessionDataBean.getIpAddress());
		}
	}

	/**
	 * This method is called to check if object need to audit or not.
	 * @param obj object.
	 * @throws AuditException exception thrown while auditing.
	 */
	public void isObjectAuditable(final Object obj) throws AuditException
	{

		if (auditableClasses != null)
		{
			final Iterator<AuditableClass> classListIterator = auditableClasses.iterator();
			while (classListIterator.hasNext())
			{
				final AuditableClass auditableClass = classListIterator.next();
				//System.out.println("auditableClass :"+ auditableClass.getClassName());
				if (obj.getClass().getName()
					.equals(auditableClass.getClassName()) && auditableClass.getIsAuditable())
				{
					LOGGER.error("Class "+ obj.getClass().getName()+
					"is present in auditablemetadata.xml and set to auditable." +
					" Audit all it's instance while inserting/updating it to database.");
					throw new AuditException(ErrorKey.getErrorKey
						("class.auditable"),null, obj.getClass().getName());
				}
			}
		}

	}

}