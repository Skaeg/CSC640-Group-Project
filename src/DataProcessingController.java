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
        iPerson  loggedIn  = null;
        char menuChoice = ' ';
        ArrayList<String> mainMenuItems = getMainMenuItems();

        while(running)
        {
            loggedIn = loginProvider();
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
                            providerController.logOutProvider();
                            loggedIn = null;
                            break;
                        case 'Q': // Exit the application
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

    private static iPerson loginProvider()
    {
        iPerson loggedinProvider = null;
        String message = "";
        int failures = 0;
        while(message != ProviderController.VALID && failures++ < MAX_NUMBER_OF_LOGIN_ATTEMPTS)
        {
            Integer providerNumber = Integer.parseInt(terminal.getInput("Enter provider number:"));
            message = providerController.tryLogInProvider(providerNumber);
            terminal.sendOutput(message);
        }
        if(failures >= MAX_NUMBER_OF_LOGIN_ATTEMPTS)
        {
            terminal.sendOutput("Number of allowed logon attempts exceeded!");
        }
        else
        {
            loggedinProvider = providerController.getLoggedInProvider();
        }
        return loggedinProvider;
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
        Boolean initializationSuccessful = false;

        return initializationSuccessful;
    }

    private static boolean initializeServiceDirectory()
    {
        Boolean initializationSuccessful = false;

        return initializationSuccessful;
    }

    private static ArrayList<String> getMainMenuItems()
    {
        ArrayList<String> menuItems = new ArrayList<String>();
        menuItems.add("{E}nter Member Number");
        menuItems.add("{Q}uit");
        return menuItems;
    }

    private static void displayMenu(ArrayList<String> menuItems)
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
