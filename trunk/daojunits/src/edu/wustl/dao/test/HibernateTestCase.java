package edu.wustl.dao.test;



/**
 * @author kalpana_thakur
 *
 */
public class HibernateTestCase extends BaseTestCase
{/*

	*//**
	 * Logger.
	 *//*
	private static org.apache.log4j.Logger logger = Logger.getLogger(HibernateTestCase.class);
	*//**
	 * DAO instance.
	 *//*
	private DAO dao;

	{
		setDAO();
	}

	*//**
	 * This method will be called to set the Default DAO.
	 *//*
	public void setDAO()
	{
		IDAOFactory daoFactory = daoConfigFactory.getInstance().getDAOFactory("caTissuecore");
		try
		{
			dao = daoFactory.getDAO();
		}
		catch (DAOException e)
		{
			logger.fatal("Problem while retrieving DAO object", e);
		}
	}


	*//**
	 * This test will assert that DAO instance is not null.
	 *//*
	@Test
	public void testDAOInstance()
	{
		assertNotNull("DAO Object is null",dao);
	}

	*//**
	 * This test will assert that Object inserted successfully.
	 *//*
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


		  	User user2 = new User();
		  	user2.setFirstName("sachin");
		  	user2.setLastName("Lale");
		  	user2.setEmailAddress("sach@lale.co.in");
		  dao.openSession(null);
		  dao.insert(user2, null, false, false);
		  dao.commit();
		  dao.closeSession();
		}
		catch(Exception exp)
		{
			assertFalse("Failed while inserting object :", true);
		}

	}

		*//**
	 * This test will assert that Object updated successfully.
	 *//*
	@Test
	public void testCaseUpdateObject()
	{
		try
		{

		  User user = new User();
		  user.setIdentifier(Long.valueOf(1));
		  user.setFirstName("Srikanth");
		  user.setLastName("Adiga");
		  user.setEmailAddress("sri.adiga@persistent.co.in");

		  dao.openSession(null);
		  	  dao.update(user);
		  	  dao.commit();
		  dao.closeSession();
		}
		catch(Exception exp)
		{
			assertFalse("Failed while updating object :", true);
		}

	  }

	*//**
	 * This test will assert that Object inserted successfully.
	 *//*
	@Test
	public void testInsertPerson()
	{
		try
		{
			dao.openSession(null);
			Person person = new Person();

			Address address = new Address();
			address.setStreet("Street unknown");
			dao.insert(address, null, false, false);
			person.setAddress(address);

			Collection<Object> orderCol = new HashSet<Object>();
			Order order = new Order();
			order.setPerson(person);

			person.setName("Kalpana");
			orderCol.add(order);
			person.setOrderCollection(orderCol);
			dao.insert(person, null, false, false);
			dao.commit();
			dao.closeSession();

		}
		catch(Exception exp)
		{
			ApplicationException appExp = (ApplicationException)exp;
			logger.fatal(appExp.getLogMessage());
			assertFalse("Failed while inserting object :", true);
		}

	}

	*//**
	 * This test will assert on constraint violation proper message is thrown.
	 *//*
	@Test
	public void testConstraintViolation()
	{
		try
		{
			dao.openSession(null);
			Address address = new Address();
			address.setStreet("Street unknown");
			dao.insert(address, null, false, false);
			dao.commit();
			dao.closeSession();
		}
		catch(Exception exp)
		{
			ConstraintViolationFormatter formatter = new ConstraintViolationFormatter();
			try
			{
				ApplicationException appExp = (ApplicationException)exp;
				String messagethrown = formatter.formatMessage(appExp.getWrapException(),
						"caTissuecore");
				assertTrue("Message thrown is not correct!! ",
						messagethrown.contains("Submission failed since"));

			}
			catch (DAOException e)
			{
				logger.fatal(e);
			}

		}
	  }



	*//**
	 * This test will assert  the retrieve used by catissuecore.
	 *//*
	@Test
	 public void testRetrieveForCatissue()
	{
		List<Object> list = null;
		try
		{
			dao.openSession(null);
			String[] selectColumnName = null;
			String sourceObjectName = "test.User";
			Object [] colValues = {Long.valueOf(1),Long.valueOf(2)};
			String[] whereColNames = {"lastName" , "identifier"};
			String[] whereColConditions = {DAOConstants.EQUAL,
					DAOConstants.IN_CONDITION};
			Object[] whereColValues = {"Naik",colValues};
			String joinCondition = DAOConstants.OR_JOIN_CONDITION;
			QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
			queryWhereClause.getWhereCondition(whereColNames, whereColConditions,
					whereColValues, joinCondition);
			list = dao.retrieve(sourceObjectName, selectColumnName,queryWhereClause);
			dao.closeSession();
			assertNotNull("Object retrieved is null ",list);
		}
		catch(Exception exp)
		{
			assertFalse("Problem while retrieving :", true);
		}
	}

	*//**
	 * This test will assert that all the objects are retrieved successfully.
	 *//*
	@Test
	public void testCaseRetriveAllObjects()
	{
	  try
	  {
		  dao.openSession(null);
	  	  List<Object> list = dao.retrieve("test.User");
	  	  dao.closeSession();

	  	  assertNotNull("No objects retrieved :",list);
	  	  assertTrue("No object retrieved ::", !list.isEmpty());
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
	public void testCaseRetriveObject()
	{
	  try
	  {
	    dao.openSession(null);
	    List<Object> list = dao.retrieve("test.User","identifier" , Long.valueOf(1));
	  	dao.closeSession();
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
	public void testCaseRetrieveObjectColumns()
	{
	  try
	  {
		String[] selectColumnName = {"identifier","firstName","lastName","emailAddress"};
		dao.openSession(null);
	    List<Object> list = dao.retrieve("test.User", selectColumnName);
	    dao.closeSession();

	    assertNotNull("No object retrieved ::",list);
		assertTrue("No object retrieved ::",!list.isEmpty());
	  }
	  catch(Exception exp)
	  {
		  assertFalse("Failed while retrieving object ::", true);
	  }
	}

	*//**
	 * This test will assert that object will given identifier
	 * retrieved successfully.
	 *//*
	@Test
	public void testCaseRetrieveObjectAsPerID()
	{
		try
		{
			dao.openSession(null);
			User user  = (User)dao.retrieve("test.User", Long.valueOf(1));
			dao.closeSession();
			assertNotNull("Object is null ",user);
		}
		catch(Exception exp)
		{
			assertFalse("Failed while retrieving object ::", true);
		}

	}

	*//**
	 * This test will assert that object will given identifier
	 * retrieved successfully.
	 *//*
	@Test
	public void testCaseLoadCleanObject()
	{
		try
		{
			dao.openSession(null);
			HibernateDAOImpl hiberDao = (HibernateDAOImpl)dao;
			User user  = (User)hiberDao.loadCleanObj("test.User", Long.valueOf(1));
			dao.closeSession();

			assertNotNull("No object retieved ::",user);

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
			String sourceObjectName = "test.User";
			String[] selectColumnName = null;

			Object[] object = {"naik"};
			QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
			queryWhereClause.addCondition(new INClause("firstName","JOHN,abhijit")).andOpr().
			addCondition(new INClause("lastName",object,sourceObjectName)).orOpr().
			addCondition(new NotEqualClause("identifier",Long.valueOf(1)));

			dao.openSession(null);
			List<Object> list = dao.retrieve(sourceObjectName, selectColumnName,queryWhereClause);
			dao.closeSession();

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
	 * when where clause holds not null condition.
	 *//*
	@Test
	public void testRetriveIsNotNullCondition()
	{
		try
		{
			String sourceObjectName = "test.User";
			String[] selectColumnName = null;

			QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
			queryWhereClause.addCondition(new NotNullClause("identifier"))
			.orOpr().addCondition(new NotNullClause("lastName",sourceObjectName));


			dao.openSession(null);
			List<Object> list = dao.retrieve(sourceObjectName, selectColumnName,queryWhereClause);
			dao.closeSession();

			assertNotNull(list);
			assertTrue("No object retrieved ::",!list.isEmpty());

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
			String sourceObjectName = "test.User";

			String[] selectColumnName = null;

			QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
			queryWhereClause.addCondition(new NullClause("lastName"));

			dao.openSession(null);
			List<Object> list = dao.retrieve(sourceObjectName, selectColumnName,queryWhereClause);
			dao.closeSession();

			assertNotNull(list);
			assertTrue("No object retrieved ::",list.size() > 0);

		}
		catch(Exception exp)
		{
			assertFalse("Failed while retrieving object ::", true);
		}

	}

	*//**
	 * This test will assert that query will be executed successfully.
	 *//*
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
			assertTrue("No object retrieved ::",!list.isEmpty());

		}
		catch(Exception exp)
		{
			assertFalse("Problem while executing query ::", true);
		}
	}


	*//**
	 * This test will assert that requested objects disabled successfully.
	 *//*
	@Test
	public void testDisableRelatedObjects()
	{
		String tableName = "test_user";
		String whereColumnName = "IDENTIFIER";
		Long[] whereColumnValues = {Long.valueOf(1),Long.valueOf(2)};
		try
		{
			dao.openSession(null);
			dao.disableRelatedObjects(tableName,whereColumnName,whereColumnValues);
			dao.commit();
			dao.closeSession();
		}
		catch(Exception exp)
		{
			assertFalse("Problem in disabling object : :", true);
		}
	}


	*//**
	 * This test will assert that attribute retrieved successfully.
	 *//*
	@Test
	public void testRetrieveAttribute()
	{
		try
		{
			dao.openSession(null);
			Object obj = (Object)dao.retrieveAttribute(User.class,
					Long.valueOf(1),"emailAddress","identifier");
			dao.closeSession();

			assertNotNull("Object retrieved is null",obj);
			List<String> list = (List<String>)obj;
			assertTrue("Problem in retrieving attribute :", !list.isEmpty());
		}
		catch(Exception exp)
		{
			assertFalse("Problem in retrieving attribute ::", true);
		}
	}

	*//**
	 * This method will use session.load to retrieve the object with the given identifier.
	 *//*
	@Test
	public void testLoadCleanObjects()
	{
		try
		{
			HibernateDAO hibernateDAO = (HibernateDAO)dao;
			hibernateDAO.openSession(null);

			Object obj = (Object)hibernateDAO.loadCleanObj(User.class,
					Long.valueOf(1));
			dao.closeSession();
			assertNotNull("Object retrieved is null",obj);
		}
		catch(Exception exp)
		{
			assertFalse("Problem in retrieving attribute ::", true);
		}
	}

	*//**
	 * This test will assert that requested objects deleted successfully.
	 *//*
	@Test
	public void testCaseDeleteObject()
	{
	  try
	  {
		  User user = new User();
		  user.setIdentifier(Long.valueOf(2));
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

	*//**
	 * This test will assert that clean session retrieved.
	 *//*
	@Test
	public void testCleanSession()
	{
		try
		{
			Session session = dao.getConnectionManager().getCleanSession();
			assertNotNull("session Object is null",session);
			session.close();
		}
		catch(Exception exp)
		{
			assertFalse("Failed while obtaining new session :", true);
		}

	}

	*//**
	 * This test will open clean session with in a session.
	 * insert one object and in second session update the same object.
	 *//*
	@Test
	public void testCleanSessionWithinaSession()
	{
		try
		{
			dao.openSession(null);

			Session session = dao.getConnectionManager().getCleanSession();
				User user = (User)createUserObject();
				Transaction transaction = session.beginTransaction();
				session.save(user);
				transaction.commit();
			session.close();

			user.setEmailAddress("cruise_tom@persistent.co.in");
			dao.update(user);
			dao.commit();
			dao.closeSession();
		}
		catch(Exception exp)
		{
			assertFalse("Problem occurred while opening clean session within a session:", true);
		}
	}


	*//**
	 * This test will ensure that second application can open session
	 * when first application session is not closed that means
	 * opening and closing of session by one application do not affect other application session.
	 * In this test application 'App1' and 'App2'insert object to their respective databases.
	 * Pointing to same database type either MySQL , Oracle or any third type.
	 *//*
	@Test
	public void testMultAppInsertPointngRespectiveDBSameDBType()
	{
		try
		{
			User user = (User)createUserObject();
			dao.openSession(null);
			insertObjectByApp2(user);
			user.setEmailAddress("cruise_tom@persistent.co.in");
			dao.insert(user, null, false, false);
			dao.commit();
			dao.closeSession();
		}
		catch(Exception exp)
		{
			assertFalse("Problem occurred while opening second session within a session:", true);
		}
	}


	*//**
	 * This test will create a complex retrieve query having multiple clause(IN,NOT NULL,IS NULL)
	 * It will ensure that objects retrieved successfully.
	 *//*
	@Test
	public void testRetriveComplexQuery()
	{
		try
		{
			String sourceObjectName = "test.User";
			Object [] colValues = {Long.valueOf(1),Long.valueOf(2)};
			String[] selectColumnName = null;

			QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
			queryWhereClause.addCondition(new INClause("identifier",colValues))
			.orOpr().addCondition(new NotNullClause("firstName")).orOpr()
			.addCondition(new EqualClause("firstName","Washu"));

			dao.openSession(null);
			List<Object> list = dao.retrieve(sourceObjectName, selectColumnName,queryWhereClause);
			dao.closeSession();
			assertNotNull("No data retrieved :",list);
			assertTrue("No data retrieved :",!list.isEmpty());
		}
		catch(Exception exp)
		{
			assertFalse("Problem occurred while retrieving object:", true);
		}
	}

	*//**
	 * This test will various factory members.
	 * 	 *//*
	@Test
	public void testDAOFactoryMembers()
	{
		IDAOFactory daoFactory = daoConfigFactory.getInstance().getDAOFactory("caTissuecore");
		assertNotNull("No configuration file exist", daoFactory.getConfigurationFile());
		assertNotNull("Default class name Name does not exists", daoFactory.getDefaultDAOClassName());
		assertNotNull("Connection Manager Name not exists", daoFactory.getConnectionManagerName());
		assertNotNull("Configuration object null ", dao.getConnectionManager().getConfiguration());
		assertNotNull("JDBC class name Name does not exists", daoFactory.getJdbcDAOClassName());

	}

	*//**
	 * This test will test various methods of HibernateMetaData class.
	 *//*
	@Test
	public void testDifferentMethodsOfHibernateMetaData()
	{
		try
		{
			dao.openSession(null);
			HibernateMetaData.initHibernateMetaData(dao.getConnectionManager().getConfiguration());
			User user  = (User)dao.retrieve("test.User", Long.valueOf(1));
			Object object = HibernateMetaData.getProxyObjectImpl(user);
				assertNotNull("Proxy Object retrieved is null :"+object);

			String tableName = HibernateMetaData.getTableName(user.getClass());
				assertTrue("Table name is empty",
						!tableName.equals(DAOConstants.TAILING_SPACES));

			String rootTableName = HibernateMetaData.getRootTableName(user.getClass());
			assertTrue("Root Table name is empty",
						!rootTableName.equals(DAOConstants.TAILING_SPACES));

			String columnName = HibernateMetaData.getColumnName(user.getClass(),"lastName");
				assertTrue("Column Name is empty",
						!columnName.equals(DAOConstants.TAILING_SPACES));

			int colWidth = HibernateMetaData.getColumnWidth(user.getClass(),"lastName");
			assertTrue("colWidth  is 0",colWidth > 0);

			String className = HibernateMetaData.getClassName("Test_user");
				assertTrue("NO class name obtained ",
						!className.equals(DAOConstants.TAILING_SPACES));


			dao.closeSession();
			assertNotNull("Object is null ",user);
		}
		catch(Exception exp)
		{
			assertFalse("Failed in HibernateMetaData ::", true);
		}

	}

	*//**
	 * @param user : user object
	 * @throws DAOException : DAOExp
	 *//*
	private void insertObjectByApp2(User user) throws DAOException

	{
		IDAOFactory daoFactory = daoConfigFactory.getInstance().getDAOFactory("DynamicExtensions");
		DAO deDAO = daoFactory.getDAO();
		deDAO.openSession(null);
		deDAO.insert(user, null, false, false);
		deDAO.commit();
		deDAO.closeSession();
	}


	*//**
	 * This method will be called to create user object.
	 * @return : It will returns the user object.
	 *//*
	User createUserObject()
	{
	  User user = new User();
	  user.setFirstName("abhijit");
	  user.setLastName("naik");
	  user.setEmailAddress("abhijit_naik@persistent.co.in");
	  return user;
	}




*/}
