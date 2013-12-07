import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: Susan_2
 * Date: 12/7/13
 * Time: 4:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceDirectory {

    private String reportContent;
    //private HashMap<String, Double> serviceEntries = new HashMap<String, Double>();
    private StringBuilder reportBuilder = new StringBuilder();
    private List<String> serviceList = new ArrayList<String>();

    public ServiceDirectory()
    {
        reportBuilder.append(String.format("%-27s%-11s%s%n", "Service", "Code", "Fee"));
    }

    public void sendReport(String destination)
    {
        try
        {
            PrintWriter out = new PrintWriter(destination);
            out.write(reportContent);
            out.close();
        }
        catch (Exception ex)
        {
            // TODO: log exception
        }
    }

    public void addServiceEntry(String service, int code, Double amount)
    {
        //reportBuilder.append(String.format("%-25s  %9d  $%,5.2f%n", name.length() > 25 ? name.substring(0,25) : service, code, amount));
        serviceList.add(String.format("%-25s  %9d  $%,5.2f%n", service.length() > 25 ? service.substring(0,25) : service, code, amount));
    }

    public void sortEntries()
    {
        Collections.sort(serviceList);
    }

    public void putIntoReport()
    {
        for (String s : serviceList)  //value is equal to a String value
        {
            reportBuilder.append(s);
        }

    }

    public void executeDirectory()
    {
        sortEntries();
        putIntoReport();
        reportContent = reportBuilder.toString();
        sendReport("ServiceDirectory.txt");
    }
}
