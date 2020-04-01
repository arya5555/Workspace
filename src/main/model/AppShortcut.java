package model;

import java.nio.file.NoSuchFileException;

// Represents an shortcut to an app, with a name and File object which is an executable
public class AppShortcut extends FilePath {

    // EFFECTS: creates new AppShortcut with given name and path, throws NoSuchFileException if path does not lead
    // to an executable file
    public AppShortcut(String name, String path) throws NoSuchFileException {
        this.name = name;
        setPath(path);
    }

    // EFFECTS: sets this path if it is valid and of extension .exe, otherwise throws NoSuchFileException
    @Override
    public void setPath(String path) throws NoSuchFileException {
        FilePath tempAppShortcut = new FilePath(name, path);

        if (tempAppShortcut.getFileExtension().equals("exe")) {
            super.setPath(path);
        } else {
            throw new NoSuchFileException(path);
        }
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.APP;
    }
}
