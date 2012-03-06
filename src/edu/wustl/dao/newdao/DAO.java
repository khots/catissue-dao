/**
 * <p>Title: DAO Interface>
 * <p>Description:	DAO provides methods to manipulate the domain objects.
 * It provides methods like insert ,update etc .</p>
 * @version 1.0
 * @author kalpana_thakur
 */

package edu.wustl.dao.newdao;

import java.io.Serializable;
import java.util.List;

import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;


/**
 * @author kalpana_thakur
 * Handles database operations like insertion, updation, deletion and retrieval of data.
 */
public interface DAO <T,ID extends Serializable>
{
	/**
	 * Insert the Object to the database.
	 * @param obj Object to be inserted in database
	 * @throws DAOException generic DAOException
	 */
	void insert(Object obj) throws DAOException;//,boolean isAuditable)

	/**
	 * updates the object into the database.
	 * @param obj Object to be updated in database
	 * @throws DAOException : generic DAOException
	 */
	void update(Object obj) throws DAOException;


	/**h 
	 * 
	 * @param currentObj
	 * @param previousObj
	 * @throws DAOException
	 */
	void update(Object currentObj,Object previousObj) throws DAOException;

	
	/**
	 * Deletes the persistent object from the database.
	 * @param obj The object to be deleted.
	 * @throws DAOException generic DAOException.
	 */
	void delete(Object obj) throws DAOException;

	
	/**
	 * 
	 * @return
	 * @throws DAOException
	 */
	T findAll() throws DAOException;
	
	/**
	 * Retrieve and returns the source object for given id.
	 * @param sourceObjectName Source object in the Database.
	 * @param identifier identifier of source object.
	 * @return object
	 * @throws DAOException generic DAOException.
	 */
	T findById(ID id) throws DAOException;

	/**
	 * Create a new instance of Query for the given HQL query string.
	 * Execute the Query and returns the list of data.
	 * @param query query
	 * @return List.
	 * @throws DAOException generic DAOException.
	 */
	List<T> executeQuery(String query) throws DAOException;

	/**
	 * Executes the HQL query.
	 * @param query HQL query to execute.
	 * @param columnValueBeans column data beans.
	 * @return list of data.
	 * @throws DAOException Database exception.
	 */
	List<T> executeQuery(String query,List<ColumnValueBean> columnValueBeans) throws DAOException;
	
	
	List executeQuery(String query,Integer startIndex,Integer maxRecords,List<ColumnValueBean> columnValueBeans) throws DAOException;
	


}