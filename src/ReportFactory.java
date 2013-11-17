/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/16/13
 * Time: 5:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReportFactory
{
    public static iReport CreateReport(ReportType reportTypeRequested) throws Exception
    {
        iReport reportRequested = null;
        switch (reportTypeRequested)
        {
            case EFTReport:
            break;
            case ManagerSummaryReport:
            break;
            case MemberReport:
            break;
            case ProviderReport:
                reportRequested = new ProviderReport();
            break;
            default:
                throw new Exception("Invalid report type.");
                //break;
        }
        return reportRequested;
    }
}
