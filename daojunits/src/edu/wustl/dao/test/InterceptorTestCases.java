/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/catissue-dao/LICENSE.txt for details.
 */

package edu.wustl.dao.test;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;

import test.Order;
import test.User;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.test.interceptor.UserInterceptProcessor;

public class InterceptorTestCases extends BaseTestCase
{

	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getCommonLogger(HibernateTestCaseForCatissue.class);
	/**
	 * DAO instance.
	 */
	private DAO dao;

	{
		setDAO();
	}

	/**
	 * This method will be called to set the Default DAO.
	 */
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


	/**
	 * This test will assert that Object inserted successfully.
	 */
	@Test
	public void testInterceptorForInsert()
	{
		try
		{
			dao.openSession(null);
			User user = createUser();
			dao.insert(user);
			dao.commit();
			Thread.sleep(70000);
			logger.info("before checking");
			assertNotNull("InterceptProcessor Not executed even after 3 seconds.",System.getProperty(UserInterceptProcessor.IS_PROCESSOR_EXECUTED_INSERT));
			logger.info("after checking");
		}
		catch(Exception exp)
		{
			ApplicationException appExp = (ApplicationException)exp;
			appExp.printStackTrace();
			logger.fatal(appExp.getLogMessage());
			assertFalse("Failed while inserting object :", true);
		}
		finally
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException e)
			{
				e.printStackTrace();
			}
		}


	}



	private User createUser()
	{
		User user = new User();
		user.setEmailAddress("kalpana@kalpana.com");
		user.setFirstName("kalpana");
		user.setActivityStatus("Active");
		user.setLastName("Thakur");

		Collection<Object> userOrderColl = new HashSet<Object>();
		Order userOrder = new Order();
		Order userOrder1 = new Order();
		userOrder.setUser(user);
		userOrder1.setUser(user);
		userOrderColl.add(userOrder);
		userOrderColl.add(userOrder1);
		user.setOrderCollection(userOrderColl);
		return user;
	}


	public void testInterceptorForEdit()
	{try
	{
		//AuditManager.init();
		dao.openSession(null);

		DAO newdao = DAOConfigFactory.getInstance().getDAOFactory("caTissuecore")
		.getDAO();

		newdao.openSession(null);
		User oldUser = (User)
		newdao.retrieveById(User.class.getName(), Long.valueOf(1));
		newdao.closeSession();

		User currentUser = new User();
		currentUser.setId(oldUser.getId());

		Collection collection = new HashSet();
		collection.add(oldUser.getOrderCollection().iterator().next());
		currentUser.setOrderCollection(collection);
		currentUser.setEmailAddress(oldUser.getEmailAddress());
		currentUser.setActivityStatus(oldUser.getActivityStatus());
		currentUser.setLastName(oldUser.getLastName());
		currentUser.setFirstName("Maria johns");


		dao.update(currentUser);
		dao.commit();
		//dao.closeSession();
		Thread.sleep(70000);
		logger.info("before checking edit");
		assertNotNull("InterceptProcessor Not executed even after 3 seconds.",System.getProperty(UserInterceptProcessor.IS_PROCESSOR_EXECUTED_UPDATE));
		logger.info("after checking edit");

	}
	catch(Exception exp)
	{
		ApplicationException appExp = (ApplicationException)exp;
		appExp.printStackTrace();
		logger.fatal(appExp.getLogMessage());
		assertFalse("Failed while inserting object :", true);
	}
	finally
	{
		try
		{
			dao.closeSession();
		}
		catch (DAOException e)
		{
			e.printStackTrace();
		}
	}

	}
}
