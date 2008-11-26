package edu.wustl.dao.test;

import java.util.List;

import org.junit.Test;

import edu.wustl.common.util.global.Constants;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.QueryWhereClause;
import edu.wustl.dao.QueryWhereClauseJDBC;

/**
 * @author kalpana_thakur
 *
 */
public class JDBCTestCases extends BaseTestCase
{/*
	*//**
	 * This test will assert the execution of query.
	 *//*
	@Test
	public void testExecuteUpdate()
	{
		try
		{
			jdbcDAO.openSession(null);
			StringBuffer strbuff = new StringBuffer();
			strbuff.append("update xyz_user set EMAIL_ADDRESS ='abc@per.co.in'" +
					" where FIRST_NAME = 'john'");
			jdbcDAO.executeUpdate(strbuff.toString());
			jdbcDAO.commit();
			jdbcDAO.closeSession();
		}
		catch(Exception exp)
		{
			assertFalse("Failed while inserting object :", true);
		}
	}

	*//**
	 * This test will assert that all the objects are retrieved successfully.
	 *//*
	@Test
	public void testCaseRetriveAllObjectsMySQL()
	{
	  try
	  {
		  jdbcDAO.openSession(null);
		  List list = jdbcDAO.retrieve("xyz_user");
	  	  jdbcDAO.closeSession();
	  	  assertNotNull(list);

	  }
	  catch(Exception exp)
	  {
		  assertFalse("Failed while retrieving object :", true);
	  }
	}

	*//**
	 * This test will assert that the object with requested
	 * column will be retrieved successfully.
	 *//*
	@Test
	public void testCaseRetriveObjectMySQL()
	{
	  try
	  {
		jdbcDAO.openSession(null);
	    List<Object> list = jdbcDAO.retrieve("xyz_user","IDENTIFIER" , Long.valueOf(2));
	    jdbcDAO.closeSession();
	  	assertNotNull("No objects retrieved",list);
		assertTrue("No object retrieved ::",list.size() > 0);
	  }
	  catch(Exception exp)
	  {
		  assertFalse("Failed while retrieving object ::", true);
	  }
	}


	*//**
	 * This test will assert that requested columns of the objects are
	 * retrieved successfully.
	 *//*
	@Test
	public void testCaseRetrieveObjectColumnsMySQL()
	{
	  try
	  {
		String[] selectColumnName = {"IDENTIFIER","FIRST_NAME","LAST_NAME","EMAIL_ADDRESS"};
		jdbcDAO.openSession(null);
	    List<Object> list = jdbcDAO.retrieve("xyz_user", selectColumnName);
	    jdbcDAO.closeSession();

	    assertNotNull("No object retrieved ::",list);
		assertTrue("No object retrieved ::",list.size() > 0);
	  }
	  catch(Exception exp)
	  {
		  assertFalse("Failed while retrieving object ::", true);
	  }
	}

	*//**
	 * This test will assert that only distinct rows
	 * retrieved successfully.
	 *//*
	@Test
	public void testCaseRetrieveOnlyDistinctRowsMySQL()
	{
		try
		  {
			String[] selectColumnName = {"IDENTIFIER","FIRST_NAME","LAST_NAME","EMAIL_ADDRESS"};
			jdbcDAO.openSession(null);
		    List<Object> list = jdbcDAO.retrieve("xyz_user", selectColumnName,true);
		    jdbcDAO.closeSession();

		    assertNotNull("No object retrieved ::",list);
			assertTrue("No object retrieved ::",list.size() > 0);
		  }
		  catch(Exception exp)
		  {
			  assertFalse("Failed while retrieving object ::", true);
		  }

	}


	*//**
	 * This test will assert that objects retrieved successfully
	 * when where clause holds in condition.
	 *//*
	@Test
	public void testRetriveInCondition()
	{
		try
		{
			String sourceObjectName = "xyz_user";
			String [] whereColumnNames =  {"IDENTIFIER"};
			String [] colConditions =  { "in"};
			Object [] colValues = {Long.valueOf(2),Long.valueOf(4)};
			Object [] whereColumnValues =  {colValues};
			String[] selectColumnName = null;

			QueryWhereClauseJDBC queryWhereClause = new QueryWhereClauseJDBC();
			queryWhereClause.setWhereClause(whereColumnNames, colConditions,
					whereColumnValues, Constants.AND_JOIN_CONDITION);

			jdbcDAO.openSession(null);
			List<Object> list = jdbcDAO.retrieve(sourceObjectName, selectColumnName,queryWhereClause);
			jdbcDAO.closeSession();
			assertNotNull("No value retrieved :",list);

		}
		catch(Exception exp)
		{
			assertFalse("Failed while retrieving object ::", true);
		}

	}

	*//**
	 * This test will assert that objects retrieved successfully
	 * when where clause holds not null condition.
	 *//*
	@Test
	public void testRetriveIsNotNullConditionMySQL()
	{
		try
		{
			String sourceObjectName = "xyz_user";

			String[] whereColumnNames = {"IDENTIFIER","FIRST_NAME","LAST_NAME","EMAIL_ADDRESS"};
			String[] whereColumnCondition = new String[]{"is not null",
					"is not null","is not null","is not null"};
			Object [] colValues = {};
			String joinCondition = Constants.OR_JOIN_CONDITION;
			Object[] whereColumnValues =  {colValues};
			String[] selectColumnName = null;

			QueryWhereClauseJDBC queryWhereClause = new QueryWhereClauseJDBC();
			queryWhereClause.setWhereClause(whereColumnNames, whereColumnCondition,
					whereColumnValues, joinCondition);

			jdbcDAO.openSession(null);
			List<Object> list = jdbcDAO.retrieve(sourceObjectName, selectColumnName,queryWhereClause);
			jdbcDAO.closeSession();

			assertNotNull("No value retrieved :" + list);
			assertTrue("No object retrieved ::",list.size() > 0);

		}
		catch(Exception exp)
		{
			assertFalse("Failed while retrieving object ::", true);
		}
	}

	*//**
	 * This test will assert that objects retrieved successfully
	 * when where clause holds is null condition.
	 *//*
	@Test
	public void testRetriveIsNullCondition()
	{
		try
		{
			String sourceObjectName = "xyz_user";
			String[] whereColumnNames = {"LAST_NAME"};
			String[] whereColumnCondition = new String[]{"is null"};
			Object [] colValues = {};
			Object[] whereColumnValues =  {colValues};
			String[] selectColumnName = null;
			String joinCondition = Constants.OR_JOIN_CONDITION;

			QueryWhereClauseJDBC queryWhereClause = new QueryWhereClauseJDBC();
			queryWhereClause.setWhereClause(whereColumnNames, whereColumnCondition,
					whereColumnValues, joinCondition);
			jdbcDAO.openSession(null);
			List<Object> list = jdbcDAO.retrieve(sourceObjectName, selectColumnName,queryWhereClause);
			jdbcDAO.closeSession();

			assertNotNull("No object retrieved ::",list);
			assertTrue("No object retrieved ::",list.size() > 0);

		}
		catch(Exception exp)
		{
			assertFalse("Failed while retrieving object ::", true);
		}

	}

	*//**
	 * This test will assert that objects retrieved successfully with given column value
	 * Having equal (=)condition.
	 *//*
	@Test
	public void testRetriveEqualCondition(JDBCDAO jdbcDAO)
	{
		try
		{
			String sourceObjectName = "xyz_user";
			String[] whereColumnNames = {"LAST_NAME"};
			String[] whereColumnCondition = new String[]{" = "};
			Object[] whereColumnValues =  {"Washu"};
			String[] selectColumnName = null;
			String joinCondition = Constants.OR_JOIN_CONDITION;

			QueryWhereClauseJDBC queryWhereClause = new QueryWhereClauseJDBC();
			queryWhereClause.setWhereClause(whereColumnNames, whereColumnCondition,
					whereColumnValues, joinCondition);
			jdbcDAO.openSession(null);
			List<Object> list = jdbcDAO.retrieve(sourceObjectName, selectColumnName,queryWhereClause);
			jdbcDAO.closeSession();

			assertNotNull("No object retrieved ::",list);
			assertTrue("No object retrieved ::",list.size() > 0);
		}
		catch(Exception exp)
		{
			assertFalse("Failed while retrieving object ::", true);
		}

	}



*/}
