package ui;

import model.*;

import java.util.List;
import java.util.Scanner;

// Command line UI within a space
public class SpaceUI {
    private static final int TABLE_COLUMN_WIDTH = 30;
    private static final String OPEN_RESOURCE_CMD = "OPEN";
    private static final String OPEN_ALL_CMD = "OPEN ALL";
    private static final String ADD_RESOURCE_CMD = "ADD RES";
    private static final String DELETE_RESOURCE_CMD = "DEL RES";
    private static final String ADD_TASK_CMD = "ADD TASK";
    private static final String DELETE_TASK_CMD = "DEL TASK";
    private static final String COMPLETE_TASK_CMD = "DONE";
    private static final String EXIT_CMD = "EXIT";
    private static final String LINK_CMD = "LINK";
    private static final String APP_CMD = "APP";
    private static final String FILE_CMD = "FILE";
    private static final String CANCEL_CMD = "CANCEL";
    private static final String HELP_CMD = "HELP";

    private Space space;
    private Scanner userInput;

    // EFFECTS: initializes and runs the ui for a space
    public SpaceUI(Space space) {
        this.space = space;
        runSpaceUI();
    }

    // MODIFIES: this
    // EFFECTS: displays space menu & processes user input
    private void runSpaceUI() {
        boolean run = true;
        String input;
        init();

        while (run) {
            displaySpaceMenu();
            input = userInput.nextLine().toUpperCase();

            if (input.equals(EXIT_CMD)) {
                run = false;
            } else {
                processInput(input);
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: processes a command
    private void processInput(String input) {
        if (input.equals(HELP_CMD)) {
            helpMenu();
        } else if (input.length() >= OPEN_RESOURCE_CMD.length()
                && input.substring(0, OPEN_RESOURCE_CMD.length()).equals(OPEN_RESOURCE_CMD)) {
            launchResource(input.substring(OPEN_RESOURCE_CMD.length()));
        } else if (input.equals(ADD_RESOURCE_CMD)) {
            addResource();
        } else if (input.equals(ADD_TASK_CMD)) {
            addTask();
        } else if (input.length() >= DELETE_RESOURCE_CMD.length()
                && input.substring(0, DELETE_RESOURCE_CMD.length()).equals(DELETE_RESOURCE_CMD)) {
            deleteResource(input.substring(DELETE_RESOURCE_CMD.length()));
        } else if (input.length() >= DELETE_TASK_CMD.length()
                && input.substring(0, DELETE_TASK_CMD.length()).equals(DELETE_TASK_CMD)) {
            deleteTask(input.substring(DELETE_TASK_CMD.length()));
        } else if (input.length() >= COMPLETE_TASK_CMD.length()
                && input.substring(0, COMPLETE_TASK_CMD.length()).equals(COMPLETE_TASK_CMD)) {
            processDoneCommand(input.substring(COMPLETE_TASK_CMD.length()));
        } else if (input.equals(OPEN_ALL_CMD)) {
            space.launchAllResources();
        } else {
            invalidCommand();
        }
    }

    // MODIFIES: this
    // EFFECTS: processes a command to mark a task as complete
    private void processDoneCommand(String input) {
        int index;
        try {
            index = getIntFromInput(input);
        } catch (Exception e) {
            invalidCommand();
            return;
        }

        if (index >= 0 && index < space.getTodo().getNumToDos()) {
            space.getTodo().completeTask(index);
        } else {
            System.out.println("Invalid task number.");
        }
    }

    // MODIFIES: this
    // EFFECTS: deletes a resource if input gives a valid resource index, otherwise prints error and does nothing
    private void deleteResource(String input) {
        int index;
        try {
            index = getIntFromInput(input);
        } catch (Exception e) {
            System.out.println("Missing resource index.");
            return;
        }

        if (index >= 0 && index < space.getResources().size()) {
            space.removeResource(index);
        } else {
            System.out.println("Invalid resource number.");
        }
    }

    // MODIFIES: this
    // EFFECTS: deletes a task if input gives a valid resource index, otherwise prints error and does nothing
    private void deleteTask(String input) {
        int index;
        try {
            index = getIntFromInput(input);
        } catch (Exception e) {
            System.out.println("Missing task index.");
            return;
        }

        if (index >= 0 && index < space.getTodo().getNumToDos()) {
            space.getTodo().removeTask(index);
        } else {
            System.out.println("Invalid task number.");
        }
    }

    // MODIFIES: this
    // EFFECTS: processes user input to add a resource, adds it to space resources
    private void addResource() {
        Resource newResource;

        System.out.println("What type of resource to add? (enter \"" + LINK_CMD + "\",\"" + FILE_CMD
                + "\",\"" + APP_CMD + "\", or enter anything else to cancel)");
        String type = userInput.next().toUpperCase();
        userInput.nextLine();

        if (type.equals(LINK_CMD) || type.equals(FILE_CMD) || type.equals(APP_CMD)) {
            System.out.println("Enter resource name.");
            String name = userInput.nextLine();
            System.out.println("Enter resource (ie. website link, path to file, or path to executable)");
            String path = userInput.nextLine();

            try {
                if (type.equals(LINK_CMD)) {
                    newResource = new WebsiteLink(name, path);
                } else if (type.equals(FILE_CMD)) {
                    newResource = new FilePath(name, path);
                } else {
                    newResource = new AppShortcut(name, path);
                }
                space.addResource(newResource);
            } catch (Exception e) {
                System.out.println("Invalid link or filepath.");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: processes user input to add a task, adds it to space to-do list
    private void addTask() {
        System.out.println("Enter new task (or enter \"" + CANCEL_CMD + "\" to cancel).");
        String input = userInput.nextLine();

        if (input.equals(CANCEL_CMD)) {
            return;
        } else {
            space.getTodo().addTask(new Task(input));
        }
    }

    // EFFECTS: if input string is an int, ignoring white spaces, returns int
    public int getIntFromInput(String input) throws NumberFormatException {
        input = input.replaceAll("\\s+", "");
        return Integer.parseInt(input) - 1;
    }

    // EFFECTS: returns true if input is valid resource number, and resource is successfully launched
    // otherwise returns false and outputs error message
    private boolean launchResource(String input) {
        int resourceNumber;

        try {
            resourceNumber = getIntFromInput(input);
        } catch (Exception e) {
            System.out.println("Invalid resource number.");
            return false;
        }

        if (resourceNumber < 0 || resourceNumber > space.getResources().size()) {
            System.out.println("Invalid resource number.");
            return false;
        }

        if (!(space.launchResource(resourceNumber))) {
            System.out.println("The resource failed to launch.");
        }
        return true;
    }

    // EFFECTS: initializes space
    private void init() {
        userInput = new Scanner(System.in);
    }

    // EFFECTS: displays all space info
    private void displaySpaceMenu() {
        System.out.println();
        System.out.println(space.getName());
        System.out.println();
        displayResources();
        displayTodo();
        // timer

        System.out.println("Enter " + EXIT_CMD + " to exit this space.");
        System.out.println("Enter " + HELP_CMD + " to see all commands.");
    }

    // EFFECTS: if this space has resources, displays them in a numbered table
    private void displayResources() {
        if (space.getResources().size() > 0) {
            displayNumberedTable("Resources", space.getAllResourceNames());
        } else {
            System.out.println("You don't have any resources yet.");
        }
        System.out.println();
    }

    // EFFECTS: if this space has tasks in its to-do list, displays tasks in a numbered table
    private void displayTodo() {
        if (space.getTodo().getNumToDos() > 0) {
            displayNumberedTable("To-Do", space.getTodo().getAllTaskDescriptions());
        } else {
            System.out.println("You don't have any tasks in your to-do list yet.");
        }
        System.out.println();
    }

    // EFFECTS: displays all commands
    private void helpMenu() {
        System.out.println("To open a resource, enter \"" + OPEN_RESOURCE_CMD + " <resource number>\"");
        System.out.println("To open all resources, enter \"" + OPEN_RESOURCE_CMD + " " + OPEN_ALL_CMD + "\"");
        System.out.println("To add a resource, enter \"" + ADD_RESOURCE_CMD + "\"");
        System.out.println("To delete a resource, enter \"" + DELETE_RESOURCE_CMD + " <resource number>\"");
        System.out.println("To complete a task, enter \"" + COMPLETE_TASK_CMD + " <task number>\"");
        System.out.println("To add a task, enter \"" + ADD_TASK_CMD + "\"");
        System.out.println("To delete a task, enter \"" + DELETE_TASK_CMD + " <task number>\"");
    }

    // EFFECTS: displays table with given title and with each row numbered {1,2,3...}
    private void displayNumberedTable(String title, List<String> rows) {
        String rowFormat = "| %-4s | %-" + TABLE_COLUMN_WIDTH + "s |%n";
        printHorizontalTableDivider();
        System.out.printf("| %-" + (TABLE_COLUMN_WIDTH + 7) + "s |%n", title);
        printHorizontalTableDivider();

        int i = 1;
        for (String s : rows) {
            if (s.length() <= TABLE_COLUMN_WIDTH) {
                System.out.format(rowFormat, i, s);
            } else {
                System.out.format(rowFormat, i, s.substring(0, TABLE_COLUMN_WIDTH - 1) + "-");
                String remaining = s.substring(TABLE_COLUMN_WIDTH - 1);
                while (remaining.length() > TABLE_COLUMN_WIDTH - 1) {
                    System.out.format(rowFormat, "", remaining.substring(0, TABLE_COLUMN_WIDTH - 1) + "-");
                    remaining = remaining.substring(TABLE_COLUMN_WIDTH - 1);
                }
                System.out.format(rowFormat, "", remaining);
            }
            i++;
        }

        printHorizontalTableDivider();
    }

    // EFFECTS: prints divider for numbered table
    private void printHorizontalTableDivider() {
        System.out.print("+");
        for (int j = 0; j < TABLE_COLUMN_WIDTH + 9; j++) {
            System.out.print("-");
        }
        System.out.println("+");
    }

    // EFFECTS: prints comment when invalid command entered
    private void invalidCommand() {
        System.out.println("Command not recognized.");
    }
}
