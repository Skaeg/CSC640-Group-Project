import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;


public class ManagerSummaryReport implements iReport
{
    private String reportContent;
   // private HashMap<String, Double> SummaryEntries = new HashMap<String, Double>();
    private StringBuilder reportBuilder = new StringBuilder();
    private double feeTotal = 0;
    private int consultationsTotal = 0;
    private int providerTotal = 0;

    public ManagerSummaryReport()
    {
        reportBuilder.append(String.format("%-27s%-15s%s%n", "Provider", "Consultations", "Total"));
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

    public void addSummaryEntry(String name, int consultations, Double amount)
    {
        reportBuilder.append(String.format("%-27s      %-5d  $%8.2f%n", name.length() > 25 ? name.substring(0,25) : name, consultations, amount));
        feeTotal += amount;
        consultationsTotal += consultations;
        providerTotal++;
    }

    public void executeReport()
    {
        reportBuilder.append(String.format("%n%nTotal Number of Providers providing services: %d%n", providerTotal));
        reportBuilder.append(String.format("Total Number of Consultations: %d%n", consultationsTotal));
        reportBuilder.append(String.format(Locale.ENGLISH, "Total Overall Fee: $%,5.2f%n", feeTotal));
        reportContent = reportBuilder.toString();
    }
}
