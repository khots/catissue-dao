package edu.wustl.dao.test;

import junit.framework.TestSuite;

public class DAOTestSuite {
	
	public static void main(String[] args)
	{
		//org.junit.runner.JUnitCore.main("edu.wustl.dao.test.HibernateInsertTestCase");

		junit.swingui.TestRunner.run(DAOTestSuite.class);
	}
	
	public static junit.framework.Test suite()
	{
		TestSuite daoSuite = new TestSuite("Test for DAO Layer");
		daoSuite.addTestSuite(HibernateTestCase.class);
				
		//return new junit.framework.JUnit4TestAdapter(HibernateInsertTestCase.class);
		return daoSuite;
	}

}
