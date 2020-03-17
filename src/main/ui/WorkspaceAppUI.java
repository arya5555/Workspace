package ui;

import model.Space;
import model.WorkspaceApp;
import model.exception.InvalidFormatException;
import persistence.Reader;
import persistence.Writer;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class WorkspaceAppUI {
    protected static final String WORKSPACE_FILE = "./data/spaces.json";
    public WorkspaceApp workspace;

    // EFFECTS: displays a message to the user, depends on ui being used
    protected abstract void displayMessage(String message);
    
    // EFFECTS: runs the appropriate UI for a space
    protected abstract void runSpace(Space space);

    // EFFECTS: initializes the workspace application with a workspace
    public WorkspaceAppUI() {
        workspace = new WorkspaceApp();
    }
    
    // MODIFIES: this
    // EFFECTS: loads spaces from WORKSPACE_FILE, if that file exists;
    //          otherwise throws IOException if file does not exist, or InvalidFormatException
    protected void loadSpaces() throws IOException, InvalidFormatException {
        List<Space> spaces = Reader.readSpaces(new File(WORKSPACE_FILE));
        for (Space s : spaces) {
            workspace.addSpace(s);
        }
    }

    // EFFECTS: saves state of all spaces in workspace to WORKSPACE_FILE
    protected void saveSpaces() {
        try {
            Writer writer = new Writer(new File(WORKSPACE_FILE));
            writer.write(workspace);
            writer.close();
            displayMessage("Space data saved to file " + WORKSPACE_FILE);
        } catch (IOException e) {
            displayMessage("File error: Unable to save to " + WORKSPACE_FILE);
        }
    }

    // EFFECTS: initializes workspace with saved space data
    protected void loadSaveData() {
        try {
            loadSpaces();
        } catch (IOException e) {
            displayMessage("Could not find previous save file " + WORKSPACE_FILE + ". No saved spaces were loaded.");
        } catch (InvalidFormatException e) {
            displayMessage("Save file " + WORKSPACE_FILE
                    + " was found, but contents were not in the correct format. No saved spaces were loaded.");
        }
    }
}
