import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/21/13
 * Time: 6:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProviderReport implements iReport
{
    String reportContent;

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

    public void executeReport(Provider provider, Set<ServiceRecord> tempSet, MemberController members, ServiceDirectory serviceDirectory)
    {
        StringBuilder reportBuilder = new StringBuilder();
        double feeTotal = 0;
        int consultations = 0;
        // Header
        reportBuilder.append(String.format("Provider: %-20s Provider Number: %09d%n", provider.getName(), provider.getIdentifier()));
        reportBuilder.append(String.format("Address: %s%n", provider.getStreetAddress()));
        reportBuilder.append(String.format("         %s, %s %d%n", provider.getCity(), provider.getState(), provider.getZipcode()));

        // Body
        reportBuilder.append(String.format("Date of Service \t Date of Entry \t Member Member ID Service Code Fee %n"));
        for(ServiceRecord sr : tempSet)
        {
            double fee = serviceDirectory.getService(sr.getServiceCode()).getServiceFee();
            reportBuilder.append(String.format("%s\t%s\t%-25s\t%09d\t%6d\t$%5.2f%n", getDateFromCalendar(sr.getDateOfService()),
                    getDateAndTimeFromCalendar(sr.getDateAndTimeServiceEntered()), members.getMember(sr.getMemberID()).getName(),
                    sr.getMemberID(), sr.getServiceCode(), fee));
            feeTotal += fee;
            consultations++;
        }

        // Summary
        reportBuilder.append(String.format("%n%nTotal consultations: %d%n", consultations));
        reportBuilder.append(String.format(Locale.ENGLISH, "Total Weekly Fee: $%5.2f", feeTotal));
        reportContent = reportBuilder.toString();
    }

    public String ToString()
    {
        return reportContent;
    }

    private String getDateFromCalendar(Calendar date)
    {
        return String.format("%2d-%2d-%4d", date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.YEAR));
    }

    private String getDateAndTimeFromCalendar(Calendar date)
    {
        return String.format("%2d-%2d-%4d %2d:%2d:%2d", date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.YEAR), date.get(Calendar.HOUR), date.get(Calendar.MINUTE), date.get(Calendar.SECOND));
    }
}
