package ui.platformspecific;

import model.Resource;

import java.awt.*;
import java.net.MalformedURLException;
import java.util.List;
import java.io.File;
import java.net.URL;

import static model.FilePath.FILE_RESOURCE_TYPE;

// On supported platforms, launches external applications
public class ResourceLauncher {

    // EFFECTS: attempts to launch resource
    public static void launch(Resource resource) {
        if (resource.getResourceType().equals(FILE_RESOURCE_TYPE)) {
            openFile(new File(resource.getPath()));
        } else {
            try {
                openSite(new URL(resource.getPath()));
            } catch (MalformedURLException e) {
                printFailedToOpen();
            }
        }
    }

    // EFFECTS: attempts to launch all resources in list
    public static void launch(List<Resource> resources) {
        for (Resource r: resources) {
            launch(r);
        }
    }

    // EFFECTS: if desktop is supported, opens given file and returns true if successful
    // or prints error if desktop is not supported or file can't be opened
    public static void openFile(File file) {
        Desktop desktop;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.open(file);
                    return;
                } catch (Exception e) {
                    printFailedToOpen();
                }
            }
        }

        printDesktopNotSupported();
    }

    /// EFFECTS: if desktop is supported, opens given site and returns true if successful
    // or prints error if desktop is not supported
    public static void openSite(URL url) {
        Desktop desktop;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(url.toURI());
                    return;
                } catch (Exception e) {
                    printFailedToOpen();
                }
            }
        }

        printDesktopNotSupported();
    }

    // EFFECTS:
    private static void printDesktopNotSupported() {
        System.out.println("Can't open resource, this desktop is not supported.");
    }

    // EFFECTS:
    private static void printFailedToOpen() {
        System.out.println("Resource couldn't be opened. Either it no longer exists or there is "
                + "no default application associated.");
    }
}
