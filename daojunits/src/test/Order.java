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
