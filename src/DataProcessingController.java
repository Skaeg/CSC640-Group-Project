import java.util.*;

public class DataProcessingController implements iRequestReport
{
    static iTerminal terminal  = new TerminalInputOutput(); // TODO: replace with factory?
    static final int MAX_NUMBER_OF_LOGIN_ATTEMPTS = 3;
    static final String  PROVIDER_CONTROLLER_FILE = "providers.xml";
    static final String  MEMBER_CONTROLLER_FILE = "members.xml";
    static final String  ADMINISTRATOR_CONTROLLER_FILE = "administrators.xml";
    static final String  SERVICE_DIRECTORY_FILE = "serviceDirectory.xml";
    static final String  SERVICE_RECORDS_FILE = "serviceRecords.xml";

    static ProviderController providerController;
    static MemberController memberController;
    static AdministratorController administratorController;
    static ServiceDirectory serviceDirectory;
    static ServiceRecordController serviceRecordController;

    static Calendar lastBillingDate;

    public static void mainMenu()
    {
        boolean running = initializeSystem();
        iEmployee loggedIn  = null;
        char menuChoice = ' ';

        // TODO: adjust this to give us the correct billing statements for the test cases
        lastBillingDate = GregorianCalendar.getInstance();
        lastBillingDate.set(Calendar.DAY_OF_MONTH, 10);
        lastBillingDate.set(Calendar.MONTH, 11);

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
                        case 'I': // Interactive Mode for Administrator
                            //admin interactive mode is where member & provider info can be edited
                            terminal.sendOutput("Interactive Mode for Administrator will display here");
                            administratorInteractiveMode();
                            break;
                        case 'R': // Reports for Administrator
                            //admin can run reports
                            terminal.sendOutput("Report choices for Administrator will display here");
                            break;
                        case 'L': // logout provider
                            loggedController.logout();
                            loggedIn = null;
                            break;
                        case 'T':
                            generateWeeklyReports();
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
            Integer providerNumber = 0;
            try
            {
                providerNumber = Integer.parseInt(terminal.getInput("Enter employee id number:"));
            }
            catch(Exception ex)
            {
                terminal.sendOutput(ex.getMessage());
            }
            if(providerController.tryLogIn(providerNumber) == ProviderController.VALID)
            {
                loggedController[0] = providerController;
                message = "Valid";
                break;
            }
            else if(administratorController.tryLogIn(providerNumber) == ProviderController.VALID)
            {
                loggedController[0] = administratorController;
                message = "Valid";
                break;
            }

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
        return initializeMembers() & initializeAdministrators() & initializeServiceDirectory() & initializeProviders() & initializeServiceRecords();
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
        Boolean administratorsInitialized = false;
        administratorController = new AdministratorController(ADMINISTRATOR_CONTROLLER_FILE);
        return administratorController.administratorFileOpen();
    }

    private static boolean initializeServiceDirectory()
    {
        Boolean initializationSuccessful = false;
        serviceDirectory = new ServiceDirectory(SERVICE_DIRECTORY_FILE);
        return serviceDirectory.serviceDirectoryFileOpen();
    }

    private static boolean initializeServiceRecords()
    {
        Boolean initializationSuccessful = false;
        serviceRecordController = new ServiceRecordController(SERVICE_RECORDS_FILE);
        return serviceRecordController.serviceRecordFileOpen();
    }

    private static void displayMenu(List<String> menuItems)
    {
        List<String> responses = new ArrayList<String>();
        for(String prompt : menuItems)
        {
            terminal.sendOutput(prompt);
        }
    }

    private static void generateWeeklyReports()
    {
        try
        {
            EFTReport eftReport = new EFTReport();
            HashMap<String, iReport> reports = new HashMap<String, iReport>();
            Calendar today = GregorianCalendar.getInstance();
            today.set(Calendar.MONTH, 11);
            for(Integer providerID : providerController.getProviders().keySet())
            {
                Set<ServiceRecord> serviceRecords = serviceRecordController.getListOfServiceRecordsByProvider(providerID, lastBillingDate, today);
                Provider provider = providerController.getProvider(providerID);

                if(serviceRecords != null && !serviceRecords.isEmpty() && provider != null)
                {
                    ProviderReport providerReport = new ProviderReport();
                    providerReport.executeReport(provider, serviceRecords, memberController, serviceDirectory);
                    reports.put(provider.getName(), providerReport);
                    eftReport.addEFTEntry(provider.getName(), provider.getIdentifier(), providerReport.getFeeTotal());
                }
            }
            eftReport.executeReport();
            reports.put("EFT_Report", eftReport);
            /*
            execute other reports here
            then add to reports hashmap
            */
            for(Map.Entry<String, iReport> report : reports.entrySet())
            {
                report.getValue().sendReport(String.format("%s_%s.txt", report.getKey(), getDateFromCalendar(GregorianCalendar.getInstance())));
            }

            // weekly report run, update the last billing date
            lastBillingDate = GregorianCalendar.getInstance(); // TODO: save to file?
        }
        catch (Exception ex)
        {
            terminal.sendOutput(ex.getMessage());
        }
    }

