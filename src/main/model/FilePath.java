package model;

import platformspecific.ResourceLauncher;

import java.io.File;
import java.nio.file.NoSuchFileException;

// represents a file with a name and a File object
public class FilePath implements Resource {
    public static final String FILE_RESOURCE_TYPE = "FILE";
    protected String name;
    protected File file;

    // EFFECTS: initializes empty, unnamed FilePath object
    public FilePath() {
        this.name = "";
        this.file = null;
    }

    // EFFECTS: initializes FilePath with given name and path, throws exception if path is not valid
    public FilePath(String name, String path) throws NoSuchFileException {
        this.name = name;
        setPath(path);
    }

    // EFFECTS: returns file's extension
    public String getFileExtension() {
        int index = 0;
        for (char c : file.getName().toCharArray()) {
            if (c == '.') {
                return file.getName().substring(index + 1);
            }
            index++;
        }

        return "";
    }

    //getters
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPath() {
        return file.getPath();
    }

    @Override
    public String getResourceType() {
        return FILE_RESOURCE_TYPE;
    }

     //setters

    // EFFECTS: attempts to set file to given path, throws exception if path is not valid
    @Override
    public void setPath(String path) throws NoSuchFileException {
        File newFile = new File(path);
        if (newFile.exists()) {
            file = new File(path);
        } else {
            throw new NoSuchFileException(path);
        }
    }
}
