package edu.wustl.dao.test;
import junit.framework.TestCase;

import org.junit.Test;

import edu.wustl.dao.DAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;

public class HibernateInsertTestCase extends TestCase{

	@Test
	public void testAddInstitution()
	{
			IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory("caTissuecore");
			assertTrue(daoFactory.getDAO() instanceof DAO);
			assertTrue("Retrieved the DAO instance : ", true);
			System.out.println("---------Hello abc ---------------------------");
		 
	}
	
   @Test
   public void testoneIsOne() {
	     assertTrue(1 == 1);   
   }

   @Test
   public void testTwoIsTwo() {
	     assertTrue(2 == 2);   
   }

	
}
