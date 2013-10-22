/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/13/13
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class Administrator implements iPerson, iRequestReport
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
}
