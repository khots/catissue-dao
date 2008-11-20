package test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.wustl.dao.DAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;

public class DAOTest {

	 @Test
	public void checkTest() {
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory("caTissuecore");
		DAO dao = daoFactory.getDAO();
		assertTrue(dao != null);
		
		System.out.println("Success !!!!!");
	}
	
}
