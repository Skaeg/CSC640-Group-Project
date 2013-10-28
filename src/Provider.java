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
    @Override
    public String getName()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setName(String first, String last)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getAddress()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setAddress(String address)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getIdentifier()
    {
        return -1;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setIdentifier(int id)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public int addMemberServiceRecord(Service service)
    {
        //To change body of implemented methods use File | Settings | File Templates.
        return -1;
    }

    public String checkMemberStatus(int memberID)
    {
        //To change body of implemented methods use File | Settings | File Templates.
        return null;
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
