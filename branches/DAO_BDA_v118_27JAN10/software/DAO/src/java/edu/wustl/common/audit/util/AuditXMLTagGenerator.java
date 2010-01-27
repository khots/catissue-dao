
package edu.wustl.common.audit.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * This class generates the tag used metadata audit xml
 * @author kunal_kamble
 *
 */
public class AuditXMLTagGenerator
{
	/**
	 * @param className
	 * @return
	 */
	public String getAuditableMetatdataXMLString(String className)
	{
		StringBuffer auditableMetatdataXML = new StringBuffer();
		auditableMetatdataXML.append(getAuditableClassTag(className));
		auditableMetatdataXML.append('\n');
		try
		{
			Class newObjectClass = Class.forName(className);
			Map<String, Field> fieldList = new HashMap<String, Field>();
			populateAllFields(newObjectClass, fieldList);
			for (Field field : fieldList.values())
			{
				if (!"serialVersionUID".equalsIgnoreCase(field.getName()))
				{
					auditableMetatdataXML.append('\t');
					auditableMetatdataXML.append(getAttributeTag(field));
					auditableMetatdataXML.append('\n');
				}

			}
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		finally
		{
			auditableMetatdataXML.append("</AuditableClass>");
		}
		return auditableMetatdataXML.toString();
	}

	/**
	 * @param klass
	 * @param fieldList field list declared in the class and in its parent classes
	 */
	private void populateAllFields(Class klass, Map<String, Field> fieldList)
	{
		if (klass.getSuperclass() != null)
		{
			populateAllFields(klass.getSuperclass(), fieldList);
		}
		klass.getDeclaredFields();
		for (Field field : klass.getDeclaredFields())
		{
			fieldList.put(field.getName(), field);
		}
	}

	/**
	 * @param className
	 * @return Auditable class tag
	 */
	private String getAuditableClassTag(String className)
	{
		String relationshipType = "main";
		String roleName = "";
		String auditableClass = AuditXMLConstants.AUDIT_CLASS_TAG;
		auditableClass = auditableClass.replace(AuditXMLConstants.CLASS_NAME_TOKEN, className);
		auditableClass = auditableClass.replace(AuditXMLConstants.ROLE_NAME_TOKEN, roleName);
		auditableClass = auditableClass.replace(AuditXMLConstants.RELATIONSHIP_TYPE_TOKEN,
				relationshipType);

		return auditableClass;

	}

	/**
	 * @param field
	 * @return attribute tag
	 */
	private String getAttributeTag(Field field)
	{
		String attributeTag = null;
		if (field.getType().getName().startsWith("java.util.")
				&& !field.getType().getName().startsWith("java.util.Date"))
		{
			attributeTag = getContainmentAssociationCollection(field);
		}
		else
		{
			String fieldName = field.getName();
			String fieldType = field.getType().getName();
			attributeTag = AuditXMLConstants.ATTRIBUTE_TAG;
			attributeTag = attributeTag.replace(AuditXMLConstants.NAME_TOKEN, fieldName);
			attributeTag = attributeTag.replace(AuditXMLConstants.DATA_TYPE_TOKEN, fieldType);
		}
		return attributeTag;
	}

	/**
	 * @param field
	 * @return Containment association collection tag
	 */
	private String getContainmentAssociationCollection(Field field)
	{
		String containmentAssociationTag = AuditXMLConstants.CONTAINMENT_ASSOCIATION_COLLECTION_TAG;
		String relationshipType = "containment";
		String roleName = field.getName();
		String className = field.getType().getName();

		containmentAssociationTag = containmentAssociationTag.replace(
				AuditXMLConstants.CLASS_NAME_TOKEN, className);
		containmentAssociationTag = containmentAssociationTag.replace(
				AuditXMLConstants.RELATIONSHIP_TYPE_TOKEN, relationshipType);
		containmentAssociationTag = containmentAssociationTag.replace(
				AuditXMLConstants.ROLE_NAME_TOKEN, roleName);
		return containmentAssociationTag;
	}
}
