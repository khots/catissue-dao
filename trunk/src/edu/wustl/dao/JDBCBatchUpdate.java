package edu.wustl.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.DAOConstants;
/**
 * @author kalpana_thakur
 * This class will handle JDBC batch updates.
 */
public class JDBCBatchUpdate
{

	/**
	 * Default size of batch.
	 */
	private final int DEFAULT_BATCH_SIZE = 1;
	/**
	 * It holds the batch size.
	 */
	private int batchCounter = 0;

	/**
	 * Batch size.
	 */
	private int batchSize = DEFAULT_BATCH_SIZE;

	/**
	 * Connection.
	 */
	private Connection connection;

	/**
	 * Connection statement.
	 */
	private Statement statement;


	/**
	 * This method will be called to set the size of the batch.
	 * @param batchSize batchSize
	 * @throws DAOException :DAOException
	 */
	public void setBatchSize(int batchSize) throws DAOException
	{
		clearBatch();
		this.batchSize = batchSize;
	}

	/**
	 * This method will be called to set the DML object to batch.
	 * @param dmlObject :DML object
	 * @throws DAOException : Generic database exception.
	 */
	public void addDMLToBatch(String dmlObject) throws DAOException
	{
		try
		{
			if(batchCounter < batchSize)
			{
				statement.addBatch(dmlObject);
				batchCounter++;
			}
			else
			{
				ErrorKey errorKey = ErrorKey.getErrorKey("dao.batch.size.error");
				throw new DAOException(errorKey,new Exception(),"DAOFactory.java :");
			}
		}
		catch (SQLException exp)
		{
			clearBatch();
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"DatabaseConnectionParams.java :"+
				DAOConstants.BATCH_UPDATE_ERROR);
		}

	}

	/**
	 * This method will be called for batch update insert.
	 * @throws DAOException :Generic DAOException.
	 */
	public void batchUpdate() throws DAOException
	{
		try
		{
			statement.executeBatch();
		}
		catch (SQLException exp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"DatabaseConnectionParams.java :"+
					DAOConstants.BATCH_UPDATE_ERROR);
		}
	}

	/**
	 * @throws DAOException :Generic DAOException.
	 */
	public void clearBatch() throws DAOException
	{

		try
		{
			batchCounter = DEFAULT_BATCH_SIZE;
			if(statement != null)
			{
				statement.clearBatch();
			}
		}
		catch (SQLException exp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"DatabaseConnectionParams.java :"+
				DAOConstants.BATCH_UPDATE_ERROR);
		}
	}

	/**
	 * This method will be called to set the database connection.
	 * @param connection :connection
	 * @throws DAOException : Database exception.
	 */
	public void setConnection(Connection connection) throws DAOException
	{
		this.connection = connection;
		initializeBatchstmt();

	}

	/**
	 * This method will be called to set the batch statement.
	 * @throws DAOException : Database exception.
	 */
	private void initializeBatchstmt() throws DAOException
	{
		try
		{
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			ResultSet.CONCUR_UPDATABLE);

		}
		catch (SQLException exp)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey,exp,"DatabaseConnectionParams.java :"+
				DAOConstants.STMT_CREATION_ERROR);
		}
	}


	/**
	 * This method will be called to close the connection.
	 */
	public void cleanConnection()
	{
		connection = null;
		statement = null;
	}


}
