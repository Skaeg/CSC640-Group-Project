import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/23/13
 * Time: 6:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class EFTReport implements iReport
{
    private String reportContent;
    private HashMap <String, Double> EFTEntries = new HashMap<String, Double>();
    private StringBuilder reportBuilder = new StringBuilder();

    public EFTReport()
    {
        reportBuilder.append(String.format("%-27s%-11s%s%n", "Provider", "ID", "Total"));
    }

    @Override
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

    public void addEFTEntry(String name, int ID, Double amount)
    {
         reportBuilder.append(String.format("%-25s  %9d  $%,5.2f%n", name.length() > 25 ? name.substring(0,25) : name, ID, amount));
    }

    public void executeReport()
    {
        reportContent = reportBuilder.toString();
    }
}
