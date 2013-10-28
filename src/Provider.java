import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/16/13
 * Time: 4:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class Provider implements iPerson, Serializable
{
    private String name;
    private String address;
    private int identifier;
    private ArrayList<Service> services = new ArrayList<Service>();

    public Provider()
    {
    }

    public Provider(String name, String address, int id)
    {
        this.name = name;
        this.address = address;
        identifier = id;
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
    public String getAddress()
    {
        return address;
    }

    @Override
    public void setAddress(String address)
    {
        this.address = address;
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

    public int addMemberServiceRecord(Service service)
    {
        services.add(service);
        return -1; // TODO: what to really return here?
    }

    public String checkMemberStatus(int memberID)
    {
        return memberController.getMemberStatus(memberID);
    }

    public ServiceDirectory requestServiceDirectory()
    {
        //To change body of implemented methods use File | Settings | File Templates.
        return null;
    }

    public static Boolean validateProviderNumberLength(int number)
    {
        // validate
        return number < 1000000;
    }



}
