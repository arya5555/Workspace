package ui.gui;

import model.Account;
import model.Space;
import model.exception.CancelledException;
import ui.WorkspaceAppUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

// Graphic interface for a workspace app
// Source: SimpleDrawingPlayer app
public class WorkspaceAppGUI extends WorkspaceAppUI implements GuiComponent, Observer {

    // Button names/tooltips and action commands
    private static final String ADD_SPACE_BUTTON = "Add space";
    private static final String DELETE_SPACE_BUTTON = "Delete space";
    private static final String CANCEL_BUTTON = "Done";

    private static final String DELETE_TOOLBAR_NAME = "delete";
    private static final String EDIT_TOOLBAR_NAME = "edit";

    private static final Font LABEL_FONT = LARGE_FONT;

    private GuiFrame guiFrame;
    private JPanel spacesPanel;
    private JPanel toolbar;
    private CardLayout toolbarLayout;
    private boolean deleteMode;

    public WorkspaceAppGUI() {
        super();
        guiFrame = new GuiFrame("Workspace");
        deleteMode = false;
        init();
    }

    // MODIFIES: this
    // EFFECTS: confirms that the user wants to delete the space, and deletes it
    //          if user cancels, does nothing
    public void deleteSpace(String name) {
        if (GuiFrame.popupQuestionOkCancel("Are you sure you want to delete " + name + "?")) {
            workspace.removeSpace(name);
        }
    }

