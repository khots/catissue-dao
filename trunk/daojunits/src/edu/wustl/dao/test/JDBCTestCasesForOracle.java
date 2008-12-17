package edu.wustl.dao.test;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.util.PagenatedResultData;
import edu.wustl.common.util.QueryParams;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.QueryWhereClause;
import edu.wustl.dao.condition.EqualClause;
import edu.wustl.dao.condition.INClause;
import edu.wustl.dao.condition.NullClause;
import edu.wustl.dao.condition.NotNullClause;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.DatabaseConnectionParams;

/**
 * @author kalpana_thakur
 *
 */
public class JDBCTestCasesForOracle extends BaseTestCase
{

	/**
	 * Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(JDBCTestCases.class);

	/**
	 * DAO instance.
	 */
	private JDBCDAO jdbcDAO;

	{
		setJDBCDAO();
	}

	/**
	 * This method will be called to set the Default DAO.
	 */
	public void setJDBCDAO()
	{
		IDAOFactory daoFactory = daoConfigFactory.getInstance().getDAOFactory("cider");
		try
		{
			jdbcDAO = daoFactory.getJDBCDAO();
		}
		catch (DAOException e)
		{
			logger.fatal(e.getLogMessage());
		}
	}


	/**
	 * This test will assert that DAO instance is not null.
	 */
	@Test
	public void testJDBCDAOInstance()
	{
		assertNotNull("DAO Object is null",jdbcDAO);
	}
	
