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
