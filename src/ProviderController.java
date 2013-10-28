import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
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
    public final static String VALID = "Valid";
    public final static String INVALID = "Invalid";
    public String providerFile;
    private MemberController memberController;
    private ServiceDirectory serviceDirectory;

    ProviderController (MemberController memberController, ServiceDirectory serviceDirectory)
    {
        this.memberController = memberController;
        this.serviceDirectory = serviceDirectory;
        providers = populateProvidersList();
    }

    @Override
    public void open(String file)
    {
        try
        {
            providerFile = file;
            FileInputStream fis = new FileInputStream(providerFile);
            XMLDecoder decoder = new XMLDecoder(fis);
            providers = (ArrayList<Provider>)decoder.readObject();
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
            FileOutputStream os = new FileOutputStream(providerFile);
            XMLEncoder encoder = new XMLEncoder(os);
            encoder.writeObject(providers);
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

    ArrayList<Provider> populateProvidersList()
    {
        ArrayList<Provider> providersList = new ArrayList<Provider>();
        providersList.add(new Provider("Steve", "1234 West Madeup Lane, Waukesha, WI 12345", 6789));
        return providersList;
    }
}
