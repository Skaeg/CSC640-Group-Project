import java.util.ArrayList;
import java.util.HashMap;

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

    public ArrayList<String> getListOfServices()
    {
        ArrayList<String> services = new ArrayList<String>();

        return services;
    }

    Service getService(int serviceCode)
    {
        return services.get(serviceCode);
    }
}
