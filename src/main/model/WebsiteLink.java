package model;

import model.exception.FailedToOpenException;
import model.exception.SystemNotSupportedException;
import platformspecific.ResourceLauncher;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

// Represents a website link with a name and URL
public class WebsiteLink implements Resource {
    // source for valid url characters:
    // https://stackoverflow.com/questions/1547899/which-characters-make-a-url-invalid/1547940#1547940
    private static Set<Character> VALID_URL_CHARS = new HashSet<>(
            Arrays.asList('-','.','~','_',':','/','?','#','[',']','@',
            '!','$','&','\'','(',')','*','+',',',';','%','='));
    private String name;
    private URL url;

    // EFFECTS: initializes object with given name and url, throws MalformedURLException if url is not valid
    public WebsiteLink(String name, String url) throws MalformedURLException {
        this.name = name;
        setPath(url);
    }

    // EFFECTS: if system does not support launching websites, throws SystemNotSupportedException
    //          if website fails to launch, throws FailedToOpenException
    //          otherwise, opens url in default browser
    @Override
    public void launch() throws SystemNotSupportedException, FailedToOpenException {
        ResourceLauncher.openSite(url);
    }

    // EFFECTS: returns true if url contains only valid characters and sets this url,
    // otherwise throws exception and doesn't change this url
    @Override
    public void setPath(String url) throws MalformedURLException {
        if (!(url.length() >= 7 && url.substring(0,7).equals("http://"))
                && !(url.length() >= 8 && url.substring(0,8).equals("https://"))) {
            url = "http://" + url;
        }

        for (char c : url.toCharArray()) {
            if (!VALID_URL_CHARS.contains(c) && !Character.isLetterOrDigit(c)) {
                throw new MalformedURLException("Contains invalid character " + c);
            }
        }

        this.url = new URL(url);
    }

    //getters
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPath() {
        return url.toString();
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.LINK;
    }
}
