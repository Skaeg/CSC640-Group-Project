import java.util.ArrayList;
import java.util.List;

public class DataProcessingController implements iRequestReport
{
    static iTerminal terminal  = new TerminalInputOutput(); // TODO: replace with factory?
    static final int MAX_NUMBER_OF_LOGIN_ATTEMPTS = 3;
    static final String PROVIDER_CONTROLLER_FILE = "..\\Providers\\providers.xml";
    static ProviderController providerController;

    public static void mainMenu()
    {
        initialize();

        iPerson  loggedIn = null;
        char menuChoice = ' ';
        boolean running = true;
        ArrayList<String> mainMenuItems = getMainMenuItems();

        while(running)
        {
            try
            {
                displayMenu(mainMenuItems);
                menuChoice = terminal.getInput("Enter Menu Option").toUpperCase().charAt(0);

                switch (menuChoice)
                {
                    case 'E' : //Enter Member Number
                        String memberNumber = terminal.getInput("Enter member Number:");
                        if(members.containsKey(memberNumber))
                        {

                        }
                        else
                        {
                            terminal.sendOutput(String.format("%s is an invalid member number.", memberNumber));
                        }
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
        handleExit();
    }

    private static boolean initialize()
    {
        boolean loginSuccessful = false;
        String message = "";
        providerController = new ProviderController(PROVIDER_CONTROLLER_FILE);
        int failures = 0;
        while(message != ProviderController.VALID && failures < MAX_NUMBER_OF_LOGIN_ATTEMPTS)
        {
            Integer providerNumber = Integer.parseInt(terminal.getInput("Enter provider number:"));
            message = providerController.tryLogInProvider(providerNumber);
            terminal.sendOutput(message);
        }
        if(failures == MAX_NUMBER_OF_LOGIN_ATTEMPTS)
        {
            terminal.sendOutput("Number of allowed logon attempts exceeded!");
        }
        else
        {
            loginSuccessful = true;
        }
        return loginSuccessful;
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
