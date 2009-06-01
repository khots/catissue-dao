package test;

/**
 * @author kalpana_thakur
 *
 */
public class Order
{
	/**
	 * id.
	 */
    private Long identifier;

    /**
	 * Person.
	 */
	private Person person;


    /**
     * @return person
     */
    public Person getPerson()
    {
		return person;
	}

	/**
	 * @param person person.
	 */
	public void setPerson(Person person)
	{
		this.person = person;
	}

	/**
	 * @return : identifier
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

}
