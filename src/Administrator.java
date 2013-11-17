import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/13/13
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class Administrator implements iPerson, iRequestReport, iEmployee
{
    private String name;          //up to 25 chars
    private String streetAddress; //up to 25 chars
    private String city;          //up to 14 chars
    private String state;         //2 chars
    private int zipcode;          //5 digits
    private int identifier;       //9 digits

    public Administrator()
    {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public Administrator(String name, String streetAddress, String city, String state, int zipcode , int id)
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
    public void setName(String name)
    {
        this.name = name;
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

    public static Boolean validateIdentifier(int id)
    {
        return id < 1000000000;
    }


    //// up to here


   public int saveMember(Member m)
    {
        //To change body of implemented methods use File | Settings | File Templates.
        return -1;
    }

    public Member getMember(int id)
    {
        //To change body of implemented methods use File | Settings | File Templates.
        return null;
    }

    public int deleteMember(int id)
    {
        //To change body of implemented methods use File | Settings | File Templates.
        return -1;
    }

    public int saveProvider(Provider p)
    {
        //To change body of implemented methods use File | Settings | File Templates.
        return -1;
    }

    public int deleteProvider(int id)
    {
        //To change body of implemented methods use File | Settings | File Templates.
        return -1;
    }

    public Provider getProvider(int id)
    {
        //To change body of implemented methods use File | Settings | File Templates.
        return null;
    }

    @Override
    public void requestReport(int reportType) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getMenuItems()
    {
        // TODO: this list of menu items must be updated
        ArrayList<String> menuItems = new ArrayList<String>();
        menuItems.add("{I}nteractive Mode");
        menuItems.add("{R}eports");
        menuItems.add("{L}ogout Administrator");
        menuItems.add("{Q}uit");
        return menuItems;
    }
}
