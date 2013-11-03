import java.util.ArrayList;
import java.util.List;

public class DataProcessingController implements iRequestReport
{
    static iTerminal terminal  = new TerminalInputOutput(); // TODO: replace with factory?
    static final int MAX_NUMBER_OF_LOGIN_ATTEMPTS = 3;
    static final String  PROVIDER_CONTROLLER_FILE = "providers.xml";
    static final String  MEMBER_CONTROLLER_FILE = "members.xml";
    static final String  ADMINISTRATOR_CONTROLLER_FILE = "administrators.xml";
    static final String  SERVICE_DIRECTORY_FILE = "serviceDirectory.xml";
    static ProviderController providerController;
    static MemberController memberController;
    //static AdministratorController administratorController;
    static ServiceDirectory serviceDirectory;

    public static void mainMenu()
    {
        boolean running = initializeSystem();
        iEmployee loggedIn  = null;
        char menuChoice = ' ';

        while(running)
        {
            iLogged[] loggedControllerArray = new iLogged[1];
            // using the loggedControllerArray to act like an out statement in C#
            loggedIn = loginEmployee(loggedControllerArray);
            iLogged loggedController = loggedControllerArray[0];
            List<String> mainMenuItems = loggedIn.getMenuItems();
            while(loggedIn != null)
            {
                try
                {
                    displayMenu(mainMenuItems);
                    menuChoice = terminal.getInput("Enter Menu Option").toUpperCase().charAt(0);

                    switch (menuChoice)
                    {
                        case 'E' : //Enter Member Number
                            String memberNumber = terminal.getInput("Enter member Number:");
    /*                        if(mem.containsKey(memberNumber))
                            {

                            }
                            else
                            {
                                terminal.sendOutput(String.format("%s is an invalid member number.", memberNumber));
                            }
    */                        break;
                        case 'L': // logout provider
                            loggedController.logout();
                            loggedIn = null;
                            break;
                        case 'Q': // Exit the application
                            loggedController.logout();
                            loggedIn = null;
                            running = false;
                            break;
                        default:
                            terminal.sendOutput(String.format("%c is not a valid menu choice. ", menuChoice));
                            break;
                    }
                }
                catch (Exception ex)
                {
                    // TODO: log exception here and display error message
                    terminal.sendOutput(ex.getMessage());
                }
            }
        }
        handleExit();
    }

    // using the array in a similar fashion to a C# out statement
    private static iEmployee loginEmployee(iLogged[] loggedController)
    {
        iEmployee loggedin = null;
        String message = "";
        int failures = 0;

        while(failures++ < MAX_NUMBER_OF_LOGIN_ATTEMPTS)
        {
            Integer providerNumber = Integer.parseInt(terminal.getInput("Enter employeee id number:"));
            if(providerController.tryLogIn(providerNumber) != ProviderController.VALID)
            {
                loggedController[0] = providerController;
                break;
            }
/*            else if(administratorController.tryLogIn(providerNumber) != ProviderController.VALID)
            {
                loggedController[0] = administratorController;
                break;
            }
*/
            terminal.sendOutput(message);
        }


        if(failures >= MAX_NUMBER_OF_LOGIN_ATTEMPTS)
        {
            terminal.sendOutput("Number of allowed logon attempts exceeded!");
            handleExit();
        }
        else
        {
            loggedin = loggedController[0].getLoggedIn();
        }
        return loggedin;
    }

    private static boolean initializeSystem()
    {
        return initializeMembers() & initializeAdministrators() & initializeServiceDirectory() & initializeProviders();
    }

    private static boolean initializeMembers()
    {
        Boolean membersInitialized = false;
        memberController = new MemberController(MEMBER_CONTROLLER_FILE);
        return memberController.memberFileOpen();
    }

    private static boolean initializeProviders()
    {
        providerController = new ProviderController(PROVIDER_CONTROLLER_FILE, memberController, serviceDirectory);
        return providerController.providerFileOpen();
    }

    private static boolean initializeAdministrators()
    {
        Boolean initializationSuccessful = true;

        return initializationSuccessful;
    }

    private static boolean initializeServiceDirectory()
    {
        Boolean initializationSuccessful = false;
        serviceDirectory = new ServiceDirectory(SERVICE_DIRECTORY_FILE);
        return serviceDirectory.serviceDirectoryFileOpen();
    }

    private static void displayMenu(List<String> menuItems)
    {
        List<String> responses = new ArrayList<String>();
        for(String prompt : menuItems)
        {
            terminal.sendOutput(prompt);
        }
    }

    static void handleExit()
    {
        System.exit(0);
    }

    @Override
    public void requestReport(int reportType)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
