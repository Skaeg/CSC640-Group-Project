import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/21/13
 * Time: 5:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceDirectory
{
    private HashMap<Integer, Service> services = new HashMap<Integer, Service>();
    private File serviceDirectoryFile;

    public ServiceDirectory(String file)
    {
        open(file);
    }

    public Boolean open(String file)
    {
        Boolean openSuccessful = false;
        FileInputStream fis = null;
        XMLDecoder decoder = null;
        try
        {
            serviceDirectoryFile = new File(file);
            // if the provider file does not exist, load our canned one.
            if(!serviceDirectoryFile.exists())
            {
                services = populateListOfServices();
                save(file);
            }
            else
            {
                fis = new FileInputStream(serviceDirectoryFile);
                decoder = new XMLDecoder(fis);
                services = (HashMap<Integer, Service>)decoder.readObject();
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

    public Boolean save(String file)
    {
        FileOutputStream os = null;
        XMLEncoder encoder = null;
        Boolean saveSuccessful = false;
        try
        {
            serviceDirectoryFile = new File(file);
            if(!serviceDirectoryFile.exists())
            {
                serviceDirectoryFile.createNewFile();
            }
            //String fullPath = file.getAbsolutePath();
            os = new FileOutputStream(serviceDirectoryFile);
            encoder = new XMLEncoder(os);
            encoder.writeObject(services);
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

    public Boolean add(Service toAdd)
    {
        Boolean addedSuccessfully = false;
        if(!services.containsKey(toAdd.getServiceCode()))
        {
            services.put(toAdd.getServiceCode(), toAdd);
            addedSuccessfully = true;
        }
        return addedSuccessfully;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Boolean remove(int serviceCode)
    {
        Boolean removedSuccessfully = false;
        if(!services.containsKey(serviceCode))
        {
            services.remove(serviceCode);
            removedSuccessfully = true;
        }
        return removedSuccessfully;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public HashMap<Integer, Service> populateListOfServices()
    {
        HashMap<Integer, Service> services = new HashMap<Integer, Service>();

        services.put(598385, new Service("Massage Therapy", 65.00, 598385));
        services.put(598470, new Service("Session With Dietitian", 85.00, 598470));
        services.put(883948, new Service("Aerobics Exercise Session", 15.00, 883948));
        services.put(873546, new Service("Individual Counseling", 125.00, 873546));
        services.put(582395, new Service("Group Counseling", 25.00, 582395));
        services.put(808341, new Service("Phone Counseling", 65.00, 808341));
        services.put(539136, new Service("Cooking Class", 45.00, 539136));
        services.put(773522, new Service("Health Assessment", 65.00, 773522));
        services.put(694322, new Service("Aromatherapy", 45.00, 694322));
        services.put(867530, new Service("Jenny", 9.00, 867530));

        return services;
    }

    Service getService(int serviceCode)
    {
        Service service = null;
        if(services.containsKey(serviceCode))
        {
            service = services.get(serviceCode);
        }
        return service;
    }

    public boolean serviceDirectoryFileOpen()
    {
        return serviceDirectoryFile != null ? true : false;
    }

    public Set<Service> getAllServices()
    {          Set<Service> allServices = new HashSet<Service>();

        for(Integer i : services.keySet()){
            allServices.add(services.get(i));
        }

        return  allServices;
    }
}
