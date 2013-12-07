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
  private boolean statusValid = false;
  private double feesOwed;

    public Member (){}

    public Member(String name, String streetAddress, String city, String state, int zipcode , int id, double feesOwed, boolean statusValid)
    {
        this.name = name;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.memberID = id;
        this.statusValid = statusValid;
        this.feesOwed = feesOwed;
    }

    @Override
    public String getName()
    {
         return name;  //To change body of implemented methods use File | Settings | File Templates.
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
    public int getIdentifier() {
        return this.memberID;
    }

    @Override
    public void setIdentifier(int id) {
        this.memberID = id;
    }

    public boolean isStatusValid() {
        return statusValid;
    }

    public void setStatusValid(boolean statusValid) {
        this.statusValid = statusValid;
    }

    public void debitAccount(double debit){
        this.feesOwed -= debit;
    }

    public void creditAccount(double credit){
        this.feesOwed += credit;
    }

    public double getFeesOwed() {
        return feesOwed;
    }

    public void setFeesOwed(double feesOwed) {
        this.feesOwed = feesOwed;
    }
}

