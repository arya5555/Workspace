package ui.gui;

import model.Account;
import model.exception.FailedToGetAccountException;
import model.exception.InvalidAccountException;
import model.exception.UsernameAlreadyExistsException;
import network.DatabaseTool;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.Observable;

// a menu bar for a workspace app; UI for data backups and retrieval
public class WorkspaceMenuBar extends Observable implements GuiComponent {
    private JMenuBar menuBar;
    private JMenu saveMenu;
    private JMenu loadMenu;
    private JMenuItem backup;
    private JMenuItem restore;
    private JMenuItem load;
    private JMenuItem save;
    JFrame parentFrame;

    // EFFECTS: constructs MenuBar for workspace
    WorkspaceMenuBar(JFrame parentFrame) {
        super();
        this.parentFrame = parentFrame;
        menuBar = new JMenuBar();
        saveMenu = new JMenu("Save");
        loadMenu = new JMenu("Load");
        save = new JMenuItem("Save locally");
        backup = new JMenuItem("Backup online");
        load = new JMenuItem("Load local save");
        restore = new JMenuItem("Restore online backup");
        backup.addActionListener(this::actionPerformed);
        restore.addActionListener(this::actionPerformed);
        load.addActionListener(this::actionPerformed);
        save.addActionListener(this::actionPerformed);

        saveMenu.add(save);
        saveMenu.add(backup);
        loadMenu.add(load);
        loadMenu.add(restore);

        menuBar.add(saveMenu);
        JLabel separator = new JLabel();
        separator.setPreferredSize(new Dimension(MARGIN, MARGIN));
        menuBar.add(separator);
        menuBar.add(loadMenu);
    }

    // MODIFIES: this
    // EFFECTS: processes clicks on menu items
    private void actionPerformed(ActionEvent e) {
        setChanged();
        if (e.getSource() == backup) {
            try {
                notifyObservers(new MenuBarEvent(MenuBarEvent.EventType.SAVE_SPACES,
                        null));
                setChanged();
                notifyObservers(new MenuBarEvent(MenuBarEvent.EventType.BACKUP_DATA,
                        getAccount()));
            } catch (FailedToGetAccountException ex) {
                // No message required
            }
        } else if (e.getSource() == restore) {
            try {
                notifyObservers(new MenuBarEvent(MenuBarEvent.EventType.RESTORE_BACKUP,
                        getAccount()));
            } catch (FailedToGetAccountException ex) {
                // No message required
            }
        } else if (e.getSource() == load) {
            loadSaveFile();
        } else if (e.getSource() == save) {
            notifyObservers(new MenuBarEvent(MenuBarEvent.EventType.SAVE_SPACES,
                    null));
        }
    }

    // EFFECTS: if user cancels, or sign-in info is invalid, throws FailedToGetAccountException
    //          otherwise, gets user to sign in or create an account, and returns that account
    private Account getAccount() throws FailedToGetAccountException {
        String[] options = {"Sign in", "Create an account"};

        int result = JOptionPane.showOptionDialog(parentFrame, "Sign in or create a new account",
                "", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (result == 0) {
            return createSignInDialog();
        } else if (result == 1) {
            return createSignUpDialog();
        } else {
            throw new FailedToGetAccountException();
        }
    }

    // EFFECTS: if account is not retrieved, throws FailedToGetAccountException
    //          otherwise, provides dialog to sign in to account
    private Account createSignInDialog() throws FailedToGetAccountException {
        JTextField nameField = new JTextField();
        JTextField passField = new JTextField();

        JPanel panel = createSignInPanel(nameField, passField);

        int result = JOptionPane.showConfirmDialog(parentFrame, panel, "Sign in",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            return signIn(nameField.getText(), passField.getText());
        } else {
            throw new FailedToGetAccountException();
        }
    }

    // EFFECTS: if connection to database can't be made, displays error message and throws FailedToGetAccountException
    //          if username and password are not valid, displays error message and throws FailedToGetAccountException
    //          otherwise, gets account from database and returns it
    private Account signIn(String user, String pass) throws FailedToGetAccountException {
        DatabaseTool databaseTool;
        try {
            databaseTool = new DatabaseTool();
            return databaseTool.signIn(user, pass);
        } catch (SQLException e) {
            GuiFrame.displayMessage("Failed to connect to database. Do you have internet?");
            throw new FailedToGetAccountException();
        } catch (InvalidAccountException e) {
            GuiFrame.displayMessage("Invalid account. Username or password is incorrect.");
            throw new FailedToGetAccountException();
        }
    }

    // EFFECTS: if account is not retrieved, throws FailedToGetAccountException
    //          otherwise, provides dialog to sign in to account
    private Account createSignUpDialog() throws FailedToGetAccountException {
        JTextField nameField = new JTextField();
        JTextField passField = new JTextField();

        JPanel panel = createSignInPanel(nameField, passField);

        int result = JOptionPane.showConfirmDialog(parentFrame, panel, "Create an account",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            return signUp(nameField.getText(), passField.getText());
        } else {
            throw new FailedToGetAccountException();
        }
    }

    // EFFECTS: if connection to database can't be made, displays error message and throws FailedToGetAccountException
    //          otherwise, creates new account, stores it in database, and returns it
    private Account signUp(String user, String pass) throws FailedToGetAccountException {
        DatabaseTool databaseTool;
        try {
            databaseTool = new DatabaseTool();
            databaseTool.createAccount(user, pass);
            return databaseTool.signIn(user, pass);
        } catch (SQLException e) {
            GuiFrame.displayMessage("Failed to connect to database. Do you have internet?");
            throw new FailedToGetAccountException();
        } catch (UsernameAlreadyExistsException e) {
            GuiFrame.displayMessage("An account with this username already exists. Account was not created.");
            throw new FailedToGetAccountException();
        } catch (InvalidAccountException e) {
            // programming error
            throw new FailedToGetAccountException();
        }
    }

    // EFFECTS: returns panel for sign in / sign up dialog
    private JPanel createSignInPanel(JTextField nameField, JTextField passField) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Username:"));
        panel.add(nameField);
        panel.add(Box.createRigidArea(new Dimension(0, MARGIN)));
        panel.add(new JLabel("Password:"));
        panel.add(passField);
        panel.add(Box.createRigidArea(new Dimension(0, MARGIN)));

        return panel;
    }

    // MODIFIES: this
    // EFFECTS: prompts user to choose a local save file
    //          if user cancels, does nothing
    //          if file is invalid, displays error message
    //          otherwise, loads in the data
    private void loadSaveFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON FILES", "json");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(parentFrame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            setChanged();
            notifyObservers(new MenuBarEvent(MenuBarEvent.EventType.LOAD_SAVE_DATA,
                    file.getPath()));
        }
    }

    // getters
    public JMenuBar getMenuBar() {
        return menuBar;
    }
}
