package model;

import model.exception.FailedToOpenException;
import model.exception.SystemNotSupportedException;

import java.net.MalformedURLException;
import java.nio.file.NoSuchFileException;

// Represents a "resource" within a space, ie. some relevant app, link, file, etc.
public interface Resource {

    public enum ResourceType {
        LINK,
        FILE,
        APP
    }

    // EFFECTS: attempts to launch resource
    //          if system does not support launching this resource, throws SystemNotSupportedException
    //          if resource fails to launch, throws FailedToOpenException
    //          otherwise, opens resource in appropriate application
    public void launch() throws SystemNotSupportedException, FailedToOpenException;

    // MODIFIES: this
    // EFFECTS: attempts to set this resource's path to the given string
    //          if path is invalid for resource type, throws appropriate exception
    public void setPath(String path) throws MalformedURLException, NoSuchFileException;

    //getters
    public String getName();

    public String getPath();

    public ResourceType getResourceType();
}
