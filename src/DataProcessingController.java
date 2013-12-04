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
                            administratorInteractiveMode();
                            break;
                        case 'R': // Reports for Administrator
                            //admin can run reports
                            administratorRunReports();
                            break;
                        case 'L': // logout provider
                            loggedController.logout();
                            loggedIn = null;
                            break;
                        case 'T':
                            generateWeeklyReports(-1,true, true, true, true);
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
                terminal.sendOutput("Test provider Number: 111222333");
                terminal.sendOutput("Test administrator Number: 555444111");
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

    //the parameters in this one control what reports will run.
    private static void generateWeeklyReports(int pastDays, boolean runMember, boolean runProvider, boolean runEFT, boolean runSummary)
    {
        try
        {
            EFTReport eftReport = new EFTReport();
            ManagerSummaryReport summaryReport = new ManagerSummaryReport();
            HashMap<String, iReport> reports = new HashMap<String, iReport>();
            Calendar today = GregorianCalendar.getInstance();
            Calendar startOfReportRequest =  (Calendar)today.clone();
//  ********* Not needed because all mock data is generated based on the current date. Also months are 0 --> 11
//            today.set(Calendar.MONTH, 11);

            if(pastDays < 0){
                startOfReportRequest = lastBillingDate;
            }else {
                startOfReportRequest.add(Calendar.DAY_OF_MONTH,(pastDays *= -1));
            }

           for(Integer providerID : providerController.getProviders().keySet())
            {
                Set<ServiceRecord> serviceRecords = serviceRecordController.getListOfServiceRecordsByProvider(providerID, startOfReportRequest, today);
                Provider provider = providerController.getProvider(providerID);

                if(serviceRecords != null && !serviceRecords.isEmpty() && provider != null)
                {
                    ProviderReport providerReport = new ProviderReport();
                    providerReport.executeReport(provider, serviceRecords, memberController, serviceDirectory);
                    if (runProvider)
                    {
                        reports.put(provider.getName(), providerReport);
                    }
                    if (runEFT)
                    {
                        eftReport.addEFTEntry(provider.getName(), provider.getIdentifier(), providerReport.getFeeTotal());
                    }
                    if (runSummary)
                    {
                        summaryReport.addSummaryEntry(provider.getName(), providerReport.getConsultations(), providerReport.getFeeTotal());
                    }
                }
            }
            if (runEFT)
            {
                eftReport.executeReport();
                reports.put("EFT_Report", eftReport);
            }
            if (runSummary)
            {
                summaryReport.executeReport();
                reports.put("Summary_Report",summaryReport);
            }
            /*
            execute other reports here
            then add to reports hashmap
            */

            if (runMember)
            {
            //Create memberReports for for the given time periord
                for(Integer memberID : memberController.getAllMembers().keySet())
                {
                    Set<ServiceRecord> serviceRecords = serviceRecordController.getListOfServiceRecordsByMember(memberID, startOfReportRequest, today);
                    Member member = memberController.getMember(memberID);

                    if(serviceRecords != null && !serviceRecords.isEmpty() && member != null)
                    {
                        MemberReport memberReport = new MemberReport(serviceDirectory,member,serviceRecords,providerController.getProviders());
                        reports.put(member.getName(), memberReport);
                    }
                }
            }


            for(Map.Entry<String, iReport> report : reports.entrySet())
            {
                report.getValue().sendReport(String.format("%s_%s.txt", report.getKey(), getDateFromCalendar(GregorianCalendar.getInstance())));
            }

            // if weekly report run, update the last billing date
            if(pastDays < 0){
            lastBillingDate = GregorianCalendar.getInstance(); // TODO: save to file?
            }
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

    private static void administratorInteractiveMode()
    {
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
                        addMember();
                        break;
                    case '2': // Edit Member
                        editMember();
                        break;
                    case '3': // Delete Member
                        removeMember();
                        break;
                    case '4': // Add Provider
                        addProvider();
                        break;
                    case '5': // Edit Provider
                        editProvider();
                        break;
                    case '6': // Delete Provider
                        removeProvider();
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

    private static void administratorRunReports()
    {
        //this is the administrator's submenu
        boolean inRunReports = true;
        char menuChoice = ' ';
        int daysToRun = -1;

        while(inRunReports == true)
        {
            try
            {
                terminal.sendOutput("Run Reports");
                terminal.sendOutput("1) Run all Reports");
                terminal.sendOutput("2) Member Reports");
                terminal.sendOutput("3) Provider Reports");
                terminal.sendOutput("4) EFT Report");
                terminal.sendOutput("5) Summary Report");
                terminal.sendOutput("0) Return to Main Menu");
                menuChoice = terminal.getInput("Enter Menu Option").toUpperCase().charAt(0);
               if(menuChoice != '0'){
                   daysToRun = Integer.valueOf(terminal.getInput("Enter the number of days (proir to today) you would like the reports from"));
               }

                switch (menuChoice)
                {
                    case '1': // Run All reports
                        //runMember, runProvider, runEFT, runSummary
                        generateWeeklyReports(daysToRun,true, true, true, true);
                        break;
                    case '2': // member reports
                        //runMember, runProvider, runEFT, runSummary
                        generateWeeklyReports(daysToRun,true, false, false, false);
                        break;
                    case '3': // provider reports
                        //runMember, runProvider, runEFT, runSummary
                        generateWeeklyReports(daysToRun,false, true, false, false);
                        break;
                    case '4': // eft report
                        //runMember, runProvider, runEFT, runSummary
                        generateWeeklyReports(daysToRun,false, false, true, false);
                        break;
                    case '5': // summary report
                        //runMember, runProvider, runEFT, runSummary
                        generateWeeklyReports(daysToRun,false, false, false, true);
                        break;
                    case '0': // exit to previous menu
                        inRunReports = false;
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

    private static void editMember()
    {
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
                terminal.sendOutput("Test member numbers: 333222333 or 333222339");
                memberNumber = Integer.parseInt(terminal.getInput("Enter member number, or 0 to return to previous menu:"));
            }
            catch(Exception ex)
            {
                terminal.sendOutput(ex.getMessage());
            }

            if (memberController.getMember(memberNumber)!= null)
            {
                try
                {
                    memberToEdit = memberController.getMember(memberNumber);
                    terminal.sendOutput("Edit:");
                    terminal.sendOutput("1) Name: " + memberToEdit.getName());
                    terminal.sendOutput("2) Address: " + memberToEdit.getStreetAddress());
                    terminal.sendOutput("3) City: " + memberToEdit.getCity());
                    terminal.sendOutput("4) State: " + memberToEdit.getState());
                    terminal.sendOutput("5) Zip: " + memberToEdit.getZipcode());
                    terminal.sendOutput("0) Exit to previous menu");
                    menuChoice = terminal.getInput("Enter Menu Option").toUpperCase().charAt(0);

                    String newData = "";
                    switch (menuChoice)
                    {
                        case '1': // Edit member name
                            try
                            {
                                newData = terminal.getInput("Enter new name or 0 to exit:");
                            }
                            catch(Exception ex)
                            {
                                terminal.sendOutput(ex.getMessage());
                                break;
                            }
                            if (!newData.equals("") && !newData.equals("0"))
                            {
                                memberToEdit.setName(newData);
                                memberController.save(MEMBER_CONTROLLER_FILE);
                            }
                            break;
                        case '2': // edit member address
                            try
                            {
                                newData = terminal.getInput("Enter new street address or 0 to exit:");
                            }
                            catch(Exception ex)
                            {
                                terminal.sendOutput(ex.getMessage());
                                break;
                            }
                            if (!newData.equals("") && !newData.equals("0"))
                            {
                                memberToEdit.setStreetAddress(newData);
                                memberController.save(MEMBER_CONTROLLER_FILE);
                            }
                            break;
                        case '3': // edit member city
                            try
                            {
                                newData = terminal.getInput("Enter new city or 0 to exit:");
                            }
                            catch(Exception ex)
                            {
                                terminal.sendOutput(ex.getMessage());
                                break;
                            }
                            if (!newData.equals("") && !newData.equals("0"))
                            {
                                memberToEdit.setCity(newData);
                                memberController.save(MEMBER_CONTROLLER_FILE);
                            }
                            break;
                        case '4': // edit member state
                            try
                            {
                                newData = terminal.getInput("Enter new state or 0 to exit:");
                            }
                            catch(Exception ex)
                            {
                                terminal.sendOutput(ex.getMessage());
                                break;
                            }
                            if (!newData.equals("") && !newData.equals("0"))
                            {
                                memberToEdit.setState(newData);
                                memberController.save(MEMBER_CONTROLLER_FILE);
                            }
                            break;
                        case '5': // edit member zip
                            try
                            {
                                newData = terminal.getInput("Enter new zipcode or 0 to exit:");
                            }
                            catch(Exception ex)
                            {
                                terminal.sendOutput(ex.getMessage());
                                break;
                            }
                            if (!newData.equals("") && !newData.equals("0"))
                            {
                                memberToEdit.setZipcode(Integer.parseInt(newData));
                                memberController.save(MEMBER_CONTROLLER_FILE);
                            }
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
            else if (memberNumber == 0)
            {
                loopThis = false;
            }
            else
            {
                terminal.sendOutput("No member of that number was found");
            }

        }

    }

    private static boolean addMember()
    {
        terminal.sendOutput("Add Member");
        String memberName = "";
        String memberAddress = "";
        String memberCity = "";
        String memberState = "";
        int memberZip = 0;
        int memberNumber = 0;

        //member number
        boolean loopThis = true;
        while(loopThis == true)
        {
            memberNumber = collectIntInput("Enter new 9 digit member number:");
            //check if it already exists
            if (memberController.getMember(memberNumber)!= null)
            {
                terminal.sendOutput("That member number is already used. Please enter another");
                loopThis = true;
            }
            else
            {
                loopThis = false;
            }
        }

        memberName = collectStringInput("Enter member name");
        memberAddress = collectStringInput("Enter street address");
        memberCity = collectStringInput("Enter city");
        memberState = collectStringInput("Enter state");
        memberZip = collectIntInput("Enter zipcode");

        Member newMember = new Member (memberName, memberAddress, memberCity, memberState, memberZip, memberNumber);   //new Member("Linda Schaefer", "4103 N 62nd St", "Milwaukee", "WI", 53213, 333222333));
        return memberController.add(newMember);
    }

    private static boolean addProvider()
    {
        terminal.sendOutput("Add Provider");
        String providerName = "";
        String providerAddress = "";
        String providerCity = "";
        String providerState = "";
        int providerZip = 0;
        int providerNumber = 0;

        //member number
        boolean loopThis = true;
        while(loopThis == true)
        {
            providerNumber = collectIntInput("Enter new 9 digit provider number:");
            //check if it already exists
            if (providerController.getProvider(providerNumber)!= null)
            {
                terminal.sendOutput("That provider number is already used. Please enter another");
                loopThis = true;
            }
            else
            {
                loopThis = false;
            }
        }

        providerName = collectStringInput("Enter provider name");
        providerAddress = collectStringInput("Enter street address");
        providerCity = collectStringInput("Enter city");
        providerState = collectStringInput("Enter state");
        providerZip = collectIntInput("Enter zipcode");

        Provider newProvider = new Provider (providerName, providerAddress, providerCity, providerState, providerZip, providerNumber);
        return providerController.add(newProvider);
    }

    private static String collectStringInput(String message)
    {
        String newData = "";
        boolean loopThis = true;
        while(loopThis)
        {
            try
            {
                newData = terminal.getInput(message);
            }
            catch(Exception ex)
            {
                terminal.sendOutput(ex.getMessage());
            }
            if (!newData.equals("") && !newData.equals("0"))
            {
                loopThis = false;
            }
            else
            {
                loopThis = true;
            }
        }
        return newData;
    }

    private static int collectIntInput(String message)
    {
        int newData = 0;
        boolean loopThis = true;
        while(loopThis)
        {
            try
            {
                newData = Integer.parseInt(terminal.getInput(message));
            }
            catch(Exception ex)
            {
                terminal.sendOutput(ex.getMessage());
            }
            if (newData != 0)
            {
                loopThis = false;
            }
            else
            {
                loopThis = true;
            }
        }
        return newData;
    }

    private static boolean removeMember()
    {
        terminal.sendOutput("Delete Member");
        int memberNumber = 0;
        Member memberToDelete;
        int confirmDelete = 0;

        //member number

        memberNumber = collectIntInput("Enter 9 digit member number of member to be deleted");
        //check if it already exists
        if (memberController.getMember(memberNumber)== null)
        {
            terminal.sendOutput("There is no member with that number.");
        }
        else
        {
            memberToDelete = memberController.getMember(memberNumber);
            terminal.sendOutput("Confirm deletion of "+ memberToDelete.getName());
            terminal.sendOutput("1. Confirm");
            confirmDelete = collectIntInput("2. Cancel");
        }

        if(confirmDelete == 1)
        {
            return memberController.remove(memberNumber);
        }
        else
        {
            terminal.sendOutput("Member Deletion Cancelled");
            return false;
        }
    }

    private static boolean removeProvider()
    {
        terminal.sendOutput("Delete Provider");
        int providerNumber = 0;
        Provider providerToDelete;
        int confirmDelete = 0;

        //member number

        providerNumber = collectIntInput("Enter 9 digit member number of provider to be deleted");
        //check if it already exists
        if (providerController.getProvider(providerNumber)== null)
        {
            terminal.sendOutput("There is no provider with that number.");
        }
        else
        {
            providerToDelete = providerController.getProvider(providerNumber);
            terminal.sendOutput("Confirm deletion of "+ providerToDelete.getName());
            terminal.sendOutput("1. Confirm");
            confirmDelete = collectIntInput("2. Cancel");
        }

        if(confirmDelete == 1)
        {
            return providerController.remove(providerNumber);
        }
        else
        {
            terminal.sendOutput("Provider Deletion Cancelled");
            return false;
        }
    }

    private static void editProvider()
    {
        //the administrator can edit provider info
        boolean editingProvider = true;
        char menuChoice = ' ';
        boolean loopThis = true;
        Integer providerNumber = 0;
        Provider providerToEdit;

        while(loopThis == true)
        {
            try
            {
                terminal.sendOutput("Edit Provider");
                terminal.sendOutput("Test Provider numbers: 111222333 or 111222555");
                providerNumber = Integer.parseInt(terminal.getInput("Enter provider number, or 0 to return to previous menu:"));
            }
            catch(Exception ex)
            {
                terminal.sendOutput(ex.getMessage());
            }

            if (providerController.getProvider(providerNumber)!= null)
            {
                try
                {
                    providerToEdit = providerController.getProvider(providerNumber);
                    terminal.sendOutput("Edit:");
                    terminal.sendOutput("1) Name: " + providerToEdit.getName());
                    terminal.sendOutput("2) Address: " + providerToEdit.getStreetAddress());
                    terminal.sendOutput("3) City: " + providerToEdit.getCity());
                    terminal.sendOutput("4) State: " + providerToEdit.getState());
                    terminal.sendOutput("5) Zip: " + providerToEdit.getZipcode());
                    terminal.sendOutput("0) Exit to previous menu");
                    menuChoice = terminal.getInput("Enter Menu Option").toUpperCase().charAt(0);

                    String newData = "";
                    switch (menuChoice)
                    {
                        case '1': // Edit member name
                            try
                            {
                                newData = terminal.getInput("Enter new name or 0 to exit:");
                            }
                            catch(Exception ex)
                            {
                                terminal.sendOutput(ex.getMessage());
                                break;
                            }
                            if (!newData.equals("") && !newData.equals("0"))
                            {
                                providerToEdit.setName(newData);
                                providerController.save(PROVIDER_CONTROLLER_FILE);
                            }
                            break;
                        case '2': // edit member address
                            try
                            {
                                newData = terminal.getInput("Enter new street address or 0 to exit:");
                            }
                            catch(Exception ex)
                            {
                                terminal.sendOutput(ex.getMessage());
                                break;
                            }
                            if (!newData.equals("") && !newData.equals("0"))
                            {
                                providerToEdit.setStreetAddress(newData);
                                providerController.save(PROVIDER_CONTROLLER_FILE);
                            }
                            break;
                        case '3': // edit member city
                            try
                            {
                                newData = terminal.getInput("Enter new city or 0 to exit:");
                            }
                            catch(Exception ex)
                            {
                                terminal.sendOutput(ex.getMessage());
                                break;
                            }
                            if (!newData.equals("") && !newData.equals("0"))
                            {
                                providerToEdit.setCity(newData);
                                providerController.save(PROVIDER_CONTROLLER_FILE);
                            }
                            break;
                        case '4': // edit member state
                            try
                            {
                                newData = terminal.getInput("Enter new state or 0 to exit:");
                            }
                            catch(Exception ex)
                            {
                                terminal.sendOutput(ex.getMessage());
                                break;
                            }
                            if (!newData.equals("") && !newData.equals("0"))
                            {
                                providerToEdit.setState(newData);
                                providerController.save(PROVIDER_CONTROLLER_FILE);
                            }
                            break;
                        case '5': // edit member zip
                            try
                            {
                                newData = terminal.getInput("Enter new zipcode or 0 to exit:");
                            }
                            catch(Exception ex)
                            {
                                terminal.sendOutput(ex.getMessage());
                                break;
                            }
                            if (!newData.equals("") && !newData.equals("0"))
                            {
                                providerToEdit.setZipcode(Integer.parseInt(newData));
                                providerController.save(PROVIDER_CONTROLLER_FILE);
                            }
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
            else if (providerNumber == 0)
            {
                loopThis = false;
            }
            else
            {
                terminal.sendOutput("No provider of that number was found");
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
                generateWeeklyReports(-1,true, true, true, true);
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