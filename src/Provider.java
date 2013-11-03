import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/16/13
 * Time: 4:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class Provider implements iPerson, iEmployee, Serializable
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
        name = String.format("%s %s", first, last);
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
        if(validateIdentifier(id))
        {
            identifier = id;
        }
    }

    @Override
    public List<String> getMenuItems()
    {
        ArrayList<String> menuItems = new ArrayList<String>();
        menuItems.add("{P}rovide Service");
        menuItems.add("{A}dd Service To Member Account");
        menuItems.add("{E}nter Member Number");
        menuItems.add("{L}ogout Provider");
        menuItems.add("{Q}uit");
        return menuItems;
    }

    public static Boolean validateIdentifier(int id)
    {
        return id < 1000000000;
    }
}
