package edu.wustl.dao.test;

import java.util.List;

import org.junit.Test;

import test.User;
import edu.wustl.common.util.global.Constants;
import edu.wustl.dao.HibernateDAOImpl;
import edu.wustl.dao.QueryWhereClauseImpl;

/**
 * @author kalpana_thakur
 *
 */
public class HibernateTestCase extends BaseTestCase
{

	/**
	 * This test will assert that Object inserted successfully.
	 */
	@Test
	public void testCaseInsertObject()
	{
		try
		{
		  User user = (User)createUserObject();
		  dao.openSession(null);
		  dao.insert(user, null, false, false);
		  dao.commit();
		  dao.closeSession();
		  assertTrue("Object Inserted :", true);

		}
		catch(Exception exp)
		{
			assertFalse("Failed while inserting object :", true);
		}

	}

	/**
	 * This test will assert that Object updated successfully.
	 */
	@Test
	public void testCaseUpdateObject()
	{
		try
		{

		  User user = new User();
		  user.setId(Long.valueOf(6));
		  user.setFirstName("Srikanth");
		  user.setLastName("Adiga");
		  user.setEmailAddress("sri.adiga@persistent.co.in");

		  dao.openSession(null);
		  	  dao.update(user);
		  	  dao.commit();
		  dao.closeSession();
		  assertTrue("Object updated :", true);
		}
		catch(Exception exp)
		{
			assertFalse("Failed while updating object :", true);
		}

	  }

	/**
	 * This test will assert that all the objects are retrieved successfully.
	 */
	@Test
	public void testCaseRetriveAllObjects()
	{
	  try
	  {
		  dao.openSession(null);
	  	  List<Object> list = dao.retrieve("test.User");
	  	  dao.closeSession();

	  	  assertNotNull("No objects retrieved :",list);
	  	  assertTrue("No object retrieved ::", list.size() >= 1);
	   }
	  catch(Exception exp)
	  {
		  assertFalse("Failed while retrieving object :", true);
	  }
	}

	/**
	 * This test will assert that the object with requested
	 * column will be retrieved successfully.
	 */
	@Test
	public void testCaseRetriveObject()
	{
	  try
	  {
	    dao.openSession(null);
	    List<Object> list = dao.retrieve("test.User","id" , Long.valueOf(2));
	  	dao.closeSession();
	  	assertNotNull("No objects retrieved",list);
		assertTrue("No object retrieved ::",list.size() >= 1);
	  }
	  catch(Exception exp)
	  {
		  assertFalse("Failed while retrieving object ::", true);
	  }
	}

	/**
	 * This test will assert that requested columns of the objects are
	 * retrieved successfully.
	 */
	@Test
	public void testCaseRetrieveObjectColumns()
	{
	  try
	  {
		String[] selectColumnName = {"id","firstName","lastName","emailAddress"};
		dao.openSession(null);
	    List<Object> list = dao.retrieve("test.User", selectColumnName);
	    dao.closeSession();

	    assertNotNull("No object retrieved ::",list);
		assertTrue("No object retrieved ::",list.size() >= 1);
	  }
	  catch(Exception exp)
	  {
		  assertFalse("Failed while retrieving object ::", true);
	  }
	}

	/**
	 * This test will assert that object will given identifier
	 * retrieved successfully.
	 */
	@Test
	public void testCaseRetrieveObjectAsPerID()
	{
		try
		{
			dao.openSession(null);
			User user  = (User)dao.retrieve("test.User", Long.valueOf(2));
			dao.closeSession();
			assertNotNull(user);
			assertTrue("No object retieved ::",user != null);
		}
		catch(Exception exp)
		{
			assertFalse("Failed while retrieving object ::", true);
		}

	}

	/**
	 * This test will assert that object will given identifier
	 * retrieved successfully.
	 */
	@Test
	public void testCaseLoadCleanObject()
	{
		try
		{
			dao.openSession(null);
			HibernateDAOImpl hiberDao = (HibernateDAOImpl)dao;
			User user  = (User)hiberDao.loadCleanObj("test.User", Long.valueOf(2));
			dao.closeSession();

			assertNotNull(user);
			assertTrue("No object retieved ::",user != null);
		}
		catch(Exception exp)
		{
			assertFalse("Failed while retrieving object ::", true);
		}

	}


	/**
	 * This test will assert that objects retrieved successfully
	 * when where clause holds in condition.
	 */
	@Test
	public void testRetriveInCondition()
	{
		try
		{
			String sourceObjectName = "test.User";
			String [] whereColumnNames =  {"id"};
			String [] colConditions =  { "in"};
			Object [] colValues = {Long.valueOf(2),Long.valueOf(4)};
			Object [] whereColumnValues =  {colValues};
			String[] selectColumnName = null;

			QueryWhereClauseImpl queryWhereClause = new QueryWhereClauseImpl();
			queryWhereClause.setWhereClause(whereColumnNames, colConditions,
					whereColumnValues, Constants.AND_JOIN_CONDITION);

			dao.openSession(null);
			List<Object> list = dao.retrieve(sourceObjectName, selectColumnName,queryWhereClause);
			dao.closeSession();

			assertNotNull("No object retrieved ::",list);
			assertTrue("No object retrieved ::",list.size() >= 1);

			/*Iterator<Object> itr = list.iterator();
			while(itr.hasNext())
	  		{
	  			User user = (User)itr.next();
	  			System.out.println("--User Email Id  ::"+user.getEmailAddress());

	  		}*/
		}
		catch(Exception exp)
		{
			assertFalse("Failed while retrieving object ::", true);
		}
	}


