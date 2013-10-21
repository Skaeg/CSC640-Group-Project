import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/21/13
 * Time: 5:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceRecord
{
    private int providerID;
    private int memberID;
    private int serviceCode;
    private String comments;
    private Calendar dateAndTimeServiceEntered;
    private Calendar dateOfService;

    public ServiceRecord(int _providerID, int _memberID, int _serviceCode, String _comments,
                         Calendar _dateAndTimeServiceEntered, Calendar _dateOfService)
    {
        providerID = _providerID;
        memberID = _memberID;
        serviceCode = _serviceCode;
        comments = _comments;
        dateAndTimeServiceEntered = _dateAndTimeServiceEntered;
        dateOfService = _dateOfService;
    }
    public int getProviderID()
    {
        return providerID;
    }

    public int getMemberID()
    {
        return memberID;
    }

    public int getServiceCode()
    {
        return serviceCode;
    }

    public String getComments()
    {
        return comments;
    }

    public Calendar getDateAndTimeServiceEntered()
    {
        return dateAndTimeServiceEntered;
    }

    public Calendar getDateOfService()
    {
        return dateOfService;
    }
}
