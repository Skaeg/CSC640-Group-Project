import java.io.FileInputStream;
import java.io.ObjectInputStream;
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
    Provider loggedInProvider;
    ArrayList<Provider> providers = new ArrayList<Provider>();
    public final static String VALID = "Valid";
    public final static String INVALID = "Invalid";

    ProviderController ()
    {

    }

    @Override
    public void open(String file)
    {
        try
        {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            providers.add((Provider)ois.readObject());
            ois.close();
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
        //To change body of implemented methods use File | Settings | File Templates.
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
}
