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
    public void setName(String name)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public StreetAddress getAddress()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setAddress(StreetAddress address)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getIdentifier()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setIdentifier(int)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public iReport RequestReport(reportType)
    {
        return void;
        //this was put in by Steve earlier - not sure if this is what should be done or???
        // return ReportFactory.CreateReport(reportTypeRequest);
    }

    public id saveMember(Member)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Member getMember(id)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public int deleteMember(id)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public id saveProvider(Provider)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public int deleteProvider(id)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Provider getProvider(id)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }



}
