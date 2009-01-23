/**
 * This class is used to parse the ApplicationDAOProperties.xml
 * It populates Map daoFactoryMap.
 */

package edu.wustl.dao.daofactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DatabaseProperties;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.DAOConstants;

/**
 * @author prashant_bandal
 *
 */
public class ApplicationDAOPropertiesParser
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static org.apache.log4j.Logger logger = Logger
			.getLogger(ApplicationDAOPropertiesParser.class);

	/**
	 * Specifies Document object.
	 */
	private Document dom;

	/**
	 * Specifies application variables.
	 */
	private String defaultConnManager,jdbcConnManager,applicationName, daoFactoryName,
	defaultDaoName,isDefaultDAOFactory,	configFile, jdbcDAOName;

	/**
	 * Database specific properties.
	 */
	private String databaseType,dataSource,defaultBatchSize,datePattern,timePattern,dateFormatFunction,timeFormatFunction,
	dateTostrFunction,strTodateFunction,exceptionFormatterName,queryExecutorName;

	/**
	 * This method gets DAO Factory Map.
	 * @return DAO Factory Map.
	 */
	public Map<String, IDAOFactory> getDaoFactoryMap()
	{
		Map<String, IDAOFactory> daoFactoryMap = new HashMap<String, IDAOFactory>();
		try
		{
			readFile();
			parseDocument(daoFactoryMap);
		}
		catch (Exception exception)
		{
			logger.error(exception.getMessage(), exception);
		}
		return daoFactoryMap;
	}

	/**
	 * This method parse XML File.
	 * @throws DAOException :
	 */
	private void readFile() throws DAOException
	{
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try
		{
			//Using factory get an instance of document builder
			DocumentBuilder documentBuilder = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			InputStream inputStream = ApplicationDAOPropertiesParser.class.getClassLoader()
			.getResourceAsStream("ApplicationDAOProperties.xml");
			dom = documentBuilder.parse(inputStream);

		}
		catch (Exception exp)
		{
			logger.error(exp.getMessage(), exp);
			ErrorKey errorKey = ErrorKey.getErrorKey("app.prop.parsing.exp");
			throw new DAOException(errorKey,exp,"ApplicationDAOPropertiesParser.java :"+
					DAOConstants.FILE_PARSE_ERROR);
		}

	}

	/**
	 * This method parse the document.
	 * @param daoFactoryMap this will hold the factory object as per the application.
	 * @throws InstantiationException Instantiation Exception
	 * @throws IllegalAccessException Illegal Access Exception
	 * @throws ClassNotFoundException Class Not Found Exception
	 * @throws DAOException :generic DAOException.
	 */
	private void parseDocument(Map<String, IDAOFactory> daoFactoryMap)
	throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, DAOException
	{
		//get the root element
		Element root = dom.getDocumentElement();
		NodeList rootChildren = root.getElementsByTagName("Application");
		for (int i = 0; i < rootChildren.getLength(); i++)
		{
			Node applicationChild = rootChildren.item(i);

			if (applicationChild.getNodeName().equals("Application"))
			{
				setApplicationProperties(applicationChild);
			}

			IDAOFactory daoFactory = (IDAOFactory) Class.forName(daoFactoryName).newInstance();
			DatabaseProperties databaseProperties = new DatabaseProperties();

			daoFactory.setApplicationName(applicationName);
			daoFactory.setIsDefaultDAOFactory(Boolean.parseBoolean(isDefaultDAOFactory));

			daoFactory.setDefaultDAOClassName(defaultDaoName);
			daoFactory.setConfigurationFile(configFile);
			daoFactory.setDefaultConnMangrName(defaultConnManager);

			daoFactory.setJdbcDAOClassName(jdbcDAOName);
			daoFactory.setJdbcConnMangrName(jdbcConnManager);

			daoFactory.buildSessionFactory();

			setDatabaseProperties(databaseProperties);
			daoFactory.setDatabaseProperties(databaseProperties);

			daoFactoryMap.put(daoFactory.getApplicationName(), daoFactory);
			resetApplicationProperties();
			databaseProperties = null;

		}

	}


	/**
	 * This method will be called to set all database specific properties like database name
	 * database source.
	 * @param databaseProperties : database properties
	 */
	private void setDatabaseProperties(DatabaseProperties databaseProperties)
	{
		databaseProperties.setDataBaseName(databaseType);
		databaseProperties.setDataSource(dataSource);
		databaseProperties.setDateFormatFunction(dateFormatFunction);
		databaseProperties.setDatePattern(datePattern);
		databaseProperties.setDateTostrFunction(dateTostrFunction);
		databaseProperties.setStrTodateFunction(strTodateFunction);
		databaseProperties.setTimeFormatFunction(timeFormatFunction);
		databaseProperties.setTimePattern(timePattern);
		databaseProperties.setExceptionFormatterName(exceptionFormatterName);
		databaseProperties.setQueryExecutorName(queryExecutorName);
		databaseProperties.setDefaultBatchSize(Integer.valueOf(defaultBatchSize));
	}

	/**
	 * This method resets Application Properties.
	 */
	private void resetApplicationProperties()
	{
		daoFactoryName = "";
		applicationName = "";
		isDefaultDAOFactory = "";

		defaultDaoName = "";
		configFile = "";
		defaultConnManager = "";

		jdbcDAOName = "";
		dataSource = "";
		jdbcConnManager = "";
		databaseType ="";
		datePattern = "";
		timePattern= "";
		dateFormatFunction= "";
		timeFormatFunction= "";
		dateTostrFunction= "";
		strTodateFunction= "";
		exceptionFormatterName="";
		queryExecutorName="";
		defaultBatchSize="";
	}

	/**
	 * This method sets Application Properties.
	 * @param applicationChild application Children.
	 */
	private void setApplicationProperties(Node applicationChild)
	{
		NamedNodeMap attributeMap = applicationChild.getAttributes();
		applicationName = ((Node) attributeMap.item(0)).getNodeValue();

		NodeList applicationChildList = applicationChild.getChildNodes();

		for (int k = 0; k < applicationChildList.getLength(); k++)
		{
			Node applicationChildNode = applicationChildList.item(k);

			if (applicationChildNode.getNodeName().equals("DAOFactory"))
			{
				setDAOFactoryProperties(applicationChildNode);
			}
			/*if (applicationChildNode.getNodeName().equals("ConnectionManager"))
			{
				setConnectionManagerProperties(applicationChildNode);
			}*/
		}
	}

	/**
	 * This method sets Connection Manager Properties.
	 * @param applicationChildNode application Child Node.
	 */
	/*private void setConnectionManagerProperties(Node applicationChildNode)
	{
		NamedNodeMap attMap = applicationChildNode.getAttributes();
		Node attNode = attMap.item(0);
		defaultConnManager = attNode.getNodeValue();
	}
*/
	/**
	 * This method sets DAOFactory Properties.
	 * @param childNode DAOFactory child Node.
	 */
	private void setDAOFactoryProperties(Node childNode)
	{
		setAttributesOfDAOFactory(childNode);
		NodeList childlist = childNode.getChildNodes();
		for (int m = 0; m < childlist.getLength(); m++)
		{
			Node childrenDAOFactory = childlist.item(m);
			if (childrenDAOFactory.getNodeName().equals("DefaultDAO"))
			{
				setDefaultDAOProperties(childrenDAOFactory);
			}
			if (childrenDAOFactory.getNodeName().equals("JDBCDAO"))
			{
				setJDBCDAOProperties(childrenDAOFactory);
			}
			
		}

	}

	/**
	 * This method sets the DAO factor Attributes as name and
	 * default settings.
	 * @param childNode :
	 */
	private void setAttributesOfDAOFactory(Node childNode)
	{
		NamedNodeMap attrMap = childNode.getAttributes();
		for(int i =0 ; i < attrMap.getLength();i++)
		{
			Node daoFactoryPropertyNode = attrMap.item(i);
			if(daoFactoryPropertyNode.getNodeName().equals("default"))
			{
				isDefaultDAOFactory = daoFactoryPropertyNode.getNodeValue();
			}
			if(daoFactoryPropertyNode.getNodeName().equals("name"))
			{
				daoFactoryName = daoFactoryPropertyNode.getNodeValue();
			}
		}
	}

	/**
	 * This method sets JDBCDAO Properties.
	 * @param childrenDAOFactory children DAOFactory.
	 */
	private void setJDBCDAOProperties(Node childrenDAOFactory)
	{
		NodeList childJDBCDAO = childrenDAOFactory.getChildNodes();
		for (int l = 0; l < childJDBCDAO.getLength(); l++)
		{
			Node childnode = childJDBCDAO.item(l);
			if (childnode.getNodeName().equals("Class-name"))
			{
				Node attNode = getNextnode(childnode);
				jdbcDAOName = attNode.getNodeValue();
			}
			if (childnode.getNodeName().equals("DataSource"))
			{
				Node configFileMapNode = getNextnode(childnode);
				dataSource = configFileMapNode.getNodeValue();
			}
			if (childnode.getNodeName().equals("JDBCConnectionManager"))
			{
				Node attNode = getNextnode(childnode);
				jdbcConnManager = attNode.getNodeValue();
			}
			if (childnode.getNodeName().equals("DatabaseType"))
			{
				Node attNode = getNextnode(childnode);
				databaseType = attNode.getNodeValue();
			}
			if (childnode.getNodeName().equals("DatePattern"))
			{
				Node attNode = getNextnode(childnode);
				datePattern = attNode.getNodeValue();
			}
			if (childnode.getNodeName().equals("TimePattern"))
			{
				Node attNode = getNextnode(childnode);
				timePattern = attNode.getNodeValue();
			}
			if (childnode.getNodeName().equals("DateFormatFunction"))
			{
				Node attNode = getNextnode(childnode);
				dateFormatFunction = attNode.getNodeValue();
			}
			if (childnode.getNodeName().equals("TimeFormatFunction"))
			{
				Node attNode = getNextnode(childnode);
				timeFormatFunction = attNode.getNodeValue();
			}
			if (childnode.getNodeName().equals("DateToStrFunction"))
			{
				Node attNode = getNextnode(childnode);
				dateTostrFunction = attNode.getNodeValue();
			}
			if (childnode.getNodeName().equals("StrTodateFunction"))
			{
				Node attNode = getNextnode(childnode);
				strTodateFunction = attNode.getNodeValue();
			}
			if (childnode.getNodeName().equals("ExceptionFormater"))
			{
				Node attNode = getNextnode(childnode);
				exceptionFormatterName = attNode.getNodeValue();
			}
			if (childnode.getNodeName().equals("QueryExecutor"))
			{
				Node attNode = getNextnode(childnode);
				queryExecutorName = attNode.getNodeValue();
			}
			if(childnode.getNodeName().equals("DefaultBatchSize"))
			{
				Node attNode = getNextnode(childnode);
				defaultBatchSize = attNode.getNodeValue();
			}
		}

	}

	/**
	 * This will return the next node details.
	 * @param childnode :next node
	 * @return : next node
	 */
	private Node getNextnode(Node childnode)
	{
		NamedNodeMap defaultConnMangrMap = childnode.getAttributes();
		Node defaultConnMangrNode = defaultConnMangrMap.item(0);
		return defaultConnMangrNode;
	}

	/**
	 * This method sets Default DAO Properties.
	 * @param childrenDAOFactory children DAOFactory
	 * @throws DOMException
	 */
	private void setDefaultDAOProperties(Node childrenDAOFactory)
	{
		NodeList childDefaultDAO = childrenDAOFactory.getChildNodes();
		for (int l = 0; l < childDefaultDAO.getLength(); l++)
		{
			Node childnode = childDefaultDAO.item(l);

			if (childnode.getNodeName().equals("Class-name"))
			{
				Node attNode = getNextnode(childnode);
				defaultDaoName = attNode.getNodeValue();
			}
			if (childnode.getNodeName().equals("Config-file"))
			{
				Node configFileMapNode = getNextnode(childnode);
				configFile = configFileMapNode.getNodeValue();
			}
			if (childnode.getNodeName().equals("DefaultConnectionManager"))
			{
				Node defaultConnMangrNode = getNextnode(childnode);
				defaultConnManager = defaultConnMangrNode.getNodeValue();
			}
		}
	}

/*public static void main(String[] args)
	{
		Map<String, IDAOFactory> daoFactoryMap = new HashMap<String, IDAOFactory>();
		ApplicationDAOPropertiesParser parser = new ApplicationDAOPropertiesParser();
		daoFactoryMap = parser.getDaoFactoryMap();
	}
*/
}
