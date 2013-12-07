import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Susan_2
 * Date: 11/6/13
 * Time: 5:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdministratorController implements iController, iLogged
{

    private HashMap<Integer, Administrator> administrators = new HashMap<Integer, Administrator>();
    private File administratorFile = null;
    private iEmployee loggedIn = null;
    public final static String VALID = "Valid";
    public final static String INVALID = "Invalid";

    public AdministratorController(String file)
    {
        //this.memberController = memberController;
        //this.serviceController = serviceController;
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
            administratorFile = new File(file);
            // if the provider file does not exist, load our canned one.
            if(!administratorFile.exists())
            {
                administrators = populateAdministratorsList();
                save(file);
            }
            else
            {
                fis = new FileInputStream(administratorFile);
                decoder = new XMLDecoder(fis);
                administrators = (HashMap<Integer, Administrator>)decoder.readObject();
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
            administratorFile = new File(file);
            if(!administratorFile.exists())
            {
                administratorFile.createNewFile();
            }
            //String fullPath = file.getAbsolutePath();
            os = new FileOutputStream(administratorFile);
            encoder = new XMLEncoder(os);
            encoder.writeObject(administrators);
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
        return administrators.get(id);
    }

    @Override
    public Boolean add(iPerson toAdd)
    {
        Boolean addSuccessful = false;
        if(!administrators.containsKey( toAdd.getIdentifier()))
        {
            administrators.put(toAdd.getIdentifier(), (Administrator)toAdd);
            addSuccessful = true;
        }
        return addSuccessful;
    }

    @Override
    public Boolean remove(int id)
    {
        Boolean removeSuccessful = false;
        if(administrators.containsKey(id))
        {
            administrators.remove(id);
            removeSuccessful = true;
        }
        return removeSuccessful;
    }

    private HashMap<Integer, Administrator> populateAdministratorsList()
    {
        HashMap<Integer, Administrator> administratorsList = new HashMap<Integer, Administrator>();

        administratorsList.put(555444111, new Administrator("Steve Koxlien", "1342 Lincoln Ave", "Waukesha", "WI", 53186, 555444111));
        administratorsList.put(555444222, new Administrator("Mark Noller", "1200 S. Moorland Rd", "Muskego", "WI", 53154, 555444222));
        administratorsList.put(555444333, new Administrator("Susan Retzer", "2110 N 68th St", "Wauwatosa", "WI", 53213, 555444333));

        return administratorsList;
    }

    public boolean administratorFileOpen()
    {
        return administratorFile != null ? true : false;
    }

    @Override
    public String tryLogIn(int id)
    {
        String state = INVALID;
        loggedIn = (Administrator)find(id);
        if(loggedIn != null)
        {
            state = VALID;
        }
        return state;

    }

    @Override
    public iEmployee getLoggedIn()
    {
        return loggedIn;
    }

    @Override
    public void logout()
    {
        loggedIn = null;
    }
}