	/**
	 * This test will assert that table created successfully.
	 */
	@Test
	public void testCreateTableJDBC()
	{
		try
		{
			String tableName = "Temp_Address";
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

	/**
	 * This test will assert that table created successfully.
	 */
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


	/**
	 * This test will assert that table deleted successfully.
	 */
	@Test
	public void testDropTableJDBC()
	{
		try
		{
			jdbcDAO.openSession(null);
			jdbcDAO.delete("XYZ_PHONENUMBER");
			jdbcDAO.delete("TEMP_ADDRESS");
			jdbcDAO.commit();
			jdbcDAO.closeSession();
		}
		catch(ApplicationException exp)
		{
			logger.fatal(exp.getLogMessage());
			assertFalse("Failed while droping table ::", true);
		}
	}

	/**
	 * This test will assert the execution of query.
	 */
	@Test
	public void testExecuteUpdateJDBC()
	{
		try
		{
			jdbcDAO.openSession(null);
			StringBuffer strbuff = new StringBuffer();
			strbuff.append("update test_user set EMAIL_ADDRESS ='abc@per.co.in'" +
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

	/**
	 * This test will assert that all the objects are retrieved successfully.
	 */
	@Test
	public void testCaseRetriveAllObjectsJDBC()
	{
	  try
	  {
		  jdbcDAO.openSession(null);
		  List list = jdbcDAO.retrieve("test_user");
	  	  jdbcDAO.closeSession();
	  	  assertNotNull(list);

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
	public void testCaseRetriveObjectJDBC()
	{
	  try
	  {
		jdbcDAO.openSession(null);
	    List<Object> list = jdbcDAO.retrieve("test_user","IDENTIFIER" , Long.valueOf(1));
	    jdbcDAO.closeSession();
	  	assertNotNull("No objects retrieved",list);
		//assertTrue("No object retrieved ::",!list.isEmpty());
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
	public void testCaseRetrieveObjectColumnsJDBC()
	{
	  try
	  {
		String[] selectColumnName = {"IDENTIFIER","FIRST_NAME","LAST_NAME","EMAIL_ADDRESS"};
		jdbcDAO.openSession(null);
	    List<Object> list = jdbcDAO.retrieve("test_user", selectColumnName);
	    jdbcDAO.closeSession();

	    assertNotNull("No object retrieved ::",list);
	//	assertTrue("No object retrieved ::",!list.isEmpty());
	  }
	  catch(Exception exp)
	  {
		  assertFalse("Failed while retrieving object ::", true);
	  }
	}

	/**
	 * This test will assert that only distinct rows
	 * retrieved successfully.
	 */
	@Test
	public void testCaseRetrieveOnlyDistinctRowsJDBC()
	{
		try
		  {
			String[] selectColumnName = {"IDENTIFIER","FIRST_NAME","LAST_NAME","EMAIL_ADDRESS"};
			jdbcDAO.openSession(null);
		    List<Object> list = jdbcDAO.retrieve("test_user", selectColumnName,true);
		    jdbcDAO.closeSession();

		    assertNotNull("No object retrieved ::",list);
	//		assertTrue("No object retrieved ::",!list.isEmpty());
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
	public void testRetriveInConditionJDBC()
	{
		try
		{
			String sourceObjectName = "test_user";
			Object [] colValues = {Long.valueOf(1),Long.valueOf(2)};
			String[] selectColumnName = null;

			QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
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

	/**
	 * This test will assert that objects retrieved successfully
	 * when where clause holds not null condition.
	 */
	@Test
	public void testRetriveIsNotNullConditionJDBC()
	{
		try
		{
			String sourceObjectName = "test_user";
			String[] selectColumnName = null;

			QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
			queryWhereClause.addCondition(new NotNullClause("IDENTIFIER",sourceObjectName));
			queryWhereClause.orOpr();
			queryWhereClause.addCondition(new NotNullClause("LAST_NAME",sourceObjectName));

			jdbcDAO.openSession(null);
			List<Object> list = jdbcDAO.retrieve(sourceObjectName, selectColumnName,queryWhereClause);
			jdbcDAO.closeSession();

			assertNotNull("No value retrieved :" + list);
	//		assertTrue("No object retrieved ::",list.size() > 0);

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
	public void testRetriveIsNullConditionJDBC()
	{
		try
		{
			String sourceObjectName = "test_user";
			String[] selectColumnName = null;

			QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
			queryWhereClause.addCondition(new NullClause("LAST_NAME",sourceObjectName));

			jdbcDAO.openSession(null);
			List<Object> list = jdbcDAO.retrieve(sourceObjectName, selectColumnName,queryWhereClause);
			jdbcDAO.closeSession();

			assertNotNull("No object retrieved ::",list);
	//		assertTrue("No object retrieved ::",!list.isEmpty());

		}
		catch(Exception exp)
		{
			assertFalse("Failed while retrieving object ::", true);
		}

	}

	/**
	 * This test will assert that objects retrieved successfully with given column value
	 * Having equal (=)condition.
	 */
	@Test
	public void testRetriveEqualConditionJDBC()
	{
		try
		{
			String sourceObjectName = "test_user";
			String[] selectColumnName = null;

			QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
			queryWhereClause.addCondition(new EqualClause("LAST_NAME","NAIK",sourceObjectName));

			jdbcDAO.openSession(null);
			List<Object> list = jdbcDAO.retrieve(sourceObjectName, selectColumnName,queryWhereClause);
			jdbcDAO.closeSession();

			assertNotNull("No object retrieved ::",list);
		//	assertTrue("No object retrieved ::",list.size() > 0);
		}
		catch(Exception exp)
		{
			assertFalse("Failed while retrieving object ::", true);
		}

	}


	/**
	 * This test will assert that date pattern retrieved successfully.
	 */
	@Test
	public void testDatePatternJDBC()
	{
		String datePattern = jdbcDAO.getDatePattern();
		assertNotNull("Problem in geting date pattern.",datePattern);


	}

	/**
	 * This test will assert that time pattern retrieved successfully.
	 */
	@Test
	public void testTimePatternJDBC()
	{
		String timePattern = jdbcDAO.getTimePattern();
		assertNotNull("Problem in geting date pattern.",timePattern);

	}

	/**
	 * This test will assert that date format function retrieved successfully.
	 */
	@Test
	public void testDateFormatFunctionJDBC()
	{
		String dateFormatFunction = jdbcDAO.getDateFormatFunction();
		assertNotNull("Problem in geting date pattern.",dateFormatFunction);


	}

	/**
	 * This test will assert that time format function retrieved successfully.
	 */
	@Test
	public void testTimeFormatFunctionJDBC()
	{
		String timeFormatFunction = jdbcDAO.getTimeFormatFunction();
		assertNotNull("Problem in geting date pattern.",timeFormatFunction);

	}


	/**
	 * This test will assert that Date to string format function retrieved successfully.
	 */
	@Test
	public void testDateTostrFunctionJDBC()
	{
		String dateTostrFunction = jdbcDAO.getDateTostrFunction();
		assertNotNull("Problem in geting date pattern.",dateTostrFunction);

	}


	/**
	 * This test will assert that string to date function retrieved successfully.
	 */
	@Test
	public void testStrTodateFunctionJDBC()
	{
		String strTodateFunction = jdbcDAO.getStrTodateFunction();
		assertNotNull("Problem in geting date pattern.",strTodateFunction);

	}

	/**
	 * This test will assert that string to date function retrieved successfully.
	 */
	@Test
	public void testExecuteQueryJDBC()
	{
		try
		{
			QueryParams queryParams = new QueryParams();
			queryParams.setQuery("select * from test_user");
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


	/**
	 * This test will create a complex retrieve query having multiple clause(IN,NOT NULL,IS NULL)
	 * It will ensure that objects retrieved successfully.
	 */
	@Test
	public void testRetriveComplexQueryJDBC()
	{
		try
		{
			String sourceObjectName = "test_user";
			Object [] colValues = {Long.valueOf(1),Long.valueOf(2)};
			String[] selectColumnName = null;

			QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
			queryWhereClause.addCondition(new INClause("IDENTIFIER",colValues,sourceObjectName));
			queryWhereClause.orOpr();
			queryWhereClause.addCondition(new NotNullClause("FIRST_NAME",sourceObjectName));
			queryWhereClause.orOpr();
			queryWhereClause.addCondition(new EqualClause("FIRST_NAME","naik",sourceObjectName));

			jdbcDAO.openSession(null);
			List<Object> list = jdbcDAO.retrieve(sourceObjectName, selectColumnName,queryWhereClause);
			jdbcDAO.closeSession();
			assertNotNull("No data retrieved :",list);
		//	assertTrue("No data retrieved :",!list.isEmpty());
		}
		catch(Exception exp)
		{
			assertFalse("Problem occurred while retrieving object:", true);
		}
	}

	/**
	 * This test will assert the audit method implementation.
	 */
	@Test
	public void testAuditJDBC()
	{
		try
		{
			jdbcDAO.audit(null, null, null, false);
		}
		catch(ApplicationException exp)
		{
			assertTrue("Problem: exception should be thrown :",exp.getLogMessage().
					contains("There is no implementation for this method"));
			logger.fatal(exp.getLogMessage());
		}

	}


	/**
	 * This test will delete method implementation.
	 */
	@Test
	public void testDeleteJDBC()
	{
		try
		{
			jdbcDAO.delete(new Object());
		}
		catch(ApplicationException exp)
		{
			assertTrue("Problem: exception should be thrown :",exp.getLogMessage().
					contains("There is no implementation for this method"));
			logger.fatal(exp.getLogMessage());
		}

	}


	/**
	 * This test will DisableRelatedObjects method implementation.
	 */
	@Test
	public void testDisableRelatedObjectsJDBC()
	{
		try
		{
			jdbcDAO.disableRelatedObjects(null, null, null);
		}
		catch(ApplicationException exp)
		{
			assertTrue("Problem: exception should be thrown :",exp.getLogMessage().
					contains("There is no implementation for this method"));
			logger.fatal(exp.getLogMessage());
		}

	}

	/**
	 * This test will insert method implementation.
	 */
	@Test
	public void testInsertJDBC()
	{
		try
		{
			jdbcDAO.insert(null, null, false, false);
		}
		catch(ApplicationException exp)
		{
			assertTrue("Problem: exception should be thrown :",exp.getLogMessage().
					contains("There is no implementation for this method"));
			logger.fatal(exp.getLogMessage());
		}

	}

	/**
	 * This test will retrieveAttribute() method implementation.
	 */
	@Test
	public void testRetrieveAttributeJDBC()
	{
		try
		{
			jdbcDAO.retrieveAttribute(null, null, null, null);
		}
		catch(ApplicationException exp)
		{
			assertTrue("Problem: exception should be thrown :",exp.getLogMessage().
					contains("There is no implementation for this method"));
			logger.fatal(exp.getLogMessage());
		}

	}


	/**
	 * This test will Update() method implementation.
	 */
	@Test
	public void testUpdateJDBC()
	{
		try
		{
			jdbcDAO.update(null);
		}
		catch(ApplicationException exp)
		{
			assertTrue("Problem: exception should be thrown :",exp.getLogMessage().
					contains("There is no implementation for this method"));
			logger.fatal(exp.getLogMessage());
		}

	}

	/**
	 * This test will retrieve() method implementation.
	 */
	@Test
	public void testRetrieveJDBC()
	{
		try
		{
			jdbcDAO.retrieve(null, Long.valueOf(0));
		}
		catch(ApplicationException exp)
		{
			assertTrue("Problem: exception should be thrown :",exp.getLogMessage().
					contains("There is no implementation for this method"));
			logger.fatal(exp.getLogMessage());
		}

	}

	/**
	 * This test will be called to insert default values in place of "##" column values.
	 */
	@Test
	public void testInsertHashedValues()
	{
		  try
		  {
			  jdbcDAO.openSession(null);
			  List<Object> columnValues = new ArrayList<Object>();
			  columnValues.add(Long.valueOf(1));
			  columnValues.add((Object)true);
			  columnValues.add("##");
			  columnValues.add((Object)"collected");
			  jdbcDAO.insertHashedValues("test_table_hashed", columnValues,null);
			  jdbcDAO.commit();
			  jdbcDAO.closeSession();
		  }
		  catch(Exception exp)
		  {
			ApplicationException appExp = (ApplicationException)exp;
			logger.fatal(appExp.getLogMessage());
			assertFalse("Problem while inserting ## values", true);
		  }
	}

	/**
	 * This test will be called to insert default values in place of "##" column values.
	 */
	@Test
	public void testInsertHashedValuesGivenColumns()
	{
		  try
		  {
			  jdbcDAO.openSession(null);
			  List<String> columnNames = new ArrayList<String>();
			  columnNames.add("IDENTIFIER");
			  columnNames.add("AVAILABLE");
			  columnNames.add("CREATED_ON_DATE");
			  columnNames.add("COLLECTION_STATUS");

			  List<Object> columnValues = new ArrayList<Object>();
			  columnValues.add(Long.valueOf(2));
			  columnValues.add(false);
			  columnValues.add("##");
			  columnValues.add("collected");
			  jdbcDAO.insertHashedValues("test_table_hashed", columnValues,columnNames);
			  jdbcDAO.commit();
			  jdbcDAO.closeSession();
		  }
		  catch(Exception exp)
		  {
			ApplicationException appExp = (ApplicationException)exp;
			logger.fatal(appExp.getLogMessage());
			assertFalse("Problem while inserting ## values", true);
		  }
	}

	/**
	 * This test will assert the creation of database parameters.
	 */
	@Test
	public void testDatabaseConnectionAndStmtAndResultSet()
	{
		DatabaseConnectionParams databaseConnectionParams = new DatabaseConnectionParams();
		try
		{

			databaseConnectionParams.setConnection(jdbcDAO.getCleanConnection());

			assertNotNull("Connection retrieved is null",databaseConnectionParams.getConnection());

			Statement stmt = databaseConnectionParams.getDatabaseStatement();

			assertNotNull("Statement retrieved is null :",stmt);

			ResultSet rs = databaseConnectionParams.getResultSet
			("select * from test_user");

			assertNotNull("ResultSet retrieved is null :",
					rs);

			assertNotNull("ResultSet doesnot exists or empty :",
					databaseConnectionParams.isResultSetExists("select * from test_user"));
		}
		catch(ApplicationException exp)
		{
			logger.fatal(exp.getLogMessage());
			assertFalse("Problem occurred while retrieveing conn or " +
					"while creating db stmt or rs", true);
		}
		finally
		{
			try
			{
				databaseConnectionParams.closeConnectionParams();
			}
			catch (ApplicationException exp)
			{
				logger.fatal(exp.getLogMessage());
			}
		}

	}






}
