package ui;

import model.Space;
import model.WorkspaceApp;
import model.exception.InvalidFormatException;
import persistence.Reader;
import persistence.Writer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

// Command line UI for Workspace application
public class WorkspaceAppUI {
    private static final String WORKSPACE_FILE = "./data/spaces.json";
    private static final String ADD_SPACE_CMD = "ADD";
    private static final String DELETE_SPACE_CMD = "DEL";
    private static final String EXIT_CMD = "EXIT";
    private static final String CANCEL_CMD = "CANCEL";
    private static final String CONFIRM_CMD = "YES";
    private static List<String> COMMANDS;

    WorkspaceApp workspace;
    private Scanner userInput;

    // EFFECTS: initializes and runs the workspace application
    public WorkspaceAppUI() {
        workspace = new WorkspaceApp();
        COMMANDS = new ArrayList<>(
                Arrays.asList(ADD_SPACE_CMD, DELETE_SPACE_CMD, EXIT_CMD, CANCEL_CMD, CONFIRM_CMD));
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
        System.out.println("Input \"" + ADD_SPACE_CMD + "\" to create a new space.");
        System.out.println("Input \"" + DELETE_SPACE_CMD + "\" to delete a space.");
        System.out.println("Input \"" + EXIT_CMD + "\" to exit.");
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
        } else if (workspace.getAllSpaceNames().contains(input)) {
            runSpace(workspace.getSpaceOfName(input));
        } else {
            System.out.println("Command was not recognized.");
        }
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
