import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/23/13
 * Time: 7:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProviderController implements iController, iLogged
{
    private iEmployee loggedInProvider;
    private HashMap<Integer, Provider> providers = new HashMap<Integer, Provider>();
    private File providerFile = null;
    public final static String VALID = "Valid";
    public final static String INVALID = "Invalid";
    private MemberController memberController;
    private ServiceController serviceController;

    public ProviderController(String file, MemberController memberController, ServiceController serviceController)
    {
        this.memberController = memberController;
        this.serviceController = serviceController;
        open(file);
    }
    @Override
    public Boolean open(String file)
    {
        Boolean openSuccessful = false;
        FileInputStream fis = null;
        XMLDecoder decoder = null;
        try
        {
            providerFile = new File(file);
            // if the provider file does not exist, load our canned one.
            if(!providerFile.exists())
            {
                providers = populateProvidersList();
                save(file);
            }
            else
            {
                fis = new FileInputStream(providerFile);
                decoder = new XMLDecoder(fis);
                providers = (HashMap<Integer, Provider>)decoder.readObject();
            }
        }
        catch(Exception e)
        {
            System.out.println("Exception during deserialization: " +  e);
            System.exit(0);
        }
        finally
        {
            if(decoder != null)
            {
                decoder.close();
            }
            if(fis != null)
            {
                try
                {
                    fis.close();
                } catch (IOException e)
                {
                    System.out.println("Exception during deserialization: " +  e);
                    System.exit(0);
                }
            }
        }
        return openSuccessful;
    }

    @Override
    public Boolean save(String file)
    {
        FileOutputStream os = null;
        XMLEncoder encoder = null;
        Boolean saveSuccessful = false;
        try
        {
            providerFile = new File(file);
            if(!providerFile.exists())
            {
                providerFile.createNewFile();
            }
            //String fullPath = file.getAbsolutePath();
            os = new FileOutputStream(providerFile);
            encoder = new XMLEncoder(os);
            encoder.writeObject(providers);
            saveSuccessful = true;
        }
        catch (Exception ex)
        {
            System.out.println("Exception during serialization: " +  ex);
            System.exit(0);
        }
        finally
        {
            if(encoder != null)
            {
                encoder.close();
            }
            if(os != null)
            {
                try
                {
                    os.close();
                }
                catch (IOException e)
                {
                    System.out.println("Exception during serialization: " +  e);
                    System.exit(0);
                }
            }
        }
        return saveSuccessful;
    }

    @Override
    public iPerson find(int id)
    {
        return providers.get(id);
    }

    @Override
    public Boolean add(iPerson toAdd)
    {
        Boolean addSuccessful = false;
        if(!providers.containsKey( toAdd.getIdentifier()))
        {
            providers.put(toAdd.getIdentifier(), (Provider)toAdd);
            addSuccessful = true;
        }
        return addSuccessful;
    }

    @Override
    public Boolean remove(int id)
    {
        Boolean removeSuccessful = false;
        if(providers.containsKey(id))
        {
            providers.remove(id);
            removeSuccessful = true;
        }
        return removeSuccessful;
    }

    @Override
    public String tryLogIn(int id)
    {
        String state = INVALID;
        if(Provider.validateIdentifier(id))
        {
            loggedInProvider = (Provider)find(id);
            if(loggedInProvider != null)
            {
                state = VALID;
            }
        }
        return state;
    }

    @Override
    public iEmployee getLoggedIn()
    {
        return loggedInProvider;
    }

    @Override
    public void logout()
    {
        loggedInProvider = null;
    }

    public boolean checkMemberStatus(int memberID)
    {
        return memberController.getMemberStatus(memberID);
    }

    private HashMap<Integer, Provider> populateProvidersList()
    {
        HashMap<Integer, Provider> providersList = new HashMap<Integer, Provider>();

        providersList.put(111222333, new Provider("Group Therapeutics, Inc", "2104 W Wells St", "Milwaukee", "WI", 53233, 111222333));
        providersList.put(111222444, new Provider("West Allis Counseling Center", "5935 W Beloit Rd", "West Allis", "WI", 53219, 111222444));
        providersList.put(111222555, new Provider("Addiction Treatment Center", "725 N. Mayfair Rd", "Wauwatosa", "WI", 53226, 111222555));
        providersList.put(111222666, new Provider("Massage Works", "8436 W Lisbon Ave", "Milwaukee", "WI", 53222, 111222666));
        providersList.put(111222777, new Provider("Natural Supplement Center", "11225 W Bluemound Rd", "Wauwatosa", "WI", 53226, 111222777));
        providersList.put(111222888, new Provider("Health Energy Spot", "16735 W Greenfield Ave", "New Berlin", "WI", 53151, 111222888));
        providersList.put(111222999, new Provider("Nutrition Counseling, SC", "14530 W Capitol Dr", "Brookfield", "WI", 53005, 111222999));
        providersList.put(111333000, new Provider("Vitamin Hut", "2175 S. 60th St", "West Allis", "WI", 53219, 111333000));
        providersList.put(111333111, new Provider("YMCA of New Berlin", "19555 W Lincoln Ave", "New Berlin", "WI", 53146, 111333111));
        providersList.put(111333222, new Provider("Neighborhood Counseling Center", "6267 N 76th St", "Milwaukee", "WI", 53218, 111333222));

        return providersList;
    }

    public boolean providerFileOpen()
    {
        return providerFile != null ? true : false;
    }

    public HashMap<Integer, Provider> getProviders()
    {
        return providers;
    }

    public Provider getProvider(int providerID)
    {
        return providers.get(providerID);
    }
}
