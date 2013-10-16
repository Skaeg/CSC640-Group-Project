/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/13/13
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class Administrator implements iPerson
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

    public iReport RequestReport(ReportType reportTypeRequest)
    {
        return ReportFactory.CreateReport(reportTypeRequest);
    }
}
