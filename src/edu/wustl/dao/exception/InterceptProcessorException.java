/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/catissue-dao/LICENSE.txt for details.
 */

package edu.wustl.dao.exception;


public class InterceptProcessorException extends Exception
{

	/**
	 * Default serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	private String errorCode;

	private Long objId;


	/**
	 * The public constructor to restrict creating object without
	 * initializing mandatory members.
	 * It will called on occurrence of database related exception.
	 * @param errorKey : key assigned to the error
	 * @param exception :exception
	 * @param msgValues : message displayed when error occurred
	 */
	public InterceptProcessorException(String errorCode,Long objId,Exception e, String message)
	{
		super( message,e);
		this.errorCode = errorCode;
		this.objId=objId;
	}


	 public String getErrorCode() {
		return errorCode;
	}
	 public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	 public Long getObjId() {
		return objId;
	}

	 public void setObjId(Long objId) {
		this.objId = objId;
	}
}
