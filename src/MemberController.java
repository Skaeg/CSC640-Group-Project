import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/23/13
 * Time: 7:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class MemberController implements iController
{
    private HashMap<Integer, Member> members;
    public File memberFile;
    public static String SUSPENDED = "Suspended";

    public MemberController (String file)
    {
            open(file);
    }

    public boolean getMemberStatus(int id)
    {
      return members.get(id) != null? members.get(id).isStatusValid():false;
    }

    public boolean memberFileOpen(){
        return memberFile != null? true:false;
    }

    @Override
    public Boolean open(String file)
    {
        try
        {
            memberFile = new File(file);
            // if the provider file does not exist, load our canned one.
            if(!memberFile.exists())
            {
                members = populateMembersList();
                save(memberFile.getName());
            }
            else
            {
                FileInputStream fis = new FileInputStream(memberFile);
                XMLDecoder decoder = new XMLDecoder(fis);
                members = (HashMap<Integer, Member>)decoder.readObject();
                decoder.close();
            }
        }
        catch(Exception e)
        {
            System.out.println("Exception during deserialization: " +  e);
         //   System.exit(0);
            return false;
        }
        return true;
    }

    @Override
    public Boolean save(String file) {

        try
        {
            if(!memberFile.exists())
            {
                memberFile.createNewFile();
            }
            //String fullPath = file.getAbsolutePath();
            FileOutputStream os = new FileOutputStream(memberFile);
            XMLEncoder encoder = new XMLEncoder(os);
            encoder.writeObject(members);
            encoder.close();
        }
        catch (Exception ex)
        {
            System.out.println("Exception during serialization: " +  ex);
           // System.exit(0);
            return false;
        }
        return true;
    }

    public HashMap<Integer, Member> getAllMembers()
    {          Set<iPerson> allMembers = new HashSet<iPerson>();
        return  members;
    }

    @Override
    public iPerson find(int id)
    {
        return members.get(id);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean add(iPerson toAdd)
    {
        // Do we loss anything by type casting to a member?
        members.put(toAdd.getIdentifier(), (Member) toAdd);
        return save(memberFile.getName());
    }

    @Override
    public Boolean remove(int id)
    {
        if(members.remove(id) != null){
           return save(memberFile.getName());
        }
        else{
            return false;
        }
    }

    private HashMap<Integer, Member> populateMembersList()
    {
        HashMap<Integer,Member> testMembers = new HashMap<Integer,Member>();

        testMembers.put(333222333, new Member("Linda Schaefer", "4103 N 62nd St", "Milwaukee", "WI", 53213, 333222333, 0,true));
        testMembers.put(333222334, new Member("Matt Pagels", "733 S. 88th St", "West Allis", "WI", 53214, 333222334, -56.85,true));
        testMembers.put(333222335, new Member("Tony Andersen", "5710 W Range Ave", "Brown Deer", "WI", 53209, 333222335, -199.09,false));
        testMembers.put(333222336, new Member("Lisa Thomas", "8126 W Kiehnau Ave", "Milwaukee", "WI", 53223, 333222336, 2,true));
        testMembers.put(333222337, new Member("Anna Bauer", "9347 N Fairy Chasm Cir", "Brown Deer", "WI", 53209, 333222337, .09,true));
        testMembers.put(333222338, new Member("Mike Rogers", "4040 S Moorland Rd", "New Berlin", "WI", 53151, 333222338, -.07,false));
        testMembers.put(333222339, new Member("Rik Gole", "1325 Chester Ct", "Brookfield", "WI", 53045, 333222339, -12.34,true));
        testMembers.put(333222340, new Member("Mandy Owens", "3367 S 122nd St", "West Allis", "WI", 53214, 333222340, -765.00,true));
        testMembers.put(333222341, new Member("Stephanie Frank", "15000 Vera Cruz Dr", "New Berlin", "WI", 53151, 333222341, -98765.43,false));
        testMembers.put(333222342, new Member("Jack Black", "3903 S. First St", "Milwaukee", "WI", 53207, 333222342, -12.06,true));

        return testMembers;
    }

    public Member getMember(int memberID)
    {
        return members.get(memberID);
    }
}
