package edu.wustl.dao.newdao;


public enum ActionStatus 
{
	SUCCESSFUL,FAIL;
	
	public boolean isSuccessful()
	{
		return this == SUCCESSFUL;
	}
	
}
