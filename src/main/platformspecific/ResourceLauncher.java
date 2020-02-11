package platformspecific;

import model.exception.FailedToOpenException;
import model.exception.SystemNotSupportedException;

import java.awt.*;
import java.io.File;
import java.net.URL;

// On supported platforms, launches external applications
public class ResourceLauncher {

    // EFFECTS: if desktop is supported, opens given file and returns true if successful
    // or prints error if desktop is not supported or file can't be opened
    public static void openFile(File file) throws FailedToOpenException, SystemNotSupportedException {
        Desktop desktop;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.open(file);
                } catch (Exception e) {
                    throw new FailedToOpenException();
                }
            }
        } else {
            throw new SystemNotSupportedException();
        }
    }

    /// EFFECTS: if desktop is supported, opens given site and returns true if successful
    // or prints error if desktop is not supported
    public static void openSite(URL url) throws SystemNotSupportedException, FailedToOpenException {
        Desktop desktop;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.OPEN)) {
                try {
                    desktop.browse(url.toURI());
                    return;
                } catch (Exception e) {
                    throw new FailedToOpenException();
                }
            }
        }

        throw new SystemNotSupportedException();
    }

    // EFFECTS: returns true if desktop is supported and it supports browsing and opening files
    //          otherwise returns false
    public static boolean isDesktopSupported() {
        Desktop desktop;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            return desktop.isSupported(Desktop.Action.BROWSE) && desktop.isSupported(Desktop.Action.OPEN);
        }

        return false;
    }
}
