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


    //this was *sort of* built, or at least a few parts were.  i applied the same thing that the provider and member had
    //i hope this is what needed to be done.  As with the member, if it is not correct feel free to strip it down
    //and just use the data. - Susan
    public ArrayList<Service> getListOfServices()
    {
        ArrayList<Service> services = new ArrayList<Service>();

        services.add(new Service("Massage Therapy", 65.00, 598385));
        services.add(new Service("Session With Dietitian", 85.00, 598470));
        services.add(new Service("Aerobics Exercise Session", 15.00, 883948));
        services.add(new Service("Individual Counseling", 125.00, 873546));
        services.add(new Service("Group Counseling", 25.00, 582395));
        services.add(new Service("Phone Counseling", 65.00, 808341));
        services.add(new Service("Cooking Class", 45.00, 539136));
        services.add(new Service("Health Assessment", 65.00, 773522));
        services.add(new Service("Aromatherapy", 45.00,6943228));

        return services;
    }

    Service getService(int serviceCode)
    {
        return services.get(serviceCode);
    }
}
