/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/catissue-dao/LICENSE.txt for details.
 */

package test;

/**
 * @author kalpana_thakur
 *
 */

public class Address
{
	/**
	 * identifier.
	 */
    private Long identifier;

    /**
	 * street.
	 */
	private String street;


    /**
	 * @return identifier.
	 */
    public Long getIdentifier()
    {
		return identifier;
	}

    /**
	 * @param identifier :
	 */
	public void setIdentifier(Long identifier)
	{
		this.identifier = identifier;
	}

	/**
	 * @return street.
	 */
	public String getStreet()
	{
		return street;
	}
	/**
	 * @param street :
	 */
	public void setStreet(String street)
	{
		this.street = street;
	}


}
