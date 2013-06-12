/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/catissue-dao/LICENSE.txt for details.
 */

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
		//daoSuite.addTestSuite(HibernateTestCaseForCider.class);
		//daoSuite.addTestSuite(JDBCTestCasesForCider.class);
		//return new junit.framework.JUnit4TestAdapter(HibernateInsertTestCase.class);
		return daoSuite;
	}

}
