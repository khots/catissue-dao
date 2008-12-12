package edu.wustl.dao.test;

import junit.framework.TestSuite;

/**
 * @author kalpana_thakur
 *TODO
 */
public class DAOTestSuite
{

	/**
	 * default constructor.
	 */
	DAOTestSuite ()
	{
		super();
	}
	/**
	 * @param args :
	 */
	public static void main(String[] args)
	{
		//org.junit.runner.JUnitCore.main("edu.wustl.dao.test.HibernateInsertTestCase");

		junit.swingui.TestRunner.run(DAOTestSuite.class);
	}

	/**
	 * @return daoSuite.
	 */
	public static junit.framework.Test suite()
	{
	/*	JVMTIInterface jvmti = new JVMTIInterface();
		  Object obj = TestDomainObject.createTestInstance();
		  Map snapshot1=null;
		  Map snapshot2=null;*/

		/*  snapshot1=jvmti.produceInventory();

		  // Whatever code you need to test

		  snapshot2 = jvmti.produceInventory();

		  assertTrue("Produced unexpected memory",
		    jvmti.compareInventories(
		      System.out, snapshot1, snapshot2, null, null, null));*/
		
		TestSuite daoSuite = new TestSuite("Test for DAO Layer");
		daoSuite.addTestSuite(HibernateTestCase.class);
		daoSuite.addTestSuite(JDBCTestCases.class);
	//	daoSuite.addTestSuite(JDBCTestCasesForIIDB.class);
		//return new junit.framework.JUnit4TestAdapter(HibernateInsertTestCase.class);
		return daoSuite;
	}

}
