import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/21/13
 * Time: 5:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceRecord implements Serializable
{
    private int providerID;
    private int memberID;
    private int serviceCode;
    private String comments;
    private SimpleDateTime dateAndTimeServiceEntered;
    private SimpleDateTime dateOfService;

    public ServiceRecord(int _providerID, int _memberID, int _serviceCode, String _comments,
                         SimpleDateTime _dateAndTimeServiceEntered, SimpleDateTime _dateOfService)
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

    public SimpleDateTime getDateAndTimeServiceEntered()
    {
        return dateAndTimeServiceEntered;
    }

    public SimpleDateTime getDateOfService()
    {
        return dateOfService;
    }
}
