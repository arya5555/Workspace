package model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

// represents a to-do list, which contains a list of tasks
public class ToDoList {
    public static final String COMPLETED_TASK_PREFIX = "";
    List<Task> tasks;

    // EFFECTS: creates new to-do list with no tasks
    public ToDoList() {
        tasks = new ArrayList<>();
    }

    // MODIFIES: this
    // EFFECTS: adds given task to to-do list
    public void addTask(Task task) {
        tasks.add(task);
    }

    // REQUIRES: 0 <= index < # tasks in to-do list
    // MODIFIES: this
    // EFFECTS: removes task of given index from to-do list
    public void removeTask(int index) {
        tasks.remove(index);
    }

    // MODIFIES: this
    // EFFECTS: removes task of given description from to-do list
    public void removeTask(String description) {
        tasks.removeIf(t -> t.getDescription().equals(description));
    }

    // REQUIRES: 0 <= index < # tasks in to-do list
    // MODIFIES: this
    // EFFECTS: marks task of given index as complete in to-do list
    public void completeTask(int index) {
        tasks.get(index).setComplete(true);
    }

    // MODIFIES: this
    // EFFECTS: marks task of given description as complete in to-do list
    //          if no task matching description exists, does nothing
    public void completeTask(String description) {
        for (Task t : tasks) {
            if (t.getDescription().equals(description)) {
                t.setComplete(true);
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: deletes all tasks which are complete
    public void deleteCompletedTasks() {
        tasks.removeIf(Task::getComplete);
    }

    // EFFECTS: returns number of tasks in this to-do list
    public int getNumToDos() {
        return tasks.size();
    }

    // EFFECTS: returns list of all task descriptions in this to-do list,
    // all completed tasks have a prefix of "*DONE*"
    public List<String> getAllTaskDescriptions() {
        List<String> descriptions = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getComplete()) {
                descriptions.add(COMPLETED_TASK_PREFIX + t.getDescription());
            } else {
                descriptions.add(t.getDescription());
            }
        }

        return descriptions;
    }

    // getters
    public List<Task> getTasks() {
        return tasks;
    }

    // EFFECTS: returns a JSON array containing all tasks in to-do list
    public JSONArray getListAsJson() {
        JSONArray tasksList = new JSONArray();
        for (Task t : tasks) {
            JSONObject taskDetails = new JSONObject();
            taskDetails.put("description", t.getDescription());
            taskDetails.put("complete?", t.getComplete());
            tasksList.add(taskDetails);
        }

        return tasksList;
    }
}
