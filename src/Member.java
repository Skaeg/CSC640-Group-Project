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
  private String address = "not entered";
  private int memberID = -1;

    Member(String first, String last){
        this.fname = first;
        this.lname = last;
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

    @Override
    public String getAddress()
    {
        return this.address;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
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
