package model;

import model.exception.InvalidFormatException;
import model.exception.NoBackupFoundException;
import network.DatabaseTool;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import persistence.Reader;
import persistence.Saveable;
import persistence.Writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// represents the workspace app, containing a list of spaces
public class WorkspaceApp implements Saveable {
    public static final String WORKSPACE_FILE = "./data/spaces.json";

    List<Space> spaces;

    // EFFECTS: creates a new workspace app with no spaces
    public WorkspaceApp() {
        spaces = new ArrayList<>();
    }

    // EFFECTS: returns list of names of all spaces
    public List<String> getAllSpaceNames() {
        List<String> names = new ArrayList<>();

        for (Space s : spaces) {
            names.add(s.getName());
        }

        return names;
    }

    // EFFECTS: returns space with given name, if no such space exists, returns null
    public Space getSpaceOfName(String name) {
        for (Space s : spaces) {
            if (s.getName().equals(name)) {
                return s;
            }
        }

        return null;
    }

    // MODIFIES: this
    // EFFECTS: removes space with given name from this workspace app
    public void removeSpace(String name) {
        spaces.remove(getSpaceOfName(name));
    }

    // REQUIRES: space with this name is not already part of this workspace app
    // MODIFIES: this
    // EFFECTS: adds given space to workspace app
    public void addSpace(Space space) {
        spaces.add(space);
    }

    // source for saving to JSON file: https://howtodoinjava.com/library/json-simple-read-write-json-examples/
    @Override
    public void save(PrintWriter writer) {
        JSONArray spacesList = new JSONArray();
        for (Space s : spaces) {
            JSONObject spaceDetails = new JSONObject();
            JSONArray resources = new JSONArray();
            JSONArray tasks = s.getTodo().getListAsJson();

            spaceDetails.put("name", s.getName());
            for (Resource r : s.getResources()) {
                JSONObject resourceDetails = new JSONObject();
                resourceDetails.put("type", r.getResourceType().name());
                resourceDetails.put("name", r.getName());
                resourceDetails.put("path", r.getPath());
                resources.add(resourceDetails);
            }

            spaceDetails.put("resources", resources);
            spaceDetails.put("tasks", tasks);
            spacesList.add(spaceDetails);
        }

        String data = spacesList.toString();
        writer.write(data);
    }

    // MODIFIES: this
    // EFFECTS: loads spaces from WORKSPACE_FILE, if that file exists;
    //          otherwise throws IOException if file does not exist, or InvalidFormatException
    public void loadSpaces(String filePath) throws IOException, InvalidFormatException {
        List<Space> spaces = Reader.readSpaces(new File(filePath));
        this.spaces = spaces;
    }

    // EFFECTS: throws IOException if there is an error writing to file
    //          saves state of all spaces in workspace to WORKSPACE_FILE
    public void saveSpaces() throws IOException {
        saveSpaces(WORKSPACE_FILE);
    }

    // EFFECTS: throws IOException if there is an error writing to file
    //          saves state of all spaces in workspace to given file
    public void saveSpaces(String file) throws IOException {
        Writer writer = new Writer(new File(file));
        writer.write(this);
        writer.close();
    }

    //getters
    public List<Space> getSpaces() {
        return spaces;
    }

    //setters
    public void setSpaces(List<Space> spaces) {
        this.spaces = spaces;
    }
}
