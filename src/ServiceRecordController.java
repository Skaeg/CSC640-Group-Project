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
    private ArrayList<ServiceRecord> servicesList;
    // HashMap<MemberID,Set<indexID>
    private HashMap<Integer, Set<Integer>> memberServicesMap;
    // HashMap<ProviderID,Set<indexID>
    private HashMap<Integer, Set<Integer>> providerServicesMap;

    private File serviceRecordFile;
    private String fileName;

    public ServiceRecordController(String file) {

        // Open File and load ArrayList
        this.memberServicesMap = new HashMap<Integer, Set<Integer>>();
        this.providerServicesMap = new HashMap<Integer, Set<Integer>>();
        this.servicesList = new ArrayList<ServiceRecord>();
        this.fileName = file;
        open(this.fileName);
    }

    private boolean open(String file) {
        try {
            serviceRecordFile = new File(file);
            // if the provider file does not exist, load our canned one.
            if (!serviceRecordFile.exists()) {
             saveToFile(file,createMockServiceRecords());
            }

                FileInputStream fis = new FileInputStream(serviceRecordFile);
                XMLDecoder decoder = new XMLDecoder(fis);
                ArrayList<ServiceRecord> tempServiceList = (ArrayList<ServiceRecord>) decoder.readObject();

                decoder.close();

                // Load records into the array and hashmaps
                for (ServiceRecord record : tempServiceList) {
                    loadServiceRecordToMemory(record);
                }

        } catch (Exception e) {
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

    private boolean loadServiceRecordToMemory(ServiceRecord serviceRecord) {
        int newServiceIndex = servicesList.size();

        try {
            servicesList.add(serviceRecord);

            // Add ServiceRecord index to the list of services used by a member
            if (!memberServicesMap.containsKey(serviceRecord.getMemberID())) {
                memberServicesMap.put(serviceRecord.getMemberID(), new HashSet<Integer>());
            }
            memberServicesMap.get(serviceRecord.getMemberID()).add(newServiceIndex);

            // Add ServiceRecord index to a list of services provided by a provider
            if (!providerServicesMap.containsKey(serviceRecord.getProviderID())) {
                providerServicesMap.put(serviceRecord.getProviderID(), new HashSet<Integer>());
            }
            providerServicesMap.get(serviceRecord.getProviderID()).add(newServiceIndex);

            return true;

        } catch (Exception e) {
            return false;
        }
    }


    //Returns a Set of ServiceRecords for a given provider
    public Set<ServiceRecord> getListOfServiceRecordsByProvider(int providerID) {

        if (!providerServicesMap.containsKey(providerID) || providerServicesMap.get(providerID).isEmpty()) {
            return null;
        }

        Set<ServiceRecord> tempSet = new HashSet<ServiceRecord>();

        for (Integer x : providerServicesMap.get(providerID)) {
            tempSet.add(servicesList.get(x));
        }

        return tempSet;
    }

    //Returns a Set of ServiceRecords for a given memberID
    public Set<ServiceRecord> getListOfServiceRecordsByMember(int memberID) {
        if (!memberServicesMap.containsKey(memberID) || memberServicesMap.get(memberID).isEmpty()) {
            return null;
        }

        Set<ServiceRecord> tempSet = new HashSet<ServiceRecord>();

        for (Integer x : memberServicesMap.get(memberID)) {
            tempSet.add(servicesList.get(x));
        }

        return tempSet;
    }

    private  ArrayList<ServiceRecord> createMockServiceRecords(){
        ArrayList<ServiceRecord> mockServiceRecords = new  ArrayList<ServiceRecord>();
     /*   (int _providerID, int _memberID, int _serviceCode, String _comments,
                Calendar _dateAndTimeServiceEntered, Calendar _dateOfService) */
        Calendar cal1 = new GregorianCalendar(2013,10,30,9,30);
        Calendar cal2 = new GregorianCalendar(2013,10,25);

        for(int x = 0; x < 20; x++){
         mockServiceRecords.add(new ServiceRecord(x, x+100, x + 1000, "Comment " + x, cal1, cal2));
        }

        return mockServiceRecords;
    }
}
