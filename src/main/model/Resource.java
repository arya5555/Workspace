package model;

import java.net.MalformedURLException;

public interface Resource {

    public boolean launch();

    //getters
    public String getName();

    public String getPath();

    //setters
    public void setPath(String path) throws Exception;
}
