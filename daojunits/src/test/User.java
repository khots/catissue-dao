
package test;

/**
 * @author kalpana_thakur
 *
 */

public class User
{
	/**
	 * id.
	 */
    private Long identifier;
    /**
	 * lastName.
	 */
    private String lastName;
    /**
	 * firstName.
	 */
    private String firstName;
    /**
	 * emailAddress.
	 */
    private String emailAddress;
    /**
	 * activityStatus.
	 */
    private String activityStatus;

    /**
     * @return activityStatus
     */
    public String getActivityStatus()
    {
		return activityStatus;
	}

	/**
	 * @param activityStatus : Activity status.
	 */
	public void setActivityStatus(String activityStatus)
	{
		this.activityStatus = activityStatus;
	}

	/**
	 * @return  identifier
	 */
	public Long getIdentifier()
    {
        return identifier;
    }

    /**
     * @param id :
     */
    public void setIdentifier(Long id)
    {
        this.identifier = id;
    }

    /**
     * @return emailAddress
     */
    public String getEmailAddress()
    {
        return emailAddress;
    }

    /**
     * @param emailAddress :
     */
    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    /**
     * @return firstName
     */
    public String getFirstName()
    {
        return firstName;
    }
    /**
     * @param firstName :
     */
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }
    /**
     * @return lastName
     */
    public String getLastName()
    {
        return lastName;
    }
    /**
     * @param lastName :
     */
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }
}