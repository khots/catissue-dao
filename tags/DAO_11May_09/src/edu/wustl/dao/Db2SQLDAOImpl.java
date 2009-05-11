package edu.wustl.dao;

import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;

public class Db2SQLDAOImpl  extends AbstractJDBCDAOImpl 
{

	/**
	 * Logger.
	 */
	private static org.apache.log4j.Logger logger = Logger.getLogger(MySQLDAOImpl.class);


	/**
	 * @param tableName :
	 * @throws DAOException :
	 */
	public void deleteTable(String tableName) throws DAOException
	{
		StringBuffer query;

		logger.debug("Drop Table");
		executeUpdate("DROP TABLE " +
				tableName.toString() + " cascade constraints");

	}
}
