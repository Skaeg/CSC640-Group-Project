import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Mark Noller
 * Date: 11/4/13
 * Time: 7:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceRecordController {
    // ArrayList<ServiceRecord>
    private ArrayList<ServiceRecord> servicesList = new ArrayList<ServiceRecord>();
    // HashMap<MemberID,Set<indexID>
    private HashMap<Integer, Set<Integer>> memberServicesMap = new HashMap<Integer, Set<Integer>>();
    // HashMap<ProviderID,Set<indexID>
    private HashMap<Integer, Set<Integer>> providerServicesMap = new HashMap<Integer, Set<Integer>>();

    private File serviceRecordFile = null;
    private String fileName;

    public ServiceRecordController(String file) {

        open(file);

    }

    private boolean open(String file)
    {
        try
        {
            serviceRecordFile = new File(file);
            // if the provider file does not exist, load our canned one.
            if (!serviceRecordFile.exists())
            {
                createMockServiceRecords();
                saveToFile(file,servicesList);
            }
            else
            {
                FileInputStream fis = new FileInputStream(serviceRecordFile);
                XMLDecoder decoder = new XMLDecoder(fis);
                ArrayList<ServiceRecord> tempServiceList = (ArrayList<ServiceRecord>) decoder.readObject();

                decoder.close();

                // Load records into the array and hashmaps
                for (ServiceRecord record : tempServiceList)
                {
                    loadServiceRecordToMemory(record);
                }
            }

        } catch (Exception e)
        {
            System.out.println("Exception during deserialization: " + e);
            //   System.exit(0);
            return false;
        }
        return true;
    }

    private boolean saveToFile(String file, ArrayList<ServiceRecord> serviceRecords) {
        try {
            if (serviceRecordFile == null) {
                serviceRecordFile = new File(file);
            }

            if (!serviceRecordFile.exists()) {
                serviceRecordFile.createNewFile();
            }
            //String fullPath = file.getAbsolutePath();
            FileOutputStream os = new FileOutputStream(serviceRecordFile);
            XMLEncoder encoder = new XMLEncoder(os);
            encoder.writeObject(serviceRecords);
            encoder.close();
        } catch (Exception ex) {
            System.out.println("Exception during serialization: " + ex);
            // System.exit(0);
            return false;
        }
        return true;
    }

    public boolean saveNewServiceRecord(ServiceRecord serviceRecord) {
        if (!loadServiceRecordToMemory(serviceRecord)) {
            return false;
        }
        return saveToFile(this.fileName, this.servicesList);
    }

    private boolean loadServiceRecordToMemory(ServiceRecord serviceRecord)
    {
        int newServiceIndex = servicesList.size();

        try
        {
            servicesList.add(serviceRecord);

            // Add ServiceRecord index to the list of services used by a member
            if (!memberServicesMap.containsKey(serviceRecord.getMemberID()))
            {
                memberServicesMap.put(serviceRecord.getMemberID(), new HashSet<Integer>());
            }
            memberServicesMap.get(serviceRecord.getMemberID()).add(newServiceIndex);

            // Add ServiceRecord index to a list of services provided by a provider
            if (!providerServicesMap.containsKey(serviceRecord.getProviderID()))
            {
                providerServicesMap.put(serviceRecord.getProviderID(), new HashSet<Integer>());
            }
            providerServicesMap.get(serviceRecord.getProviderID()).add(newServiceIndex);

            return true;

        }
        catch (Exception e)
        {
            return false;
        }
    }

    //Returns a Set of ServiceRecords for a given provider
    public Set<ServiceRecord> getListOfServiceRecordsByProvider(int providerID, Calendar startDate, Calendar endDate)
    {

        if (!providerServicesMap.containsKey(providerID) || providerServicesMap.get(providerID).isEmpty())
        {
            return null;
        }

        Set<ServiceRecord> tempSet = new HashSet<ServiceRecord>();

        for (Integer x : providerServicesMap.get(providerID))
        {
            ServiceRecord temp = servicesList.get(x);
            if(inDateRange(temp.getDateOfService(), startDate, endDate))
            {
                tempSet.add(temp);
            }
        }

        return tempSet;
    }

    //Returns a Set of ServiceRecords for a given memberID
    public Set<ServiceRecord> getListOfServiceRecordsByMember(int memberID, Calendar startDate, Calendar endDate)
    {
        if (!memberServicesMap.containsKey(memberID) || memberServicesMap.get(memberID).isEmpty())
        {
            return null;
        }

        Set<ServiceRecord> tempSet = new HashSet<ServiceRecord>();

        for (Integer x : memberServicesMap.get(memberID))
        {
            ServiceRecord temp = servicesList.get(x);
            if(inDateRange(temp.getDateOfService(),startDate,endDate))
            {
                tempSet.add(temp);
            }
        }

        return tempSet;
    }

   /* //Return all ServiceRecords from a given date range
    public Set<ServiceRecord> getServiceRecordsforDateRange(Calendar start, Calendar end){
        HashSet<ServiceRecord> returnSet = new HashSet();
        for(ServiceRecord record : servicesList){
         if(inDateRange(record.getDateOfService(),start,end)){
             returnSet.add(record);
         }
        }

        return returnSet;
    }*/
    private Boolean inDateRange(Calendar check, Calendar start, Calendar end)
    {
        if(check.after(start) && check.before(end)){
            return true;
        }

        return false;
    }


    private  void createMockServiceRecords()
    {
        this.memberServicesMap = new HashMap<Integer, Set<Integer>>();
        this.providerServicesMap = new HashMap<Integer, Set<Integer>>();
        this.servicesList = new ArrayList<ServiceRecord>();

        // Member ID's
        int[] memberIDs = new int[]{333222333,333222334,333222335,333222336, 333222337, 333222338,
                333222339, 333222340, 333222341, 333222342};
        // Service ID's
        int[] servcieIDs = new int[]{598385, 598470, 883948, 873546, 582395, 808341 ,539136, 773522,
                694322, 867530};
        // Provider ID's
        int[] providerIDs = new int[]{111222333, 111222444,111222555,111222666,111222666, 111222777,
            111222888, 111222999, 111333000, 111333111};

        for(int x = 0; x < 10; x++)
        {
            Calendar cal1 = new GregorianCalendar();
            cal1.add(Calendar.DAY_OF_MONTH, -x);
            Calendar cal2 = new GregorianCalendar();
            cal2.add(Calendar.DAY_OF_MONTH, - (x +2));
           /* Calendar cal1 = new GregorianCalendar(2013, 11, x + 11, 9, x, x+30);
            Calendar cal2 = new GregorianCalendar(2013, 11, x + 10);*/

            loadServiceRecordToMemory(new ServiceRecord(providerIDs[x], memberIDs[x],servcieIDs[x], "Mock data " + x, cal1, cal2));
        }

        return;
    }

    public boolean serviceRecordFileOpen()
    {
        return serviceRecordFile != null ? true : false;
    }
}
