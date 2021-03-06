package model;

import model.exception.FailedToOpenException;
import model.exception.IndexOutOfBoundsException;
import model.exception.SystemNotSupportedException;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// represents a space, which contains a list of resources and a to-do list
public class Space {
    private String name;
    private List<Resource> resources;
    private ToDoList todo;
    private boolean timerRunning;
    private WorkTimer timer;

    // EFFECTS: initializes new space with given name, no resources, and empty to-do list
    public Space(String name) {
        this.name = name;
        this.resources = new ArrayList<>();
        this.todo = new ToDoList();
        timerRunning = false;
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

    // MODIFIES: this
    // EFFECTS: removes resource of name from this space's resources
    public void removeResource(String name) {
        resources.removeIf(r -> r.getName().equals(name));
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

    // EFFECTS: attempts to launch resource with given index
    //          if index is out of bounds, throws IndexOutOfBoundsException
    //          if system does not support launching this resource, throws SystemNotSupportedException
    //          if resource fails to launch, throws FailedToOpenException
    //          otherwise, opens resource in appropriate application
    public void launchResource(int index) throws SystemNotSupportedException, FailedToOpenException,
            IndexOutOfBoundsException {
        if (index >= 0 && index < numResources()) {
            resources.get(index).launch();
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    // EFFECTS: if system does not support launching this resource, throws SystemNotSupportedException
    //          if resource fails to launch, throws FailedToOpenException
    //          otherwise, opens resource in appropriate application
    public void launchAllResources() throws SystemNotSupportedException, FailedToOpenException {
        for (Resource r: resources) {
            r.launch();
        }
    }

    // REQUIRES: hours >= 0, minutes >= 0
    // MODIFIES: this
    // EFFECTS: starts a new timer
    public void startTimer(int hours, int minutes) {
        timer = new WorkTimer(hours, minutes);
        timer.run();
        timerRunning = true;
    }

    // MODIFIES: this
    // EFFECTS: cancels a timer if it is currently running, otherwise does nothing
    public void cancelTimer() {
        if (timerRunning) {
            timer.cancelTimer();
        }
        timerRunning = false;
    }

    // REQUIRES: getTimerRunning() is true
    // EFFECTS: returns formatted time left on timer
    public String getTimeOnTimer() {
        return timer.getTime();
    }

    // MODIFIES: this
    // EFFECTS: tells space that timer time is up
    public void timeUp() {
        timerRunning = false;
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

    public boolean isTimerRunning() {
        return timerRunning;
    }

    public int numResources() {
        return resources.size();
    }

    //setters
    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public void setTodo(ToDoList todo) {
        this.todo = todo;
    }
}
