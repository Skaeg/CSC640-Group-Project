import java.io.Serializable;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: Mark Noller
 * Date: 11/14/13
 * Time: 7:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceRecord implements Serializable{
    private int providerID;
    private int memberID;
    private int serviceCode;
    private String comments;
    private GregorianCalendar dateAndTimeServiceEntered;
    private GregorianCalendar dateOfService;

    public ServiceRecord(){}

    public ServiceRecord(int providerID, int memberID, int serviceCode, String comments,
                         GregorianCalendar dateAndTimeServiceEntered, GregorianCalendar dateOfService){
        this.providerID = providerID;
        this.memberID = memberID;
        this.serviceCode = serviceCode;
        this.comments = comments;
        this.dateAndTimeServiceEntered = dateAndTimeServiceEntered;
        this.dateOfService = dateOfService;
    }

    public int getProviderID() {
        return providerID;
    }

    public void setProviderID(int providerID) {
        this.providerID = providerID;
    }

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }

    public int getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(int serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public GregorianCalendar getDateAndTimeServiceEntered() {
        return dateAndTimeServiceEntered;
    }

    public void setDateAndTimeServiceEntered(GregorianCalendar dateAndTimeServiceEntered) {
        this.dateAndTimeServiceEntered = dateAndTimeServiceEntered;
    }

    public GregorianCalendar getDateOfService() {
        return dateOfService;
    }

    public void setDateOfService(GregorianCalendar dateOfService) {
        this.dateOfService = dateOfService;
    }
}
