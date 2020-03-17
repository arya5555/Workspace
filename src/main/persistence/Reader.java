package persistence;

import model.*;
import model.exception.InvalidFormatException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

// allows reading a list of spaces from a JSON file
// general format taken from TellerApp
public class Reader {

    // EFFECTS: if file can't be found, throws IOException
    //          if file is not in correct JSON format, throws InvalidFormatException
    //          otherwise, returns a list of spaces parsed from JSON file
    public static List<Space> readSpaces(File file) throws IOException, InvalidFormatException {
        JSONArray fileContent;
        try {
            fileContent = readFile(file);
        } catch (ParseException e) {
            throw new InvalidFormatException();
        }

        List<Space> spaces;
        try {
            spaces = parseContent(fileContent);
        } catch (Exception e) {
            throw new InvalidFormatException();
        }

        return spaces;
    }

    // EFFECTS: returns JSON array read from file
    //          if file can't be found, throws IOException
    //          if file is not in correct JSON format, throws InvalidFormatException
    // source for reading from JSON file: https://howtodoinjava.com/library/json-simple-read-write-json-examples/
    public static JSONArray readFile(File file) throws IOException, ParseException {
        FileReader fileReader = new FileReader(file);
        JSONParser parser = new JSONParser();
        Object contentObject = parser.parse(fileReader);
        return (JSONArray) contentObject;
    }

    // EFFECTS: parses content for space data and returns list of spaces
    private static List<Space> parseContent(JSONArray contentArray) throws MalformedURLException, NoSuchFileException {
        List<Space> spaces = new ArrayList<>();
        for (Object object : contentArray) {
            JSONObject jsonSpaceObject = (JSONObject) object;
            Space space = new Space((String) jsonSpaceObject.get("name"));

            List<Resource> resources = parseResources((JSONArray) jsonSpaceObject.get("resources"));
            ToDoList todo = parseTodo((JSONArray) jsonSpaceObject.get("tasks"));

            space.setResources(resources);
            space.setTodo(todo);
            spaces.add(space);
        }

        return spaces;
    }

    // EFFECTS: returns list of resources from JSONArray containing resources
    //          throws MalformedURLException if resource is a site with invalid url
    //          throws NoSuchFileException if resource is a file or app file that can't be found
    private static List<Resource> parseResources(JSONArray jsonResourceArray)
            throws MalformedURLException, NoSuchFileException {

        List<Resource> resources = new ArrayList<>();
        for (Object resourceObject : jsonResourceArray) {
            JSONObject jsonResourceObject = (JSONObject) resourceObject;
            String name = (String) jsonResourceObject.get("name");
            String type = (String) jsonResourceObject.get("type");
            String path = (String) jsonResourceObject.get("path");

            Resource resource;
            if (type.equals(Resource.ResourceType.LINK.toString())) {
                resource = new WebsiteLink(name, path);
            } else if (type.equals(Resource.ResourceType.FILE.toString())) {
                resource = new FilePath(name, path);
            } else {
                resource = new AppShortcut(name, path);
            }

            resources.add(resource);
        }

        return resources;
    }

    // EFFECTS: returns to-do list from JSONArray containing tasks
    private static ToDoList parseTodo(JSONArray jsonTaskArray) {
        ToDoList todo = new ToDoList();

        for (Object taskObject : jsonTaskArray) {
            JSONObject jsonTaskObject = (JSONObject) taskObject;
            String description = (String) jsonTaskObject.get("description");
            Boolean complete = (Boolean) jsonTaskObject.get("complete?");

            Task task = new Task(description);
            task.setComplete(complete);

            todo.addTask(task);
        }

        return todo;
    }
}
