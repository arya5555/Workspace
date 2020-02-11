package ui.platformspecific;

import java.awt.*;

public class SystemTrayTool {
    private TrayIcon trayIcon;
    private boolean supported;
    private SystemTray tray;

    // EFFECTS: initializes notification tool and loads images
    //          if tray icon is not supported on platform, this NotificationTool
    //          will do nothing
    public SystemTrayTool(Image icon) {
        if (SystemTray.isSupported()) {
            tray = SystemTray.getSystemTray();
            trayIcon = new TrayIcon(icon);
            int trayIconWidth = trayIcon.getSize().width;
            trayIcon = new TrayIcon(icon.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH));
            try {
                tray.add(trayIcon);
                supported = true;
            } catch (AWTException e) {
                supported = false;
            }
        } else {
            supported = false;
        }
    }

    // EFFECTS: if system tray is not supported, does nothing
    //          otherwise displays popup message
    // source: https://stackoverflow.com/questions/34490218/how-to-make-a-windows-notification-in-java
    public void showPopup(String title, String message) {
        if (supported) {
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
        }
    }

    // MODIFIES: this
    // EFFECTS: changes hover-over tooltip for tray icon
    public void changeTooltip(String tooltip) {
        if (supported) {
            trayIcon.setToolTip(tooltip);
        }
    }

    // MODIFIES: this
    // EFFECTS: removes tray icon from system tray
    public void deleteTrayIcon() {
        if (supported) {
            tray.remove(trayIcon);
        }
    }
}
