package model;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// represents a website with a name and URL
public class WebsiteLink implements Resource {
    private static List<Character> VALID_URL_CHARS;
    private String name;
    private URL url;

    // EFFECTS: initializes object with given name and url, throws exception if url is not valid

    // source for valid url characters:
    // https://stackoverflow.com/questions/1547899/which-characters-make-a-url-invalid/1547940#1547940
    public WebsiteLink(String name, String url) throws Exception {
        VALID_URL_CHARS = new ArrayList<>(
                Arrays.asList('-','.','~','_',':','/','?','#','[',']','@',
                        '!','$','&','\'','(',')','*','+',',',';','%','='));
        this.name = name;
        setPath(url);
    }

    // EFFECTS: Opens link in default browser, returns true if successful
    // or false if there is an error

    // source for how to open link in browser:
    // https://stackoverflow.com/questions/10967451/open-a-link-in-browser-with-java-button
    @Override
    public boolean launch() {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(this.url.toURI());
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        return false;
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

    //setters

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
}
