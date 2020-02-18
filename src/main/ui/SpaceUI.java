package ui;

import model.*;
import model.exception.FailedToOpenException;
import model.exception.IndexOutOfBoundsException;
import model.exception.SystemNotSupportedException;
import platformspecific.ResourceLauncher;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

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
    private static final String START_TIMER_CMD = "TIMER";
    private static final String EXIT_CMD = "EXIT";
    private static final String LINK_CMD = "LINK";
    private static final String APP_CMD = "APP";
    private static final String FILE_CMD = "FILE";
    private static final String CANCEL_CMD = "CANCEL";
    private static final String HELP_CMD = "HELP";
    private static final String CANCEL_TIMER_CMD = "CANCEL TIMER";
    private static final String TIMER_ICON_FILE = "timer.png";

    private Space space;
    private BufferedReader userInput;

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

            try {
                input = getInput().toUpperCase();

                if (input.equals(EXIT_CMD)) {
                    run = false;
                } else {
                    processInput(input);
                }
            } catch (IOException e) {
                System.out.println("Input stream error.");
            } catch (InterruptedException e) {
                timeUp();
            }
        }
    }

    // EFFECTS: initializes space
    private void init() {
        userInput = new BufferedReader(new InputStreamReader(System.in));
    }

    // EFFECTS: displays all space info
    private void displaySpaceMenu() {
        System.out.println();
        System.out.println(space.getName());
        System.out.println();
        displayResources();
        displayTodo();
        displayTimer();

        System.out.println("Enter \"" + EXIT_CMD + "\" to exit this space.");
        System.out.println("Enter \"" + HELP_CMD + "\" to see all commands.");
    }

    // EFFECTS: if timer is started, displays time, otherwise does nothing
    private void displayTimer() {
        if (space.isTimerRunning()) {
            System.out.println("Work time left: " + space.getTimeOnTimer());
        }
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
        System.out.println("\"" + OPEN_RESOURCE_CMD + " <resource number>\" - Open a resource");
        System.out.println("\"" + OPEN_ALL_CMD + "\" - Open all resources");
        System.out.println("\"" + ADD_RESOURCE_CMD + "\" - Add a resource");
        System.out.println("\"" + DELETE_RESOURCE_CMD + " <resource number>\" - Delete a resource");
        System.out.println("\"" + COMPLETE_TASK_CMD + " <task number>\" - Complete a task");
        System.out.println("\"" + ADD_TASK_CMD + "\" - Add a task");
        System.out.println("\"" + DELETE_TASK_CMD + " <task number>\" - Delete a task");
        System.out.println("\"" + START_TIMER_CMD + " <# of minutes>\" - Start a timer");
        System.out.println("\"" + CANCEL_TIMER_CMD + "\" - Cancel a timer");
    }

    // EFFECTS: if timer interrupts, throws TimeUpException
    //          or if there is an IO error, throws IOException
    //          otherwise waits for input and returns it
    private String getInput() throws InterruptedException, IOException {
        while (space.isTimerRunning() && !userInput.ready()) {
            Thread.sleep(100);
        }
        return userInput.readLine();
    }

    // MODIFIES: this
    // EFFECTS: processes a command
    private void processInput(String input) throws IOException, InterruptedException {
        if (input.equals(HELP_CMD)) {
            helpMenu();
        } else if (input.startsWith(OPEN_RESOURCE_CMD)) {
            launchResource(input.substring(OPEN_RESOURCE_CMD.length()));
        } else if (input.equals(ADD_RESOURCE_CMD)) {
            addResource();
        } else if (input.equals(ADD_TASK_CMD)) {
            addTask();
        } else if (input.startsWith(DELETE_RESOURCE_CMD)) {
            deleteResource(input.substring(DELETE_RESOURCE_CMD.length()));
        } else if (input.startsWith(DELETE_TASK_CMD)) {
            deleteTask(input.substring(DELETE_TASK_CMD.length()));
        } else if (input.startsWith(COMPLETE_TASK_CMD)) {
            processDoneCommand(input.substring(COMPLETE_TASK_CMD.length()));
        } else if (input.equals(OPEN_ALL_CMD)) {
            launchAllResources();
        } else if (input.startsWith(START_TIMER_CMD)) {
            startTimer(input.substring(START_TIMER_CMD.length()));
        } else if (input.equals(CANCEL_TIMER_CMD)) {
            space.cancelTimer();
        } else {
            invalidCommand();
        }
    }

    // EFFECTS: attempts to launch all resources, outputs errors if there are exceptions
    private void launchAllResources() {
        try {
            space.launchAllResources();
        } catch (SystemNotSupportedException e) {
            System.out.println("System does not support launching one or more resources.");
        } catch (FailedToOpenException e) {
            System.out.println("One or more resources failed to launch.");
        }
    }

    // MODIFIES: this
    // EFFECTS: processes input to start a new timer
    private void startTimer(String input) {
        int minutes;
        try {
            minutes = getIntFromInput(input);
        } catch (Exception e) {
            invalidCommand();
            return;
        }

        if (minutes < 0) {
            System.out.println("Timer length cannot be negative.");
            return;
        }

        Image icon;
        try {
            icon = ImageIO.read(new File("./data/" + TIMER_ICON_FILE));
        } catch (IOException e) {
            icon = new BufferedImage(10, 10, 1);
        }

        space.startTimer(minutes, Thread.currentThread(), icon);
    }

    // MODIFIES: this
    // EFFECTS: prints that timer time is up
    private void timeUp() {
        System.out.println("Time's up!");
        space.timeUp();
    }

    // MODIFIES: this
    // EFFECTS: processes a command to mark a task as complete
    private void processDoneCommand(String input) {
        int index;
        try {
            index = getIntFromInput(input) - 1;
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
            index = getIntFromInput(input) - 1;
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
            index = getIntFromInput(input) - 1;
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
    private void addResource() throws IOException, InterruptedException {
        Resource newResource;

        System.out.println("What type of resource to add? (enter \"" + LINK_CMD + "\",\"" + FILE_CMD
                + "\",\"" + APP_CMD + "\", or enter anything else to cancel)");
        String type = getInput().toUpperCase();

        if (type.equals(LINK_CMD) || type.equals(FILE_CMD) || type.equals(APP_CMD)) {
            System.out.println("Enter resource name.");
            String name = getInput();
            System.out.println("Enter resource (ie. website link, path to file, or path to executable)");
            String path = getInput();

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
    private void addTask() throws IOException {
        System.out.println("Enter new task (or enter \"" + CANCEL_CMD + "\" to cancel).");
        String input;
        input = userInput.readLine();

        if (!input.equals(CANCEL_CMD)) {
            space.getTodo().addTask(new Task(input));
        }
    }

    // EFFECTS: if input is valid resource number, attempts to launch resource, otherwise outputs error message
    private void launchResource(String input) {
        int resourceNumber;

        try {
            resourceNumber = getIntFromInput(input) - 1;
        } catch (Exception e) {
            System.out.println("You must input a resource number.");
            return;
        }

        try {
            space.launchResource(resourceNumber);
        } catch (SystemNotSupportedException e) {
            System.out.println("System does not support launching one or more resources.");
        } catch (FailedToOpenException e) {
            System.out.println("One or more resources failed to launch.");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Invalid resource index.");
        }
    }

    // EFFECTS: if input string is an int, ignoring white spaces, returns int
    private int getIntFromInput(String input) throws NumberFormatException {
        input = input.replaceAll("\\s+", "");
        return Integer.parseInt(input);
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
