package model;

// represents a task to be placed in a to-do list, with a description and tracks if it is complete
public class Task {
    private boolean complete;
    private String description;

    // EFFECTS: sets this task's description to given String,
    // sets task as incomplete
    public Task(String description) {
        this.description = description;
        complete = false;
    }

    // EFFECTS: sets this task's description to given String,
    // sets task as completed if complete is true and incomplete if complete is false
    public Task(String description, boolean complete) {
        this.description = description;
        this.complete = complete;
    }

    // getters
    public boolean getComplete() {
        return complete;
    }

    public String getDescription() {
        return description;
    }

    // setters
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
