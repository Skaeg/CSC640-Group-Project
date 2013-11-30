import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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

    static Calendar nextWeeklyReportDate;
    static Calendar lastBillingDate;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    static Timer weeklyReportTimer = new Timer();
    //expressed in minutes
    private final static long fONCE_PER_DAY = 60*24;
    private final static long fONCE_PER_WEEK = fONCE_PER_DAY * 7;

    public static void mainMenu()
    {
        boolean running = initializeSystem();
        iEmployee loggedIn  = null;
        char menuChoice = ' ';

        // TODO: adjust this to give us the correct billing statements for the test cases
        lastBillingDate = new GregorianCalendar();
        lastBillingDate.add(Calendar.DAY_OF_MONTH, -10);
      //  lastBillingDate.set(Calendar.DAY_OF_MONTH, 10);
      //  lastBillingDate.set(Calendar.MONTH, 11);

        DataProcessingController dataProcessingController = new DataProcessingController();
        dataProcessingController.scheduleWeeklyReportGeneration();

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
                terminal.sendOutput("Test employee Number: 111222333");
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
            ManagerSummaryReport summaryReport = new ManagerSummaryReport();
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
                    summaryReport.addSummaryEntry(provider.getName(), providerReport.getConsultations(), providerReport.getFeeTotal());
                }
            }
            eftReport.executeReport();
            reports.put("EFT_Report", eftReport);
            summaryReport.executeReport();
            reports.put("Summary_Report",summaryReport);

            /*
            execute other reports here
            then add to reports hashmap
            */

            //Create memberReports for for the given time periord
            for(Integer memberID : memberController.getAllMembers().keySet())
            {
                Set<ServiceRecord> serviceRecords = serviceRecordController.getListOfServiceRecordsByMember(memberID, lastBillingDate, today);
                Member member = memberController.getMember(memberID);

                if(serviceRecords != null && !serviceRecords.isEmpty() && member != null)
                {
                    MemberReport memberReport = new MemberReport(serviceDirectory,member,serviceRecords);
                    reports.put(member.getName(), memberReport);
                }
            }



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
        return String.format("%02d-%02d-%4d", date.get(Calendar.MONTH) + 1, date.get(Calendar.DATE), date.get(Calendar.YEAR));
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
        //to do: ***********************
        //change return value to boolean, set it initally to false, then after successful change set to true.
       //check after every loop iteration if it went ok


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

    // based on code from this website: http://programmingexamples.wikidot.com/sheduledexecutorservice
    public void scheduleWeeklyReportGeneration()
    {
        final Runnable weeklyReportGeneration = new Runnable()
        {
            public void run()
            {
                generateWeeklyReports();
            }
        };
        long initialDelay = 0;

        nextWeeklyReportDate = (Calendar)lastBillingDate.clone();
        while(nextWeeklyReportDate.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY)
        {
            nextWeeklyReportDate.add(Calendar.DAY_OF_MONTH, 1);
            initialDelay++;
        }
        // get number of days expressed in minutes
        initialDelay *= fONCE_PER_DAY;
        // find out how many hours till Midnight expressed in minutes, only check to 23 instead of 24, otherwise
        // the time would go over, for example if it is 1:12 pm, the difference in hours would be 11 hours, but
        // adding 11 hours to the current time would end up at 12:12 am.
        initialDelay += (23 - nextWeeklyReportDate.get(Calendar.HOUR_OF_DAY)) * 60;
        initialDelay += 60 - nextWeeklyReportDate.get(Calendar.MINUTE);

        long period = fONCE_PER_WEEK;
        TimeUnit units = TimeUnit.MINUTES;
        final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(weeklyReportGeneration, initialDelay, period, units);
        // tested with the following code to guarantee that the schedule would work
        //final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(weeklyReportGeneration, 1, 5, TimeUnit.SECONDS);

        scheduler.schedule(new Runnable() { public void run() { beeperHandle.cancel(true); } }, 60 * 60, TimeUnit.SECONDS);
    }
}