package edu.wustl.dao.test;


/**
 * @author kalpana_thakur
 *
 */
public class JDBCTestCases extends BaseTestCase
{/*

	*//**
	 * DAO instance.
	 *//*
	private JDBCDAO jdbcDAO;

	{
		setJDBCDAO();
	}

	*//**
	 * This method will be called to set the Default DAO.
	 *//*
	public void setJDBCDAO()
	{
		IDAOFactory daoFactory = daoConfigFactory.getInstance().getDAOFactory("caTissuecore");
		try
		{
			jdbcDAO = daoFactory.getJDBCDAO();
		}
		catch (DAOException e)
		{
			e.printStackTrace();
		}
	}


	*//**
	 * This test will assert that DAO instance is not null.
	 *//*
	@Test
	public void testJDBCDAOInstance()
	{
		assertNotNull("DAO Object is null",jdbcDAO);
	}

	*//**
	 * This test will assert the execution of query.
	 *//*
	@Test
	public void testExecuteUpdateJDBC()
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
	public void testCaseRetriveAllObjectsJDBC()
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
	public void testCaseRetriveObjectJDBC()
	{
	  try
	  {
		jdbcDAO.openSession(null);
	    List<Object> list = jdbcDAO.retrieve("xyz_user","IDENTIFIER" , Long.valueOf(2));
	    jdbcDAO.closeSession();
	  	assertNotNull("No objects retrieved",list);
		assertTrue("No object retrieved ::",!list.isEmpty());
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
	public void testCaseRetrieveObjectColumnsJDBC()
	{
	  try
	  {
		String[] selectColumnName = {"IDENTIFIER","FIRST_NAME","LAST_NAME","EMAIL_ADDRESS"};
		jdbcDAO.openSession(null);
	    List<Object> list = jdbcDAO.retrieve("xyz_user", selectColumnName);
	    jdbcDAO.closeSession();

	    assertNotNull("No object retrieved ::",list);
		assertTrue("No object retrieved ::",!list.isEmpty());
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
	public void testCaseRetrieveOnlyDistinctRowsJDBC()
	{
		try
		  {
			String[] selectColumnName = {"IDENTIFIER","FIRST_NAME","LAST_NAME","EMAIL_ADDRESS"};
			jdbcDAO.openSession(null);
		    List<Object> list = jdbcDAO.retrieve("xyz_user", selectColumnName,true);
		    jdbcDAO.closeSession();

		    assertNotNull("No object retrieved ::",list);
			assertTrue("No object retrieved ::",!list.isEmpty());
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
	public void testRetriveInConditionJDBC()
	{
		try
		{
			String sourceObjectName = "xyz_user";
			Object [] colValues = {Long.valueOf(2),Long.valueOf(4)};
			String[] selectColumnName = null;

			QueryWhereClause queryWhereClause = new QueryWhereClause();
			queryWhereClause.addCondition(new INClause("IDENTIFIER",colValues,sourceObjectName));

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
	public void testRetriveIsNotNullConditionJDBC()
	{
		try
		{
			String sourceObjectName = "xyz_user";
			String[] selectColumnName = null;

			QueryWhereClause queryWhereClause = new QueryWhereClause();
			queryWhereClause.addCondition(new NotNullClause("IDENTIFIER",sourceObjectName));
			queryWhereClause.operatorOr();
			queryWhereClause.addCondition(new NotNullClause("LAST_NAME",sourceObjectName));

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
	public void testRetriveIsNullConditionJDBC()
	{
		try
		{
			String sourceObjectName = "xyz_user";
			String[] selectColumnName = null;

			QueryWhereClause queryWhereClause = new QueryWhereClause();
			queryWhereClause.addCondition(new IsNullClause("LAST_NAME",sourceObjectName));

			jdbcDAO.openSession(null);
			List<Object> list = jdbcDAO.retrieve(sourceObjectName, selectColumnName,queryWhereClause);
			jdbcDAO.closeSession();

			assertNotNull("No object retrieved ::",list);
			assertTrue("No object retrieved ::",!list.isEmpty());

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
	public void testRetriveEqualConditionJDBC()
	{
		try
		{
			String sourceObjectName = "xyz_user";
			String[] selectColumnName = null;

			QueryWhereClause queryWhereClause = new QueryWhereClause();
			queryWhereClause.addCondition(new EqualClause("LAST_NAME","Washu",sourceObjectName));

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
	 * This test will assert that table created successfully.
	 *//*
	@Test
	public void testCreateTableJDBC()
	{
		try
		{
			String tableName = "XYZ_Address";
			String[] columnNames = {"City","State"};
			jdbcDAO.openSession(null);
			jdbcDAO.createTable(tableName, columnNames);
			jdbcDAO.commit();
			jdbcDAO.closeSession();
		}
		catch(Exception exp)
		{
			assertFalse("Failed while creating table ::", true);
		}

	}

	*//**
	 * This test will assert that table created successfully.
	 *//*
	@Test
	public void testCreateTableQueryJDBC()
	{
		try
		{
			String query = "create table xyz_phoneNumber ( phone_number varchar(20) )";
			jdbcDAO.openSession(null);
			jdbcDAO.createTable(query);
			jdbcDAO.commit();
			jdbcDAO.closeSession();
		}
		catch(Exception exp)
		{
			assertFalse("Failed while creating table ::", true);
		}

	}

	*//**
	 * This test will assert that table deleted successfully.
	 *//*
	@Test
	public void testDropTableJDBC()
	{
		try
		{
			jdbcDAO.openSession(null);
			jdbcDAO.delete("xyz_phoneNumber");
			jdbcDAO.delete("XYZ_Address");
			jdbcDAO.commit();
			jdbcDAO.closeSession();
		}
		catch(Exception exp)
		{
			assertFalse("Failed while droping table ::", true);
		}
	}

	*//**
	 * This test will assert that date pattern retrieved successfully.
	 *//*
	@Test
	public void testDatePatternJDBC()
	{
		String datePattern = jdbcDAO.getDatePattern();
		assertNotNull("Problem in geting date pattern.",datePattern);
		assertTrue("Problem in geting date pattern.",datePattern.contains("%m-%d-%Y"));

	}

	*//**
	 * This test will assert that time pattern retrieved successfully.
	 *//*
	@Test
	public void testTimePatternJDBC()
	{
		String timePattern = jdbcDAO.getTimePattern();
		assertNotNull("Problem in geting date pattern.",timePattern);

	}

	*//**
	 * This test will assert that date format function retrieved successfully.
	 *//*
	@Test
	public void testDateFormatFunctionJDBC()
	{
		String dateFormatFunction = jdbcDAO.getDateFormatFunction();
		assertNotNull("Problem in geting date pattern.",dateFormatFunction);


	}

	*//**
	 * This test will assert that time format function retrieved successfully.
	 *//*
	@Test
	public void testTimeFormatFunctionJDBC()
	{
		String timeFormatFunction = jdbcDAO.getTimeFormatFunction();
		assertNotNull("Problem in geting date pattern.",timeFormatFunction);

	}


	*//**
	 * This test will assert that Date to string format function retrieved successfully.
	 *//*
	@Test
	public void testDateTostrFunctionJDBC()
	{
		String dateTostrFunction = jdbcDAO.getDateTostrFunction();
		assertNotNull("Problem in geting date pattern.",dateTostrFunction);

	}


	*//**
	 * This test will assert that string to date function retrieved successfully.
	 *//*
	@Test
	public void testStrTodateFunctionJDBC()
	{
		String strTodateFunction = jdbcDAO.getStrTodateFunction();
		assertNotNull("Problem in geting date pattern.",strTodateFunction);

	}

	*//**
	 * This test will assert that string to date function retrieved successfully.
	 *//*
	@Test
	public void testExecuteQueryJDBC()
	{
		try
		{
			QueryParams queryParams = new QueryParams();
			queryParams.setQuery("select * from xyz_user");
			queryParams.setSessionDataBean(null);
			queryParams.setSecureToExecute(true);
			queryParams.setHasConditionOnIdentifiedField(false);
			queryParams.setQueryResultObjectDataMap(null);
			queryParams.setStartIndex(-1);
			queryParams.setNoOfRecords(-1);
			jdbcDAO.openSession(null);
			PagenatedResultData pagenatedResultData =
				(PagenatedResultData)jdbcDAO.executeQuery(queryParams);
			jdbcDAO.closeSession();
			assertNotNull("Problem while retrieving data ",pagenatedResultData!=null);
		}
		catch(Exception exp)
		{
			assertFalse("Problem while retrieving data ", true);
		}

	}


	*//**
	 * This test will create a complex retrieve query having multiple clause(IN,NOT NULL,IS NULL)
	 * It will ensure that objects retrieved successfully.
	 *//*
	@Test
	public void testRetriveComplexQueryJDBC()
	{
		try
		{
			String sourceObjectName = "xyz_user";
			Object [] colValues = {Long.valueOf(2),Long.valueOf(4)};
			String[] selectColumnName = null;

			QueryWhereClause queryWhereClause = new QueryWhereClause();
			queryWhereClause.addCondition(new INClause("IDENTIFIER",colValues,sourceObjectName));
			queryWhereClause.operatorOr();
			queryWhereClause.addCondition(new NotNullClause("FIRST_NAME",sourceObjectName));
			queryWhereClause.operatorOr();
			queryWhereClause.addCondition(new EqualClause("FIRST_NAME","Washu",sourceObjectName));

			jdbcDAO.openSession(null);
			List<Object> list = jdbcDAO.retrieve(sourceObjectName, selectColumnName,queryWhereClause);
			jdbcDAO.closeSession();
			assertNotNull("No data retrieved :",list);
			assertTrue("No data retrieved :",!list.isEmpty());
		}
		catch(Exception exp)
		{
			assertFalse("Problem occurred while retrieving object:", true);
		}
	}


*/}
