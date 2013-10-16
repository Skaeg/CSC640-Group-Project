/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/16/13
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class StreetAddress
{

    private String Address;
    private String City;
    private String State;
    private String ZipCode;

    public StreetAddress(String address, String city, String state, String zipCode)
    {
        Address = address;
        City = city;
        State = state;
        ZipCode = zipCode;
    }

    public String getAddress()
    {
        return Address;
    }

    public String getCity()
    {
        return City;
    }

    public String getState()
    {
        return State;
    }

    public String getZipCode()
    {
        return ZipCode;
    }

}
