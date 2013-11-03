/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 11/3/13
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */
public interface iLogged
{
    String tryLogIn(int id);
    iEmployee getLoggedIn();
    void logout();
}
