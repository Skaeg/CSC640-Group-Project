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
public class Member implements iPerson, Serializable
{
  private String name;
  private String streetAddress; //up to 25 chars
  private String city;          //up to 14 chars
  private String state;         //2 chars
  private int zipcode;          //5 digits
  private int memberID = -1;    //9 digits
  private ArrayList<Service> listOfServices;

    public Member (){}

    public Member(String fname, String lname, String streetAddress, String city, String state, int zipcode , int id)
    {
        this.name = String.format("%s %s", fname, lname);
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.memberID = id;
        listOfServices = new ArrayList<Service>();
    }

    @Override
    public String getName()
    {
         return name;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setName(String first, String last)
    {
        String.format("%s %s", first, last);
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
    public int getIdentifier() {
        return this.memberID;
    }

    @Override
    public void setIdentifier(int id) {
        this.memberID = id;
    }

    public void addServiceToList(Service service){
        this.listOfServices.add(service);
    }
    public List getListOfServices(){
        return this.listOfServices;
    }
}

