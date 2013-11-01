import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/16/13
 * Time: 4:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class Member implements iPerson
{
  private String fname = "";
  private String lname = "";
  //private String address = "not entered";
  private String streetAddress; //up to 25 chars
  private String city;          //up to 14 chars
  private String state;         //2 chars
  private int zipcode;          //5 digits
  private int memberID = -1;    //9 digits

  // I believe this can be removed -- Mark
  //  private ArrayList<Service> services = new ArrayList<Service>();

    Member(String first, String last){
        this.fname = first;
        this.lname = last;
    }

    public Member(String fname, String lname, String streetAddress, String city, String state, int zipcode , int id)
    {
        this.fname = fname;
        this.lname = lname;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.memberID = id;
    }

    @Override
    public String getName()
    {
         return this.fname + " " + this.lname;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setName(String first, String last)
    {
        this.fname = first;
        this.lname = last;
    }

    /*
    @Override
    public String getAddress()
    {
        return this.address;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }
    */

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
}
