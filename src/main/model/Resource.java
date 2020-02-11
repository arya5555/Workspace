package model;

import java.net.MalformedURLException;

public interface Resource {
    //getters
    public String getName();

    public String getPath();

    public String getResourceType();

    //setters
    public void setPath(String path) throws Exception;
}
