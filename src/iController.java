/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/23/13
 * Time: 7:49 PM
 * To change this template use File | Settings | File Templates.
 */
public interface iController
{
    Boolean open(String file);
    Boolean save(String file);
    iPerson find(int id);
    Boolean add(iPerson toAdd);
    Boolean remove(int id);

}
