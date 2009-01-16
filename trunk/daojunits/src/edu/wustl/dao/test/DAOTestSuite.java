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
		TestSuite daoSuite = new TestSuite("Test for DAO Layer");
		daoSuite.addTestSuite(HibernateTestCaseForCatissue.class);
		daoSuite.addTestSuite(JDBCTestCasesForCatissue.class);
		//daoSuite.addTestSuite(JDBCTestCasesForCider.class);
		//daoSuite.addTestSuite(HibernateTestCaseForCider.class);
		//return new junit.framework.JUnit4TestAdapter(HibernateInsertTestCase.class);
		return daoSuite;
	}

}
