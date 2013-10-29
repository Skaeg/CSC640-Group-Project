/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/13/13
 * Time: 10:54 AM
 * To change this template use File | Settings | File Templates.
 */
public interface iPerson
{
    String getName();
    void setName(String first, String last);
    String getStreetAddress();
    void setStreetAddress(String streetAddress);
    String getCity();
    void setCity(String city);
    String getState();
    void setState(String state);
    int getZipcode();
    void setZipcode(int zipcode);
    int getIdentifier();
    void setIdentifier(int id);
}
