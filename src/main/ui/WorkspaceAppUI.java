package ui;

import model.Account;
import model.Space;
import model.WorkspaceApp;
import model.exception.*;
import org.json.simple.JSONArray;
import persistence.Reader;
import persistence.Writer;
//import tools.DatabaseTool;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

// Command line UI for Workspace application
public class WorkspaceAppUI {
    private static final String WORKSPACE_FILE = "./data/spaces.json";
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

    WorkspaceApp workspace;
    private Scanner userInput;

    // EFFECTS: initializes and runs the workspace application
    public WorkspaceAppUI() {
        workspace = new WorkspaceApp();
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

    // MODIFIES: this
    // EFFECTS: loads spaces from WORKSPACE_FILE, if that file exists;
    // otherwise does nothing
    private void loadSpaces() throws IOException, InvalidFormatException {
        List<Space> spaces = Reader.readSpaces(new File(WORKSPACE_FILE));
        for (Space s : spaces) {
            workspace.addSpace(s);
        }
    }

    // EFFECTS: saves state of all spaces in workspace to WORKSPACE_FILE
    private void saveSpaces() {
        try {
            Writer writer = new Writer(new File(WORKSPACE_FILE));
            writer.write(workspace);
            writer.close();
            System.out.println("Space data saved to file " + WORKSPACE_FILE);
        } catch (IOException e) {
            System.out.println("File error: Unable to save to " + WORKSPACE_FILE);
        }
    }

    // EFFECTS: initializes workspace
    private void init() {
        userInput = new Scanner(System.in);
        try {
            loadSpaces();
        } catch (IOException e) {
            System.out.println("Could not find previous save file " + WORKSPACE_FILE);
            System.out.println("No saved spaces were loaded.");
        } catch (InvalidFormatException e) {
            System.out.println("Save file " + WORKSPACE_FILE
                    + " was found, but contents were not in the correct format");
            System.out.println("No saved spaces were loaded.");
        }
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

    // MODIFIES: this
    // EFFECTS: enters a space
    private void runSpace(Space space) {
        new SpaceUI(space);
    }
}
