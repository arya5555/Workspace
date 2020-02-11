package model;

import java.util.ArrayList;
import java.util.List;

// represents a space, which contains a list of resources and a to-do list
public class Space {
    private String name;
    private List<Resource> resources;
    private ToDoList todo;

    // EFFECTS: initializes new space with given name, no resources, and empty to-do list
    public Space(String name) {
        this.name = name;
        this.resources = new ArrayList<>();
        this.todo = new ToDoList();
    }

    // MODIFIES: this
    // EFFECTS: adds Resource r to this space
    public void addResource(Resource r) {
        resources.add(r);
    }

    // REQUIRES: 0 <= index < # resources
    // MODIFIES: this
    // EFFECTS: removes resource of index from this space's resources
    public void removeResource(int index) {
        resources.remove(index);
    }

    // EFFECTS: returns resource with given name if it exists in this space,
    // otherwise returns null
    public Resource getResourceOfName(String name) {
        for (Resource r : resources) {
            if (r.getName().equals(name)) {
                return r;
            }
        }

        return null;
    }

    // EFFECTS: returns list of names of all resources in this space
    public List<String> getAllResourceNames() {
        List<String> names = new ArrayList<>();

        for (Resource r : resources) {
            names.add(r.getName());
        }

        return names;
    }

    //getters
    public List<Resource> getResources() {
        return resources;
    }

    public String getName() {
        return name;
    }

    public ToDoList getTodo() {
        return todo;
    }
}
