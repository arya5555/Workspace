package ui.commandline;

import model.Space;
import ui.WorkspaceAppUI;

import java.util.*;

// Command line UI for Workspace application
public class WorkspaceAppCommandUI extends WorkspaceAppUI {
    private static final String ADD_SPACE_CMD = "ADD";
    private static final String DELETE_SPACE_CMD = "DEL";
    private static final String EXIT_CMD = "EXIT";
    private static final String CANCEL_CMD = "CANCEL";
    private static final String CONFIRM_CMD = "YES";
    private static final String BACKUP_CMD = "BACKUP";
    private static final String RESTORE_CMD = "RESTORE";
    private static final String HELP_CMD = "HELP";
    private static final String SIGN_IN_CMD = "LOGIN";
    private static final String SIGN_UP_CMD = "NEW";

    private static Set<String> COMMANDS;

    private Scanner userInput;

    // EFFECTS: initializes and runs the workspace application
    public WorkspaceAppCommandUI() {
        super();
        COMMANDS = new HashSet<>(
                Arrays.asList(ADD_SPACE_CMD, DELETE_SPACE_CMD, EXIT_CMD, CANCEL_CMD, CONFIRM_CMD,
                        BACKUP_CMD, RESTORE_CMD, HELP_CMD));
        runWorkspaceAppUI();
    }

    // MODIFIES: this
    // EFFECTS: displays main menu & processes user input
    private void runWorkspaceAppUI() {
        boolean run = true;
        String input;

        init();
        System.out.println("Welcome to your Workspace.");
        while (run) {
            displayMenu();
            input = userInput.nextLine().toUpperCase();

            if (input.equals("EXIT")) {
                run = false;
            } else {
                processInput(input);
            }
        }

        saveSpaces();
    }

    // EFFECTS: initializes workspace
    private void init() {
        userInput = new Scanner(System.in);
        loadSaveData();
    }

    // EFFECTS: display options
    private void displayMenu() {
        System.out.println("");
        listSpaces();
        System.out.println("Enter \"" + HELP_CMD + "\" to view all commands.");
    }

    // EFFECTS: list all of user's spaces
    private void listSpaces() {
        if (workspace.getSpaces().size() == 0) {
            System.out.println("You don't have any spaces yet.");
        } else {
            System.out.println("You have the following spaces:");
            for (Space s : workspace.getSpaces()) {
                System.out.println("  - " + s.getName());
            }
            System.out.println("Input the name of any space to enter it.");
        }
    }

    // MODIFIES: this
    // EFFECTS: processes a command
    private void processInput(String input) {
        if (input.equals(ADD_SPACE_CMD)) {
            addSpace();
        } else if (input.equals(DELETE_SPACE_CMD)) {
            deleteSpace();
        } else if (input.equals(HELP_CMD)) {
            helpMenu();
        } else if (workspace.getAllSpaceNames().contains(input)) {
            runSpace(workspace.getSpaceOfName(input));
        } else if (input.equals(BACKUP_CMD)) {
            //backupSpaceData();
        } else if (input.equals(RESTORE_CMD)) {
            //restoreSpacesFromBackup();
            //init();
        } else {
            System.out.println("Command was not recognized.");
        }
    }

    // EFFECTS: displays all possible input commands and their purpose
    private void helpMenu() {
        System.out.println("\"" + ADD_SPACE_CMD + "\": Create a new space.");
        System.out.println("\"" + DELETE_SPACE_CMD + "\": Delete a space.");
        System.out.println("\"" + EXIT_CMD + "\": Exit app and save data locally.");
        System.out.println("\"" + RESTORE_CMD + "\": Restore app data backed up with an account.");
        System.out.println("\"" + BACKUP_CMD + "\": Backup app data to an account.");
    }

