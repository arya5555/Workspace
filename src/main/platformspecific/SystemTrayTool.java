package platformspecific;

import model.exception.SystemNotSupportedException;

import java.awt.*;

public class SystemTrayTool {
    private TrayIcon trayIcon;
    private SystemTray tray;

    // EFFECTS: if tray icon is not supported on platform, throws SystemNotSupportedException
    //          otherwise, initializes notification tool and loads images
    public SystemTrayTool(Image icon) throws SystemNotSupportedException {
        if (SystemTray.isSupported()) {
            tray = SystemTray.getSystemTray();
            trayIcon = new TrayIcon(icon);
            int trayIconWidth = trayIcon.getSize().width;
            trayIcon = new TrayIcon(icon.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH));
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                throw new SystemNotSupportedException();
            }
        } else {
            throw new SystemNotSupportedException();
        }
    }

    // EFFECTS: if system tray is not supported, does nothing
    //          otherwise displays popup message
    // source: https://stackoverflow.com/questions/34490218/how-to-make-a-windows-notification-in-java
    public void showPopup(String title, String message) {
        trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
    }

    // MODIFIES: this
    // EFFECTS: changes hover-over tooltip for tray icon
    public void changeTooltip(String tooltip) {
        trayIcon.setToolTip(tooltip);
    }

    // MODIFIES: this
    // EFFECTS: removes tray icon from system tray
    public void deleteTrayIcon() {
        tray.remove(trayIcon);
    }
}