    static void handleExit()
    {
        System.exit(0);
    }

    private static String getDateFromCalendar(Calendar date)
    {
        return String.format("%2d-%2d-%4d", date.get(Calendar.MONTH), date.get(Calendar.DATE), date.get(Calendar.YEAR));
    }

    @Override
    public void requestReport(int reportType)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private static void administratorInteractiveMode(){
        //this is the administrator's submenu
        boolean inInteractiveMode = true;
        char menuChoice = ' ';

        while(inInteractiveMode == true)
        {
            try
            {
                terminal.sendOutput("Interactive Mode");
                terminal.sendOutput("1) Add Member");
                terminal.sendOutput("2) Edit Member");
                terminal.sendOutput("3) Delete Member");
                terminal.sendOutput("4) Add Provider");
                terminal.sendOutput("5) Edit Provider");
                terminal.sendOutput("6) Delete Provider");
                terminal.sendOutput("0) Return to Main Menu");
                menuChoice = terminal.getInput("Enter Menu Option").toUpperCase().charAt(0);

                switch (menuChoice)
                {
                    case '1': // Add Member
                        terminal.sendOutput("Add Member for Administrator will display here");
                        break;
                    case '2': // Edit Member
                        terminal.sendOutput("Edit Member for Administrator will display here");
                        editMember();
                        break;
                    case '3': // Delete Member
                        terminal.sendOutput("Delete Member for Administrator will display here");
                        break;
                    case '4': // Add Provider
                        terminal.sendOutput("Add Provider for Administrator will display here");
                        break;
                    case '5': // Edit Provider
                        terminal.sendOutput("Edit Provider for Administrator will display here");
                        break;
                    case '6': // Delete Provider
                        terminal.sendOutput("Delete Provider for Administrator will display here");
                        break;
                    case '0': // exit to previous menu
                        inInteractiveMode = false;
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

    private static void editMember(){
        //NOTE:  THIS ONLY WORKS partly at this point - you can enter a member number, and it will get to the menu that will let you
        //select which part you want to edit, combined with displaying the member's info.

        //the administrator can edit member info
        boolean editingMember = true;
        char menuChoice = ' ';
        boolean loopThis = true;
        Integer memberNumber = 0;
        Member memberToEdit;

        while(loopThis == true)
        {
            try
            {
                terminal.sendOutput("Edit Member");
                memberNumber = Integer.parseInt(terminal.getInput("Enter member number, or 0 to return to previous menu:"));
            }
            catch(Exception ex)
            {
                terminal.sendOutput(ex.getMessage());
            }

            if (memberController.getMember(memberNumber)!= null) {
                try
                {
                    memberToEdit = memberController.getMember(memberNumber);
                    terminal.sendOutput("Edit:");
                    terminal.sendOutput("1) Member Number: " + memberToEdit.getIdentifier());
                    terminal.sendOutput("2) Name: " + memberToEdit.getName());
                    terminal.sendOutput("3) Address: " + memberToEdit.getStreetAddress());
                    terminal.sendOutput("4) City: " + memberToEdit.getCity());
                    terminal.sendOutput("5) State: " + memberToEdit.getState());
                    terminal.sendOutput("6) Zip: " + memberToEdit.getZipcode());
                    terminal.sendOutput("0) Exit to previous menu");
                    menuChoice = terminal.getInput("Enter Menu Option").toUpperCase().charAt(0);

                    String newData = "";
                    switch (menuChoice)
                    {

                        //*** NOTE TO GUYS:  IS THIS OK OR DO I NEED TO DO A TRY/CATCH FOR EACH ENTRY?
                        //***ALSO, is it ok to do here or should i pull it out somewhere else?
                        case '1': // edit member number
                            //memberToEdit.setIdentifier(Integer.parseInt(terminal.getInput("Enter new member number:")));
                            terminal.sendOutput("NOT SURE IF WE SHOULD ALLOW THE MEMBER NUMBER TO CHANGE?  THOUGHTS?");
                            //also need to change identifier in the hashmap!
                            break;
                        case '2': // Edit member name
                            memberToEdit.setName(terminal.getInput("Enter new name:"));
                            break;
                        case '3': // edit member address
                            memberToEdit.setStreetAddress(terminal.getInput("Enter new street address:"));
                            break;
                        case '4': // edit member city
                            memberToEdit.setCity(terminal.getInput("Enter new city:"));
                            break;
                        case '5': // edit member state
                            memberToEdit.setState(terminal.getInput("Enter new state:"));
                            break;
                        case '6': // edit member zip
                            memberToEdit.setZipcode(Integer.parseInt(terminal.getInput("Enter new zipcode:")));
                            break;
                        case '0': // exit to previous menu
                            loopThis = false;
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
            else if (memberNumber == 0){
                loopThis = false;
            }
            else{
                terminal.sendOutput("No member of that number was found");
            }



        }

    }


}