    // MODIFIES: this
    // EFFECTS: creates a new space and adds it to the workspace app, as long as space name is valid
    private void addSpace() {
        boolean run = true;
        String input = null;

        while (run) {
            System.out.println("Enter the name of your new space (or enter \"" + CANCEL_CMD + "\" to cancel)");
            input = userInput.nextLine().toUpperCase();

            if (input.equals(CANCEL_CMD)) {
                run = false;
            } else {
                if (checkValidSpaceName(input)) {
                    workspace.addSpace(new Space(input));
                    run = false;
                } else {
                    System.out.println("That is not a valid space name.");
                    System.out.println("Either a space with that name already exists, or it is a command keyword.");
                }
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: asks user to specify which space to delete, then removes it from workspace
    private void deleteSpace() {
        boolean run = true;
        String input = null;

        while (run) {
            System.out.println("Enter name of space to delete (or enter \"" + CANCEL_CMD + "\" to cancel).");
            input = userInput.nextLine().toUpperCase();

            if (input.equals(CANCEL_CMD)) {
                run = false;
            } else if (workspace.getAllSpaceNames().contains(input)) {
                String spaceName = input.toUpperCase();
                System.out.println("Are you sure you want to delete " + spaceName + " space?");
                System.out.println("Enter \"" + CONFIRM_CMD + "\" to continue, or enter anything else to cancel.");

                input = userInput.nextLine().toUpperCase();

                if (input.equals(CONFIRM_CMD)) {
                    workspace.removeSpace(spaceName);
                    System.out.println(spaceName + " was deleted.");
                    run = false;
                } else {
                    System.out.println("Delete was cancelled.");
                }
            } else {
                System.out.println("Command was not recognized.");
            }
        }
    }

    // EFFECTS: returns true if name is not an existing space name and not a command keyword, otherwise returns false
    private boolean checkValidSpaceName(String name) {
        return (!(COMMANDS.contains(name)) && !(workspace.getAllSpaceNames().contains(name)));
    }

    // EFFECTS: outputs the given message to console
    protected void displayMessage(String message) {
        System.out.println(message);
    }

//    // EFFECTS: processes input to either sign into an account or create a new account
//    //          returns the account, or throws FailedToGetAccountException
//    private Account getAccount() throws FailedToGetAccountException {
//        System.out.println("Enter \"" + SIGN_IN_CMD + "\" to sign in to an existing account, \""
//                + SIGN_UP_CMD + "\" to create a new account, or anything else to cancel.");
//        String input = userInput.nextLine().toUpperCase();
//
//        if (input.equals(SIGN_IN_CMD)) {
//            return accountSignIn();
//        } else if (input.equals(SIGN_UP_CMD)) {
//            return newAccount();
//        } else {
//            throw new FailedToGetAccountException();
//        }
//    }
//
//    // EFFECTS: gets input to sign into account and returns account
//    //          if this fails, thows FailedToGetAccountException
//    private Account accountSignIn() throws FailedToGetAccountException {
//        System.out.println("Enter username:");
//        String username = userInput.nextLine();
//        System.out.println("Enter password:");
//        String password = userInput.nextLine();
//
//        try {
//            DatabaseTool databaseTool = new DatabaseTool();
//            Account account = databaseTool.signIn(username, password);
//            databaseTool.close();
//            return account;
//        } catch (SQLException e) {
//            System.out.println("Error reaching database. Could not sign in.");
//            throw new FailedToGetAccountException();
//        } catch (InvalidAccountException e) {
//            System.out.println("Username and password did not match. Could not sign in.");
//            throw new FailedToGetAccountException();
//        }
//    }
//
//    // EFFECTS: gets input and creates a new account, which is returned
//    //          throws FailedToGetAccount if there is an error connecting to database
//    private Account newAccount() throws FailedToGetAccountException {
//        System.out.println("Enter new username:");
//        String username = userInput.nextLine();
//        System.out.println("Enter new password:");
//        String password = userInput.nextLine();
//
//        try {
//            DatabaseTool databaseTool = new DatabaseTool();
//            databaseTool.createAccount(username, password);
//            Account account = databaseTool.signIn(username, password);
//            databaseTool.close();
//            return account;
//        } catch (SQLException e) {
//            System.out.println("Error communicating with database. New account was not created.");
//            throw new FailedToGetAccountException();
//        } catch (InvalidAccountException e) {
//            // programming error
//            throw new FailedToGetAccountException();
//        } catch (UsernameAlreadyExistsException e) {
//            System.out.println("Username already exists. Please choose another username.");
//            return newAccount();
//        }
//    }
//
//    // MODIFIES: this
//    // EFFECTS: restores space data from online backup under account if there is one
//    private void restoreSpacesFromBackup() {
//        JSONArray backup = null;
//
//        try {
//            Account account = getAccount();
//            DatabaseTool databaseTool = new DatabaseTool();
//            backup = databaseTool.retrieveBackup(account);
//            databaseTool.close();
//        } catch (FailedToGetAccountException e) {
//            return;
//        } catch (SQLException e) {
//            System.out.println("Failed to communicate with database to retrieve data. Data was not restored.");
//            return;
//        } catch (NoBackupFoundException e) {
//            System.out.println("No backup was found for this account. Data was not restored.");
//            return;
//        }
//
//        try {
//            Writer writer = new Writer(new File(WORKSPACE_FILE));
//            writer.write(backup.toString());
//            writer.close();
//            System.out.println("Data was sucessfully retrieved.");
//        } catch (IOException e) {
//            System.out.println("Failed to save retrieved data to " + WORKSPACE_FILE + ". Data was not restored.");
//        }
//    }
//
//    // EFFECTS: backs up space data to account
//    private void backupSpaceData() {
//        saveSpaces();
//
//        Account account;
//
//        try {
//            account = getAccount();
//        } catch (FailedToGetAccountException e) {
//            System.out.println("Data was not backed up.");
//            return;
//        }
//
//        try {
//            JSONArray data = Reader.readFile(new File(WORKSPACE_FILE));
//            DatabaseTool databaseTool = new DatabaseTool();
//            databaseTool.backupData(account, data);
//            databaseTool.close();
//            System.out.println("Data successfully backed up.");
//        } catch (Exception e) {
//            System.out.println("Failed to communicate with database to backup data. Data was not backed up.");
//        }
//    }

    // MODIFIES: this
    // EFFECTS: enters a space
    protected void runSpace(Space space) {
        new SpaceCommandUI(space);
    }
}
