import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/16/13
 * Time: 4:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class Provider implements iPerson, Serializable
{
    private String name;          //up to 25 chars
    private String streetAddress; //up to 25 chars
    private String city;          //up to 14 chars
    private String state;         //2 chars
    private int zipcode;          //5 digits
    private int identifier;       //9 digits

    public Provider()
    {
    }

    public Provider(String name, String streetAddress, String city, String state, int zipcode , int id)
    {
        this.name = name;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.identifier = id;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String first, String last)
    {
        name = first + last;
    }

    @Override
    public String getStreetAddress()
    {
        return streetAddress;
    }

    @Override
    public void setStreetAddress(String streetAddress)
    {
        this.streetAddress = streetAddress;
    }


    @Override
    public void setCity(String city)
    {
        this.city = city;
    }

    @Override
    public String getCity()
    {
        return city;
    }

    @Override
    public void setState(String state)
    {
        this.state = state;
    }

    @Override
    public String getState()
    {
        return state;
    }

    @Override
    public void setZipcode(int zipcode)
    {
        this.zipcode = zipcode;
    }

    @Override
    public int getZipcode()
    {
        return zipcode;
    }


    @Override
    public int getIdentifier()
    {
        return identifier;
    }

    @Override
    public void setIdentifier(int id)
    {
        if(validateProviderNumberLength(id))
        {
            identifier = id;
        }
    }

    public ServiceDirectory requestServiceDirectory()
    {
        //To change body of implemented methods use File | Settings | File Templates.
        return null;
    }

    public static Boolean validateProviderNumberLength(int number)
    {
        // validate
        return number < 1000000000;
    }



}
