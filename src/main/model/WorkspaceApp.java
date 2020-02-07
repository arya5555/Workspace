package model;

import java.util.ArrayList;
import java.util.List;

// represents the workspace app, containing a list of spaces
public class WorkspaceApp {
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

    //getters
    public List<Space> getSpaces() {
        return spaces;
    }
}
