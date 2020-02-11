package model;

import model.exception.FailedToOpenException;
import model.exception.SystemNotSupportedException;

import java.net.MalformedURLException;

public interface Resource {

    public void launch() throws SystemNotSupportedException, FailedToOpenException;

    //getters
    public String getName();

    public String getPath();

    public String getResourceType();

    //setters
    public void setPath(String path) throws Exception;
}
