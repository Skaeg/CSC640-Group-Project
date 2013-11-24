import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/21/13
 * Time: 6:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class MemberReport implements iReport
{
    private Member member;
    private Set<ServiceRecord> serviceRecords;
    private ServiceDirectory serviceDirectory;

    StringBuilder reportContent;

    public MemberReport(ServiceDirectory serviceDirectory, Member member, Set<ServiceRecord> serviceRecords)
    {
        this.member =  member;
        this.serviceRecords = serviceRecords;
        this.serviceDirectory = serviceDirectory;
    }

    @Override
    public void sendReport(String destination)
    {
        if(member == null || serviceRecords == null || serviceDirectory == null){
            System.out.println("Cannot write memberReport to file due to null value");

            return;
        }
        if(reportContent == null){
            reportContent = new StringBuilder();
        }

        // Member Info
        reportContent.append(String.format("Name: %s%n", member.getName()));
        reportContent.append(String.format("Member Number: %s%n", member.getIdentifier()));
        reportContent.append(String.format("Address: %s %s, %s %s%n%n", member.getStreetAddress(),
                member.getCity(), member.getState(),member.getZipcode()));

        // List of services
        for(ServiceRecord s : serviceRecords ){
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            reportContent.append(String.format("Date of service: %s%n", sdf.format(s.getDateOfService().getTime())));
            reportContent.append(String.format("Provider Number: %d%n", s.getProviderID()));
            reportContent.append(String.format("Service provided: %s%n%n" , serviceDirectory.getService(s.getServiceCode())));
        }

        try
        {
            PrintWriter out = new PrintWriter(destination);
            out.write(reportContent.toString());
            out.close();
        }
        catch (Exception ex)
        {
            System.out.println("Something went wrong writing memberReport to file");
            // TODO: log exception
        }

    }

}
