/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/catissue-dao/LICENSE.txt for details.
 */

package edu.wustl.dao.test.interceptor;

import edu.wustl.dao.interceptor.InterceptProcessor;
import edu.wustl.dao.interceptor.SaveUpdateInterceptThread.eventType;

public class UserInterceptProcessor implements InterceptProcessor{

	public static final String IS_PROCESSOR_EXECUTED_INSERT="INTERCEPT_PROCESSOR_INSERT";
	public static final String IS_PROCESSOR_EXECUTED_UPDATE="INTERCEPT_PROCESSOR_INSERT";
	public void process(Object arg0, eventType type)
	{
		System.out.println(" in person intercept processor");
		// TODO Auto-generated method stub
		if(eventType.onInsert.equals(type))
		{
			//System.setProperty(IS_PROCESSOR_EXECUTED_INSERT, "true");
		}
		else
		{
			//System.setProperty(IS_PROCESSOR_EXECUTED_UPDATE, "true");
		}

	}
	public void onError(String objectType, eventType type, Long objectId)
	{
		// TODO Auto-generated method stub
		System.out.println("in error recovery method");

	}

}
