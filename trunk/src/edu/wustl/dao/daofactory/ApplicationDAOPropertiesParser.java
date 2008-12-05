/**
 *
 */

package edu.wustl.dao.daofactory;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.logger.Logger;
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
	private String connectionManager, applicationName, daoFactoryName, defaultDaoName,isDefaultDAOFactory,
			configFile, jdbcDAOName;

	/**
	 * This method gets Dao Factory Map.
	 * @return dao Factory Map.
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
	 * This method parse Xml File.
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
			dom = documentBuilder.parse("ApplicationDAOProperties.xml");

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
			daoFactory.setConnectionManagerName(connectionManager);
			daoFactory.setDefaultDAOClassName(defaultDaoName);
			daoFactory.setJdbcDAOClassName(jdbcDAOName);
			daoFactory.setApplicationName(applicationName);
			daoFactory.setConfigurationFile(configFile);
			daoFactory.setIsDefaultDAOFactory(Boolean.parseBoolean(isDefaultDAOFactory));
			daoFactory.buildSessionFactory();
			daoFactoryMap.put(daoFactory.getApplicationName(), daoFactory);
			resetApplicationProperties();
		}

	}

	/**
	 * This method resets Application Properties.
	 */
	private void resetApplicationProperties()
	{
		daoFactoryName = "";
		connectionManager = "";
		defaultDaoName = "";
		jdbcDAOName = "";
		applicationName = "";
		configFile = "";
		isDefaultDAOFactory = "";
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
			if (applicationChildNode.getNodeName().equals("ConnectionManager"))
			{
				setConnectionManagerProperties(applicationChildNode);
			}
		}
	}

	/**
	 * This method sets Connection Manager Properties.
	 * @param applicationChildNode application Child Node.
	 */
	private void setConnectionManagerProperties(Node applicationChildNode)
	{
		NamedNodeMap attMap = applicationChildNode.getAttributes();
		Node attNode = attMap.item(0);
		connectionManager = attNode.getNodeValue();
	}

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
			if (childrenDAOFactory.getNodeName().equals("jdbcDAO"))
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
		NamedNodeMap attMap = childrenDAOFactory.getAttributes();
		Node attNode = attMap.item(0);
		jdbcDAOName = attNode.getNodeValue();
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
				NamedNodeMap attMap = childnode.getAttributes();
				Node attNode = attMap.item(0);
				defaultDaoName = attNode.getNodeValue();
			}
			if (childnode.getNodeName().equals("Config-file"))
			{
				NamedNodeMap configFileMap = childnode.getAttributes();
				Node configFileMapNode = configFileMap.item(0);
				configFile = configFileMapNode.getNodeValue();
			}
		}
	}

/*	public static void main(String[] args)
	{
		Map<String, IDAOFactory> daoFactoryMap = new HashMap<String, IDAOFactory>();
		ApplicationDAOPropertiesParser parser = new ApplicationDAOPropertiesParser();
		daoFactoryMap = parser.getDaoFactoryMap();
	}*/

}
