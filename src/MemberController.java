import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/23/13
 * Time: 7:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class MemberController implements iController
{

    public String getMemberStatus(int id)
    {
        return "remember to fill out this method";
    }


   //******CAUTION!!!  I simply plopped this in, based on what the ProviderController had to populate the
   //providers list.  If it is a mess, at least you can peel out the data and use it.  I DID NOT RUN IT. Not sure if things are set or not.
   //-Susan
    ArrayList<Member> populateMembersList()
    {
        ArrayList<Member> membersList = new ArrayList<Member>();

        membersList.add(new Member("Linda", "Schaefer", "4103 N 62nd St", "Milwaukee", "WI", 53213, 333222333));
        membersList.add(new Member("Matt", "Pagels", "733 S. 88th St", "West Allis", "WI", 53214, 333222444));
        membersList.add(new Member("Tony", "Andersen", "5710 W Range Ave", "Brown Deer", "WI", 53209, 333222555));
        membersList.add(new Member("Lisa", "Thomas", "8126 W Kiehnau Ave", "Milwaukee", "WI", 53223, 333222666));
        membersList.add(new Member("Anna", "Bauer", "9347 N Fairy Chasm Cir", "Brown Deer", "WI", 53209, 333222777));
        membersList.add(new Member("Mike", "Rogers", "4040 S Moorland Rd", "New Berlin", "WI", 53151, 333222888));
        membersList.add(new Member("Rik", "Gole", "1325 Chester Ct", "Brookfield", "WI", 53045, 333222999));
        membersList.add(new Member("Mandy", "Owens", "3367 S 122nd St", "West Allis", "WI", 53214, 333444000));
        membersList.add(new Member("Stephanie", "Frank", "15000 Vera Cruz Dr", "New Berlin", "WI", 53151, 333444111));
        membersList.add(new Member("Jack", "Black", "3903 S. First St", "Milwaukee", "WI", 53207, 333444222));

        return membersList;
    }


}
