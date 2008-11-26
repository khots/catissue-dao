package edu.wustl.dao.test;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import edu.wustl.common.util.dbmanager.DAOException;
import edu.wustl.dao.DAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;

/**
 * @author kalpana_thakur
 *
 */
public class BaseTestCase extends TestCase
{


	/**
	 * DAO instance.
	 */
	protected DAO dao;

	/**
	 * JDBCDAO instance.
	 */
	protected JDBCDAO jdbcDAO;

	/**
	 * @return :It will return the JDBCDAO object
	 */
	protected JDBCDAO getJdbcDAO()
	{
		return jdbcDAO;
	}

	/**
	 * @param jdbcDAO :This method will be called to set the JDBCDAO object
	 */
	protected void setJdbcDAO(JDBCDAO jdbcDAO)
	{
		this.jdbcDAO = jdbcDAO;
	}

	/**
	 * @return :It will return the DAO object
	 */
	protected DAO getDao()
	{
		return dao;
	}

	/**
	 * @param dao :This method will be called to set the DAO object
	 */
	protected void setDao(DAO dao)
	{
		this.dao = dao;
	}


	/**
	 * This will invoke the daoFactory instance
	 * and returns the DAO object as per the application.
	 */
	@Before
	public void setUp()
	{
		try
		{
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory("caTissuecore");

			dao = daoFactory.getDAO();
			jdbcDAO = daoFactory.getJDBCDAO();

		}
		catch (DAOException e)
		{
			assertFalse("Problem in retrieving DAO object", true);
			e.printStackTrace();
		}
	}

	/**
	 * This test will assert that DAO instance is not null.
	 */
	@Test
	public void testDAOInstance()
	{
		assertNotNull("DAO Object is null",dao);
		/*if(dao != null )
		{
			System.out.println("Application Name :: "+dao.getConnectionManager().getApplicationName());
		}*/

	}

}