    // MODIFIES: this
    // EFFECTS: initializes workspace and gui
    private void init() {
        WorkspaceMenuBar menuBar = new WorkspaceMenuBar(guiFrame);
        menuBar.addObserver(this);
        guiFrame.setJMenuBar(menuBar.getMenuBar());
        guiFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (GuiFrame.popupQuestionYesNo("Would you like to save your changes?")) {
                    saveSpaces();
                }
            }
        });

        loadSaveData();
        createSpacesPanel();
        createToolbar();
        enterNormalSpaceMode();
    }

    // MODIFIES: this
    // EFFECTS: displays this workspace's spaces as buttons on screen
    private void createSpacesPanel() {
        if (spacesPanel == null) {
            spacesPanel = GuiFrame.formatPanel(new JPanel());
            guiFrame.getContentPane().add(spacesPanel, BorderLayout.CENTER);
        } else {
            spacesPanel.removeAll();
        }

        spacesPanel.setLayout(new GridLayout(0, 1, 0, 10));

        for (Space s : workspace.getSpaces()) {
            JButton button = new FormattedJButton(s.getName());
            button.setToolTipText("Open " + s.getName());
            button.setActionCommand(s.getName());
            button.addActionListener(this::actionPerformed);
            spacesPanel.add(button);
        }

        if (deleteMode) {
            spaceButtonsDeleteMode();
        }
    }

    // MODIFIES: this
    // EFFECTS: updates space buttons to delete mode
    private void spaceButtonsDeleteMode() {
        for (Component c : spacesPanel.getComponents()) {
            JButton button = (JButton) c;

            String spaceName = button.getText();
            button.setToolTipText("Delete " + spaceName);
            button.setBackground(ACCENT_2);
        }
        guiFrame.refresh();
    }

    // MODIFIES: this
    // EFFECTS: updates space buttons to normal mode
    private void spaceButtonsNormalMode() {
        for (Component c : spacesPanel.getComponents()) {
            JButton button = (JButton) c;

            String spaceName = button.getText();
            button.setToolTipText("Open " + spaceName);
            button.setBackground(LARGE_BTN_COLOUR);
        }
        guiFrame.refresh();
    }

    // MODIFIES: this
    // EFFECTS: creates toolbar
    private void createToolbar() {
        JPanel editSpaceToolbar = createEditSpaceToolbar();
        JPanel deleteSpaceToolbar = createDeleteSpaceToolbar();

        toolbar = GuiFrame.formatPanel(new JPanel());
        toolbarLayout = new CardLayout();
        toolbar.setLayout(toolbarLayout);
        toolbar.add(deleteSpaceToolbar, DELETE_TOOLBAR_NAME);
        toolbar.add(editSpaceToolbar, EDIT_TOOLBAR_NAME);

        toolbar.setVisible(true);
        toolbar.setOpaque(true);

        guiFrame.getContentPane().add(toolbar, BorderLayout.SOUTH);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("hi"));
        guiFrame.add(panel, BorderLayout.EAST);
    }

    // MODIFIES: this
    // EFFECTS: creates and returns JPanel toolbar to add or remove spaces
    private JPanel createEditSpaceToolbar() {
        JPanel editSpaceToolbar = GuiFrame.formatPanel(new JPanel());
        editSpaceToolbar.setLayout(new FlowLayout(FlowLayout.RIGHT, MARGIN, MARGIN));

        JButton addButton = new FormattedJButton("+", SMALL_BTN_COLOUR);
        addButton.setToolTipText(ADD_SPACE_BUTTON);
        addButton.setActionCommand(ADD_SPACE_BUTTON);
        addButton.addActionListener(this::actionPerformed);
        editSpaceToolbar.add(addButton);

        JButton delButton = new FormattedJButton("-", SMALL_BTN_COLOUR);
        delButton.setToolTipText(DELETE_SPACE_BUTTON);
        delButton.setActionCommand(DELETE_SPACE_BUTTON);
        delButton.addActionListener(this::actionPerformed);
        editSpaceToolbar.add(delButton);

        editSpaceToolbar.setOpaque(true);
        editSpaceToolbar.setVisible(true);

        return editSpaceToolbar;
    }

    // MODIFIES: this
    // EFFECTS: creates and returns JPanel toolbar for delete space mode
    private JPanel createDeleteSpaceToolbar() {
        JPanel deleteSpaceToolbar = GuiFrame.formatPanel(new JPanel());
        deleteSpaceToolbar.setLayout(new FlowLayout(FlowLayout.RIGHT, MARGIN, MARGIN));

        JLabel label = new JLabel();
        label.setText("Choose the space to delete");
        label.setFont(LABEL_FONT);
        label.setForeground(ACCENT_2);

        deleteSpaceToolbar.add(label, FlowLayout.LEFT);
        JButton cancelButton = new FormattedJButton(CANCEL_BUTTON, SMALL_BTN_COLOUR);
        cancelButton.setActionCommand(CANCEL_BUTTON);
        cancelButton.addActionListener(this::actionPerformed);
        deleteSpaceToolbar.add(cancelButton);

        deleteSpaceToolbar.setOpaque(true);
        deleteSpaceToolbar.setVisible(true);

        return deleteSpaceToolbar;
    }

    // MODIFIES: this
    // EFFECTS: processes button clicks
    private void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(ADD_SPACE_BUTTON)) {
            addSpace();
        } else if (e.getActionCommand().equals(DELETE_SPACE_BUTTON)) {
            enterDeleteSpaceMode();
        } else if (e.getActionCommand().equals(CANCEL_BUTTON)) {
            enterNormalSpaceMode();
        } else {
            if (deleteMode) {
                deleteSpace(e.getActionCommand());
                createSpacesPanel();
                guiFrame.refresh();
            } else {
                guiFrame.setVisible(false);
                runSpace(workspace.getSpaceOfName(e.getActionCommand()));
            }
        }
    }

    // MODIFIES:
    // MODIFIES: this
    // EFFECTS: asks user for new space name and adds it, or does nothing if the user cancels
    private void addSpace() {
        String spaceName;

        try {
            spaceName = GuiFrame.popupTextField("Enter name of your new space");
        } catch (CancelledException e) {
            return;
        }

        workspace.addSpace(new Space(spaceName));
        refresh();
    }

    @Override
    public void refresh() {
        createSpacesPanel();
        guiFrame.refresh();
    }

    // MODIFIES: this
    // EFFECTS: lets user select a space to delete and deletes it, or does nothing if the user cancels
    private void enterDeleteSpaceMode() {
        spaceButtonsDeleteMode();
        toolbarLayout.show(toolbar, DELETE_TOOLBAR_NAME);
        deleteMode = true;
    }

    // MODIFIES: this
    // EFFECTS: if app is in delete space mode, returns to normal mode
    private void enterNormalSpaceMode() {
        spaceButtonsNormalMode();
        toolbarLayout.show(toolbar, EDIT_TOOLBAR_NAME);
        deleteMode = false;
    }

    @Override
    protected void displayMessage(String message) {
        GuiFrame.displayMessage(message);
    }

    @Override
    protected void runSpace(Space space) {
        new SpaceGUI(space, guiFrame);
    }

    // MODIFIES: this
    // EFFECTS: follows appropriate
    @Override
    public void update(Observable o, Object arg) {
        if (o.getClass().equals(WorkspaceMenuBar.class) && arg.getClass().equals(MenuBarEvent.class)) {
            MenuBarEvent argument = (MenuBarEvent) arg;
            if (argument.getEventType().equals(MenuBarEvent.EventType.SAVE_SPACES)) {
                saveSpaces();
            } else if (argument.getEventType().equals(MenuBarEvent.EventType.LOAD_SAVE_DATA)) {
                loadSaveData((String) argument.getObject());
            } else if (argument.getEventType().equals(MenuBarEvent.EventType.BACKUP_DATA)) {
                backupData((Account) argument.getObject());
            } else {
                restoreBackup((Account) argument.getObject());
            }
        }
    }

    // getters
    public JFrame getJFrame() {
        return guiFrame;
    }
}
