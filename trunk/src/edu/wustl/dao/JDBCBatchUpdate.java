package edu.wustl.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.PagenatedResultData;
import edu.wustl.common.util.QueryParams;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.DAOConstants;
/**
 * @author kalpana_thakur
 * This class with handle JDBC batch updates.
 */
public class JDBCBatchUpdate 
{

	/**
	 * It holds the batch size.
	 */
	private int batchCounter = 0;

	/**
	 * Batch size.
	 */
	private int batchSize = 1;

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
			if(statement == null)
			{
				statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_UPDATABLE);
			}

			if(batchCounter < batchSize)
			{
				statement.addBatch(dmlObject);
				batchCounter++;
			}
			else
			{
				ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
				throw new DAOException(errorKey,new Exception(),"DAOFactory.java :"+
						DAOConstants.BATCH_SIZE_ERROR);
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
			batchCounter = 0;
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
	 * @param connection :connection
	 */
	public void setConnection(Connection connection)
	{
		this.connection = connection;
	}



}
