import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    static final String  SERVICE_DIRECTORY_FILE = "serviceController.xml";
    static final String  SERVICE_RECORDS_FILE = "serviceRecords.xml";

    static ProviderController providerController;
    static MemberController memberController;
    static AdministratorController administratorController;
    static ServiceController serviceController;
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

        DataProcessingController dataProcessingController = new DataProcessingController();
        dataProcessingController.scheduleWeeklyReportGeneration();

        while(running)
        {
            iLogged[] loggedControllerArray = new iLogged[1];
            // using the loggedControllerArray to act like an out statement in C#
            loggedIn = loginEmployee(loggedControllerArray);
            iLogged loggedController = loggedControllerArray[0];
            List<String> mainMenuItems = loggedIn.getMenuItems();
            String sMemberNumber;
            int iMemberNum;
            while(loggedIn != null)
            {
                try
                {
                    displayMenu(mainMenuItems);
                    menuChoice = terminal.getInput("Enter Menu Option").toUpperCase().charAt(0);

                    switch (menuChoice)
                    {
                        case 'B':
                            int tries = 0;
                            String correctService;
                            // get member number and validate
                            sMemberNumber = terminal.getInput("Enter member Number: 333222335");
                            iMemberNum = Integer.parseInt(sMemberNumber);
                            Member member = memberController.getMember(iMemberNum);
                            // enter date of service
                            String sDateOfService = terminal.getInput("Enter the date of the service MM-dd-yy");

                            DateFormat df = new SimpleDateFormat("MM-dd-yy");
                            Date dt = df.parse(sDateOfService);
                            Calendar dateOfService = Calendar.getInstance();
                            Service serviceFound = null;
                            dateOfService.setTime(dt);
                            if(Calendar.getInstance().before(dateOfService)){
                                terminal.sendOutput("Date of service must take place in the past");
                                break;
                            }
                              while(serviceFound==null)
                                {
                                    try{
                                    serviceFound = serviceController.getService(Integer.parseInt(terminal.getInput("Enter the service code: 883948")));
                                    }catch (NumberFormatException  e){ }

                                    if(serviceFound == null){
                                        terminal.sendOutput("Invalid Service Code");
                                        if(++tries >= 3)
                                        {
                                            terminal.sendOutput("3 invalid attempts");
                                            break;
                                        } }
                                }

                                // display service name
                                if(serviceFound != null){
                                    correctService = terminal.getInput(String.format("Is %s the correct service: $%.2f? Y/N" , serviceFound.getServiceName(), serviceFound.getServiceFee()));
                                    if(correctService.toUpperCase().compareTo("Y") != 1){
                                        break;
                                    }


                            // enter comments
                            String comments = terminal.getInput("Enter any comments for this service here:");
                            // write service record to file
                            //int memberID, int serviceCode, String comments, Calendar dateAndTimeServiceEntered, Calendar dateOfService
                            serviceRecordController.saveNewServiceRecord(new ServiceRecord(((Provider)loggedIn).getIdentifier(), member.getIdentifier(), serviceFound.getServiceCode(), comments, GregorianCalendar.getInstance(), dateOfService));
                    }
                            break;
                        case 'E' : //Enter Member Number
                            sMemberNumber = terminal.getInput("Enter member Number:");
                            iMemberNum = 0;
                            try
                            {
                                iMemberNum = Integer.parseInt(sMemberNumber.trim());
                            }
                            catch(Exception ex)
                            {
                                terminal.sendOutput("Invalid Number");
                            }

                            Member tempMember = memberController.getMember(iMemberNum);
                            if(tempMember == null)
                            {
                                terminal.sendOutput(String.format("%s is an invalid member number.", sMemberNumber));
                            }
                            else if(!memberController.getMemberStatus(iMemberNum))
                            {
                                terminal.sendOutput(String.format("Member %s Suspended - Fees Owed: %d.", tempMember.getIdentifier(), tempMember.getFeesOwed()));
                            }
                            else
                            {
                                terminal.sendOutput(String.format("%s Validated", sMemberNumber));
                            }
                            break;
                        case 'I': // Interactive Mode for Administrator
                            //admin interactive mode is where member & provider info can be edited
                            administratorInteractiveMode();
                            break;
                        case 'D': // create service directory for provider
                            createServiceDirectory();
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
        String message = "Invalid Account";
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
                terminal.sendOutput(message);
                break;
            }
            else if(administratorController.tryLogIn(providerNumber) == AdministratorController.VALID)
            {
                loggedController[0] = administratorController;
                message = "Valid";
                terminal.sendOutput(message);
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
        providerController = new ProviderController(PROVIDER_CONTROLLER_FILE, memberController, serviceController);
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
        serviceController = new ServiceController(SERVICE_DIRECTORY_FILE);
        return serviceController.serviceDirectoryFileOpen();
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
    private static void generateWeeklyReports()
    {
        try
        {
            Map<String, iReport> reports = generateAllReports(lastBillingDate);

            for(Map.Entry<String, iReport> report : reports.entrySet())
            {
                report.getValue().sendReport(String.format("%s_%s.txt", report.getKey(), getDateFromCalendar(GregorianCalendar.getInstance())));
            }

            lastBillingDate = GregorianCalendar.getInstance(); // TODO: save to file?
        }
        catch (Exception ex)
        {
            terminal.sendOutput(ex.getMessage());
        }
    }

    private static Map<String, iReport> generateAllReports(Calendar startOfReportRequest)
    {
        Calendar today = GregorianCalendar.getInstance();
        Map<String, iReport> reports = new HashMap<String, iReport>();

        reports.putAll(generateProviderReport(startOfReportRequest));
        reports.putAll(generateEFTReport(startOfReportRequest));
        reports.putAll(generateManagerSummaryReport(startOfReportRequest));
        reports.putAll(generateMembersReport(startOfReportRequest));
        return reports;
    }

    private static Map<String, iReport> generateManagerSummaryReport(Calendar startOfReportRequest)
    {
        Calendar today = GregorianCalendar.getInstance();
        Map<String, iReport> reports = new HashMap<String, iReport>();
        ManagerSummaryReport summaryReport = new ManagerSummaryReport();

        for(Integer providerID : providerController.getProviders().keySet())
        {
            Set<ServiceRecord> serviceRecords = serviceRecordController.getListOfServiceRecordsByProvider(providerID, startOfReportRequest, today);
            Provider provider = providerController.getProvider(providerID);

            if(serviceRecords != null && !serviceRecords.isEmpty() && provider != null)
            {
                ProviderReport providerReport = new ProviderReport();
                providerReport.executeReport(provider, serviceRecords, memberController, serviceController);
                summaryReport.addSummaryEntry(provider.getName(), providerReport.getConsultations(), providerReport.getFeeTotal());
            }
        }
        summaryReport.executeReport();
        reports.put("Summary_Report",summaryReport);
        return reports;
    }

    private static Map<String, iReport> generateEFTReport(Calendar startOfReportRequest)
    {
        Calendar today = GregorianCalendar.getInstance();
        Map<String, iReport> reports = new HashMap<String, iReport>();
        EFTReport eftReport = new EFTReport();

        for(Integer providerID : providerController.getProviders().keySet())
        {
            Set<ServiceRecord> serviceRecords = serviceRecordController.getListOfServiceRecordsByProvider(providerID, startOfReportRequest, today);
            Provider provider = providerController.getProvider(providerID);

            if(serviceRecords != null && !serviceRecords.isEmpty() && provider != null)
            {
                ProviderReport providerReport = new ProviderReport();
                providerReport.executeReport(provider, serviceRecords, memberController, serviceController);
                eftReport.addEFTEntry(provider.getName(), provider.getIdentifier(), providerReport.getFeeTotal());
            }
        }
        eftReport.executeReport();
        reports.put("EFT_Report", eftReport);
        return reports;
    }

    private static Map<String, iReport> generateProviderReport(Calendar startOfReportRequest)
    {
        Calendar today = GregorianCalendar.getInstance();
        Map<String, iReport> reports = new HashMap<String, iReport>();

        for(Integer providerID : providerController.getProviders().keySet())
        {
            Set<ServiceRecord> serviceRecords = serviceRecordController.getListOfServiceRecordsByProvider(providerID, startOfReportRequest, today);
            Provider provider = providerController.getProvider(providerID);

            if(serviceRecords != null && !serviceRecords.isEmpty() && provider != null)
            {
                ProviderReport providerReport = new ProviderReport();
                providerReport.executeReport(provider, serviceRecords, memberController, serviceController);
                reports.put(provider.getName(), providerReport);
            }
        }
        return reports;
    }

    private static Map<String, iReport> generateMembersReport(Calendar startOfReportRequest)
    {
        Calendar today = GregorianCalendar.getInstance();
        Map<String, iReport> reports = new HashMap<String, iReport>();

        //Create memberReports for for the given time periord
        for(Integer memberID : memberController.getAllMembers().keySet())
        {
            Set<ServiceRecord> serviceRecords = serviceRecordController.getListOfServiceRecordsByMember(memberID, startOfReportRequest, today);
            Member member = memberController.getMember(memberID);

            if(serviceRecords != null && !serviceRecords.isEmpty() && member != null)
            {
                MemberReport memberReport = new MemberReport(serviceController,member,serviceRecords,providerController.getProviders());
                memberReport.executeReport();
                reports.put(member.getName(), memberReport);
            }
        }
        if(reports.isEmpty())
        {
            terminal.sendOutput("No entries found for that date range.");
        }
        else
        {
            for(Map.Entry<String, iReport> report : reports.entrySet())
            {
                report.getValue().sendReport(String.format("%s_%s.txt", report.getKey(), getDateFromCalendar(GregorianCalendar.getInstance())));
            }
        }
        return reports;
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

    private static void createServiceDirectory()
    {
        ServiceDirectory serviceDirectory = new ServiceDirectory();

        for (Service service : serviceController.getServicesForDirectory().values())
        {
            serviceDirectory.addServiceEntry(service.getServiceName(), service.getServiceCode(), service.getServiceFee());
        }

        serviceDirectory.executeDirectory();
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
        Map<String, iReport> reports = null;

        while(inRunReports == true)
        {
            Calendar startOfReportRequest = GregorianCalendar.getInstance();
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
               if(menuChoice != '0')
               {
                   daysToRun = Integer.valueOf(terminal.getInput("Enter the number of days (prior to today) you would like the reports from"));
                   startOfReportRequest.add(Calendar.DAY_OF_MONTH,(daysToRun *= -1));
               }

                switch (menuChoice)
                {
                    case '1': // Run All reports
                        //runMember, runProvider, runEFT, runSummary
                        reports = generateAllReports(startOfReportRequest);
                        break;
                    case '2': // member reports
                        //runMember, runProvider, runEFT, runSummary
                        reports = generateMembersReport(startOfReportRequest);
                        break;
                    case '3': // provider reports
                        //runMember, runProvider, runEFT, runSummary
                        reports = generateProviderReport(startOfReportRequest);
                        break;
                    case '4': // eft report
                        //runMember, runProvider, runEFT, runSummary
                        reports = generateEFTReport(startOfReportRequest);
                        break;
                    case '5': // summary report
                        //runMember, runProvider, runEFT, runSummary
                        reports = generateManagerSummaryReport(startOfReportRequest);
                        break;
                    case '0': // exit to previous menu
                        inRunReports = false;
                        break;
                    default:
                        terminal.sendOutput(String.format("%c is not a valid menu choice. ", menuChoice));
                        break;
                }
                for(Map.Entry<String, iReport> report : reports.entrySet())
                {
                    report.getValue().sendReport(String.format("%s_%s.txt", report.getKey(), getDateFromCalendar(GregorianCalendar.getInstance())));
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
                    int newZip = 0;
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
                            if (newData.length() == 2 && newData.matches("^[a-zA-Z]+$"))
                            {
                                memberToEdit.setState(newData);
                                memberController.save(MEMBER_CONTROLLER_FILE);
                            }
                            else
                            {
                                terminal.sendOutput("Incorrect entry - state not changed");
                            }
                            break;
                        case '5': // edit member zip
                            newZip = 0;
                            loopThis = true;
                            while(loopThis == true)
                            {
                                newZip = collectIntInput("Enter 5 digit zipcode:");
                                if (newZip < 100000 && newZip > 9999)
                                {
                                    loopThis = false;
                                }
                                else
                                {
                                    terminal.sendOutput("Number must be 5 digits");
                                    loopThis = true;
                                }
                            }
                            if (newZip != 0)
                            {
                                memberToEdit.setZipcode(newZip);
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
            else if (memberNumber < 1000000000 && memberNumber > 99999999)
            {
                loopThis = false;
            }
            else
            {
                terminal.sendOutput("Number must be 9 digits");
                loopThis = true;
            }
        }

        memberName = collectStringInput("Enter member name");
        memberAddress = collectStringInput("Enter street address");
        memberCity = collectStringInput("Enter city");
        loopThis = true;
        while(loopThis == true)
        {
            memberState = collectStringInput("Enter state");
            if (memberState.length() == 2 && memberState.matches("^[a-zA-Z]+$"))
            {
                loopThis = false;
            }
            else
            {
                terminal.sendOutput("State must be 2 letter characters");
                loopThis = true;
            }
        }


        loopThis = true;
        while(loopThis == true)
        {
            memberZip = collectIntInput("Enter 5 digit zipcode:");
            if (memberZip < 100000 && memberZip > 9999)
            {
                loopThis = false;
            }
            else
            {
                terminal.sendOutput("Number must be 5 digits");
                loopThis = true;
            }
        }

        Member newMember = new Member (memberName, memberAddress, memberCity, memberState, memberZip, memberNumber, 0,true);   //new Member("Linda Schaefer", "4103 N 62nd St", "Milwaukee", "WI", 53213, 333222333));
        return memberController.add(newMember);
    }

    private static boolean addProvider()
    {
        terminal.sendOutput("Add Provider");
        String providerName = "";
        String providerAddress = "";
        String providerCity = "";
        String providerState = "";
        String providerEmail = "";
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
            else if (providerNumber < 1000000000 && providerNumber > 99999999)
            {
                loopThis = false;
            }
            else
            {
                terminal.sendOutput("Number must be 9 digits");
                loopThis = true;
            }
        }

        providerName = collectStringInput("Enter provider name");
        providerAddress = collectStringInput("Enter street address");
        providerCity = collectStringInput("Enter city");

        loopThis = true;
        while(loopThis == true)
        {
            providerState = collectStringInput("Enter state");
            if (providerState.length() == 2 && providerState.matches("^[a-zA-Z]+$"))
            {
                loopThis = false;
            }
            else
            {
                terminal.sendOutput("State must be 2 letter characters");
                loopThis = true;
            }
        }

        //member zip
        loopThis = true;
        while(loopThis == true)
        {
            providerZip = collectIntInput("Enter 5 digit zipcode:");
            if (providerZip < 100000 && providerZip > 9999)
            {
                loopThis = false;
            }
            else
            {
                terminal.sendOutput("Number must be 5 digits");
                loopThis = true;
            }
        }

        providerEmail = collectStringInput("Enter email");

        Provider newProvider = new Provider (providerName, providerAddress, providerCity, providerState, providerZip, providerEmail, providerNumber);
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
            if (newData.equals(""))
            {
                loopThis = true;
            }
            else
            {
                loopThis = false;
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
                    terminal.sendOutput("6) Email: " + providerToEdit.getEmail());
                    terminal.sendOutput("0) Exit to previous menu");
                    menuChoice = terminal.getInput("Enter Menu Option").toUpperCase().charAt(0);

                    int newZip = 0;
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
                        case '2': // edit address
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
                        case '3': // edit  city
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
                        case '4': // edit  state
                            try
                            {
                                newData = terminal.getInput("Enter new state (2 characters) or 0 to exit:");
                            }
                            catch(Exception ex)
                            {
                                terminal.sendOutput(ex.getMessage());
                                break;
                            }
                            if (newData.length() == 2 && newData.matches("^[a-zA-Z]+$"))
                            {
                                providerToEdit.setState(newData);
                                providerController.save(PROVIDER_CONTROLLER_FILE);
                            }
                            else
                            {
                                terminal.sendOutput("Incorrect entry - state not changed");
                            }
                            break;
                        case '5': // edit  zip
                            newZip = 0;
                            loopThis = true;
                            while(loopThis == true)
                            {
                                newZip = collectIntInput("Enter 5 digit zipcode:");
                                if (newZip < 100000 && newZip > 9999)
                                {
                                    loopThis = false;
                                }
                                else
                                {
                                    terminal.sendOutput("Number must be 5 digits");
                                    loopThis = true;
                                }
                            }

                            if (newZip != 0)
                            {
                                providerToEdit.setZipcode(newZip);
                                providerController.save(PROVIDER_CONTROLLER_FILE);
                            }
                            break;
                        case '6': // edit provider email
                            try
                            {
                                newData = terminal.getInput("Enter new email or 0 to exit:");
                            }
                            catch(Exception ex)
                            {
                                terminal.sendOutput(ex.getMessage());
                                break;
                            }
                            if (!newData.equals("") && !newData.equals("0"))
                            {
                                providerToEdit.setEmail(newData);
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