	/**
	 * This test will assert that objects retrieved successfully
	 * when where clause holds not null condition.
	 */
	@Test
	public void testRetriveIsNotNullCondition()
	{
		try
		{
			String sourceObjectName = "test.User";

			String[] whereColumnNames = {"id","lastName","firstName","emailAddress"};
			String[] whereColumnCondition = new String[]{"is not null",
					"is not null","is not null","is not null"};
			Object [] colValues = {};
			Object[] whereColumnValues =  {colValues};
			String[] selectColumnName = null;
			String joinCondition = Constants.OR_JOIN_CONDITION;

			QueryWhereClauseImpl queryWhereClause = new QueryWhereClauseImpl();
			queryWhereClause.setWhereClause(whereColumnNames, whereColumnCondition,
					whereColumnValues, joinCondition);

			dao.openSession(null);
			List<Object> list = dao.retrieve(sourceObjectName, selectColumnName,queryWhereClause);
			dao.closeSession();

			assertNotNull(list);
			assertTrue("No object retrieved ::",list.size() >= 1);

			/*Iterator<Object> itr = list.iterator();
			while(itr.hasNext())
	  		{
	  			User user = (User)itr.next();
	  			System.out.println("--User Email Id  ::"+user.getEmailAddress());
	  		}*/

		}
		catch(Exception exp)
		{
			assertFalse("Failed while retrieving object ::", true);
		}
	}

	/**
	 * This test will assert that objects retrieved successfully
	 * when where clause holds is null condition.
	 */
	@Test
	public void testRetriveIsNullCondition()
	{
		try
		{
			String sourceObjectName = "test.User";
			String[] whereColumnNames = {"lastName"};
			String[] whereColumnCondition = new String[]{"is null"};
			Object [] colValues = {};
			Object[] whereColumnValues =  {colValues};
			String[] selectColumnName = null;
			String joinCondition = Constants.OR_JOIN_CONDITION;

			QueryWhereClauseImpl queryWhereClause = new QueryWhereClauseImpl();
			queryWhereClause.setWhereClause(whereColumnNames, whereColumnCondition,
					whereColumnValues, joinCondition);
			dao.openSession(null);
			List<Object> list = dao.retrieve(sourceObjectName, selectColumnName,queryWhereClause);
			dao.closeSession();

			assertNotNull(list);
			assertTrue("No object retrieved ::",list.size() >= 1);

			/*Iterator<Object> itr = list.iterator();
			while(itr.hasNext())
	  		{
	  			User user = (User)itr.next();
	  			System.out.println("--User Email Id  ::"+user.getEmailAddress());

	  		}*/

		}
		catch(Exception exp)
		{
			assertFalse("Failed while retrieving object ::", true);
		}

	}

	/**
	 * This test will assert that query will be executed successfully.
	 */
	@Test
	public void testCaseExecuteQuery()
	{
		try
		{
			dao.openSession(null);
			String sql = "select count(*) from test.User";
			List<Object> list = dao.executeQuery(sql, null, false, null);
			dao.closeSession();
			assertNotNull(list);
			assertTrue("No object retrieved ::",list.size() >= 1);

		}
		catch(Exception exp)
		{
			assertFalse("Problem while executing query ::", true);
		}
	}


	/**
	 * This test will assert that requested objects disabled successfully.
	 */
	@Test
	public void testDisableRelatedObjects()
	{
		String tableName = "xyz_user";
		String whereColumnName = "IDENTIFIER";
		Long[] whereColumnValues = {Long.valueOf(6),Long.valueOf(18)};
		try
		{
			dao.openSession(null);
			dao.disableRelatedObjects(tableName,whereColumnName,whereColumnValues);
			dao.commit();
			dao.closeSession();
			assertTrue("Problem in disabling object", true);
		}
		catch(Exception exp)
		{
			assertFalse("Problem in disabling object : :", true);
		}
	}

	/**
	 * This test will assert that requested objects deleted successfully.
	 */
	@Test
	public void testCaseDeleteObject()
	{
	  try
	  {
		  User user = new User();
		  user.setId(Long.valueOf(7));
		  dao.openSession(null);
	  	  dao.delete(user);
	  	  dao.commit();
	  	  dao.closeSession();
	  	assertTrue("Object deleted :", true);
	  }
	  catch(Exception exp)
	  {
		  assertFalse("Failed while deleting object :", true);
	  }

	}


	/**
	 * This method will be called to create user object.
	 * @return : It will returns the user object.
	 */
	User createUserObject()
	{
	  User user = new User();
	  user.setFirstName("john");
	  user.setLastName("Reber");
	  user.setEmailAddress("John_Reber@persistent.co.in");
	  return user;
	}



}
