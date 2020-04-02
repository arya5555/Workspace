package ui;

import model.Account;
import model.Space;
import model.WorkspaceApp;
import model.exception.InvalidFormatException;
import model.exception.NoBackupFoundException;
import network.DatabaseBackupManager;
import network.DatabaseTool;
import org.json.simple.JSONArray;
import persistence.Reader;
import persistence.Writer;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static model.WorkspaceApp.WORKSPACE_FILE;

public abstract class WorkspaceAppUI {
    public WorkspaceApp workspace;

    // EFFECTS: displays a message to the user, depends on ui being used
    protected abstract void displayMessage(String message);
    
    // EFFECTS: runs the appropriate UI for a space
    protected abstract void runSpace(Space space);

    // EFFECTS: refreshes the ui to display any changes in the list of spaces
    protected abstract void refresh();

    // EFFECTS: initializes the workspace application with a workspace
    public WorkspaceAppUI() {
        workspace = new WorkspaceApp();
    }

    // EFFECTS: saves state of all spaces in workspace to WORKSPACE_FILE and displays feedback messages
    public void saveSpaces() {
        try {
            workspace.saveSpaces();
            displayMessage("Workspace data saved to file " + WORKSPACE_FILE);
        } catch (IOException e) {
            displayMessage("File error: Unable to save to " + WORKSPACE_FILE);
        }
    }

    // MODIFIES: this
    // EFFECTS: initializes workspace with saved space data and displays feedback messages
    public void loadSaveData() {
        loadSaveData(WORKSPACE_FILE);
    }

    // MODIFIES: this
    // EFFECTS: initializes workspace with saved space data and displays feedback messages
    public void loadSaveData(String filePath) {
        try {
            workspace.loadSpaces(filePath);
            refresh();
        } catch (IOException e) {
            displayMessage("Could not find previous save file " + filePath + ". No saved spaces were loaded.");
        } catch (InvalidFormatException e) {
            displayMessage("Save file " + filePath
                    + " was found, but contents were not in the correct format. No saved spaces were loaded.");
        }
    }

    // EFFECTS: stores workspace data in database, or displays error message if unable
    public void backupData(Account account) {
        try {
            saveSpaces();
            DatabaseBackupManager.backupData(account);
            displayMessage("Data successfully backed up.");
        } catch (SQLException e) {
            e.printStackTrace();
            displayMessage("Failed to communicate with database to backup data.");
        } catch (Exception e) {
            displayMessage("Could not find valid save data at " + WORKSPACE_FILE);
        }
    }

    // MODIFES: this
    // EFFECTS: overwrites local save data with backed up data and displays feedback message
    public void restoreBackup(Account account) {
        try {
            DatabaseBackupManager.restoreBackup(account);
            loadSaveData();
        } catch (SQLException e) {
            displayMessage("Failed to connect to database to retrieve backup.");
        } catch (NoBackupFoundException e) {
            displayMessage("No backup found for this account.");
        } catch (IOException ex) {
            displayMessage("Failed to save retrieved data to " + WORKSPACE_FILE + ". Data was not restored.");
        }
    }
}
