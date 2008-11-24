package edu.wustl.dao.test;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import edu.wustl.dao.DAO;
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

		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory("caTissuecore");
		dao = daoFactory.getDAO();
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
