/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/catissue-dao/LICENSE.txt for details.
 */

package edu.wustl.common.domain;

import java.util.Calendar;
import java.util.Date;

public class InterceptorErrorObject
{
	private String objectType;
	private String errorCode;
	private Long objectId;
	private Date timestamp= Calendar.getInstance().getTime();
	private boolean recoveryDone = false;
	private Long eventCode=1l;
	private Integer numberOfTry=0;
	private String processorClass ;

	public InterceptorErrorObject(Long identifier,String objectType,String errorCode,Long objId,Long eventCode,Integer numberOfTry, String processorClass)
	{
		id=identifier;
		this.objectType=objectType;
		this.errorCode=errorCode;
		objectId = objId;
		this.eventCode=eventCode;
		this.numberOfTry = numberOfTry;
		this.processorClass = processorClass;
	}

	public InterceptorErrorObject()
	{}


	/**
	 * System generated unique id.
	 */
	private Long id;

	public String getProcessorClass() {
		return processorClass;
	}
	public void setProcessorClass(String processorClass) {
		this.processorClass = processorClass;
	}

	public Integer getNumberOfTry() {
		return numberOfTry;
	}
	public void setNumberOfTry(Integer numberOfTry) {
		this.numberOfTry = numberOfTry;
	}
	public void incrementNumberOfTry()
	{
		numberOfTry++;
	}

	public boolean isValidForTryAgain()
	{
		if(numberOfTry<10)
		{
			return true;
		}
		return false;
	}
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public Long getObjectId() {
		return objectId;
	}
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public boolean getRecoveryDone()
	{
		return recoveryDone;
	}

	public void setRecoveryDone(boolean recoveryDone)
	{
		this.recoveryDone = recoveryDone;
	}
	public Long getEventCode() {
		return eventCode;
	}
	public void setEventCode(Long eventCode) {
		this.eventCode = eventCode;
	}

}
