package model;

import model.exception.FailedToOpenException;
import model.exception.SystemNotSupportedException;

import java.net.MalformedURLException;
import java.nio.file.NoSuchFileException;

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

    //getters
    public String getName();

    public String getPath();

    public ResourceType getResourceType();

    //setters
    public void setPath(String path) throws MalformedURLException, NoSuchFileException;
}
