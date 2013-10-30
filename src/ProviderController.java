import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/23/13
 * Time: 7:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProviderController implements iController
{
    private Provider loggedInProvider;
    private ArrayList<Provider> providers = new ArrayList<Provider>();
    private HashMap<Integer, Provider> testProviders = new HashMap<Integer, Provider>();
    public final static String VALID = "Valid";
    public final static String INVALID = "Invalid";
    public File providerFile;
    private MemberController memberController;
    private ServiceDirectory serviceDirectory;

    public ProviderController (MemberController memberController, ServiceDirectory serviceDirectory)
    {
        this.memberController = memberController;
        this.serviceDirectory = serviceDirectory;
        providers = populateProvidersList();
    }

    public ProviderController(String file)
    {
        open(file);
    }
    @Override
    public void open(String file)
    {
        try
        {
            providerFile = new File(file);
            // if the provider file does not exist, load our canned one.
            if(!providerFile.exists())
            {
                providers = populateProvidersList();
                for(Provider pro : providers)
                {
                    testProviders.put(pro.getIdentifier(), pro);
                }
                save();
            }
            FileInputStream fis = new FileInputStream(providerFile);
            XMLDecoder decoder = new XMLDecoder(fis);
            testProviders = (HashMap<Integer, Provider>)decoder.readObject();
            decoder.close();
        }
        catch(Exception e)
        {
            System.out.println("Exception during deserialization: " +  e);
            System.exit(0);
        }
    }

    @Override
    public void close()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<iPerson> read()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void save()
    {
        try
        {
            if(!providerFile.exists())
            {
                providerFile.createNewFile();
            }
            //String fullPath = file.getAbsolutePath();
            FileOutputStream os = new FileOutputStream(providerFile);
            XMLEncoder encoder = new XMLEncoder(os);
            encoder.writeObject(testProviders);
            encoder.close();
        }
        catch (Exception ex)
        {
            System.out.println("Exception during serialization: " +  ex);
            System.exit(0);
        }
    }

    @Override
    public iPerson find(int id)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void add(iPerson toAdd)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void remove(int id)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Provider getLoggedInProvider()
    {
        return loggedInProvider;
    }

    public String tryLogInProvider(int providerID)
    {
        String state = INVALID;
        if(Provider.validateProviderNumberLength(providerID))
        {
            loggedInProvider = (Provider)find(providerID);
            if(loggedInProvider != null)
            {
                state = VALID;
            }
        }
        return state;
    }

    public int addMemberServiceRecord(Service service)
    {
        // TODO: add service to member
        return -1; // TODO: what to really return here?
    }

    public String checkMemberStatus(int memberID)
    {
        return memberController.getMemberStatus(memberID);
    }

    ArrayList<Provider> populateProvidersList()
    {
        ArrayList<Provider> providersList = new ArrayList<Provider>();

        providersList.add(new Provider("Group Therapeutics, Inc", "2104 W Wells St", "Milwaukee", "WI", 53233,
                111222333));
        providersList.add(new Provider("West Allis Counseling Center", "5935 W Beloit Rd", "West Allis", "WI", 53219,
                111222444));
        providersList.add(new Provider("Addiction Treatment Center", "725 N. Mayfair Rd", "Wauwatosa", "WI", 53226,
                111222555));
        providersList.add(new Provider("Massage Works", "8436 W Lisbon Ave", "Milwaukee", "WI", 53222, 111222666));
        providersList.add(new Provider("Natural Supplement Center", "11225 W Bluemound Rd", "Wauwatosa", "WI", 53226,
                111222777));
        providersList.add(new Provider("Health Energy Spot", "16735 W Greenfield Ave", "New Berlin", "WI", 53151,
                111222888));
        providersList.add(new Provider("Nutrition Counseling, SC", "14530 W Capitol Dr", "Brookfield", "WI", 53005,
                111222999));
        providersList.add(new Provider("Vitamin Hut", "2175 S. 60th St", "West Allis", "WI", 53219, 111333000));
        providersList.add(new Provider("YMCA of New Berlin", "19555 W Lincoln Ave", "New Berlin", "WI", 53146,
                111333111));
        providersList.add(new Provider("Neighborhood Counseling Center", "6267 N 76th St", "Milwaukee", "WI", 53218,
                111333222));

        return providersList;
    }
}
