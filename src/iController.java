import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/23/13
 * Time: 7:49 PM
 * To change this template use File | Settings | File Templates.
 */
public interface iController
{
    void open(String file);
    void close();
    Set<iPerson> read();
    void save();
    iPerson find(int id);
    void add(iPerson toAdd);
    void remove(int id);
}
