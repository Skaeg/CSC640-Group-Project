import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/21/13
 * Time: 6:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProviderReport implements iReport
{
    private HashMap<Integer, Provider> providers;
    private HashMap<Integer, Member> members;
    private HashMap<Integer, List<String>> providerReports = new HashMap<Integer, List<String>>();
    public ProviderReport(HashMap<Integer, Provider> providers, HashMap<Integer, Member> members)
    {
        this.providers = providers;
        this.members = members;
    }

    @Override
    public void sendReport()
    {

    }

    @Override
    public void executeReport()
    {
        for(Map.Entry<Integer, Provider> provider : providers.entrySet())
        {
            int providerID = provider.getValue().getIdentifier();
            for(Map.Entry<Integer, Member> member : members.entrySet())
            {
                // if the member has not had any services, skip to the next member
                List<ServiceRecord> memberServices = member.getValue().getListOfServices();
                if(memberServices.size() == 0)
                {
                    continue;
                }
                for(ServiceRecord memberService : memberServices)
                {
                    if(memberService.getProviderID() == providerID)
                    {
                        String serviceString = " ";
                        if(!providerReports.containsKey(providerID))
                        {
                            providerReports.put(providerID, new ArrayList<String>());
                        }
                        providerReports.get(providerID).add(serviceString);

                    }
                }
            }
        }
    }
}
