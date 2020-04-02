package ui.gui;

import model.*;
import model.exception.CancelledException;
import model.exception.FailedToOpenException;
import model.exception.SystemNotSupportedException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

// Graphic interface for a space, within a Workspace app
public class SpaceGUI implements GuiComponent {

    // Button names/tooltips and action commands
    private static final String OPEN_SELECTED_RESOURCES = "Open selected";
    private static final String OPEN_ALL_RESOURCES = "Open all";
    private static final String ADD_RESOURCE = "Add resource";
    private static final String DELETE_RESOURCE = "Delete resource";
    private static final String DELETE_COMPLETED_TASKS = "Delete completed";
    private static final String ADD_TASK = "Add task";
    private static final String DELETE_TASK = "Delete task";

    private static final Color PANEL_COLOUR = MAIN_COLOUR;

    private Space space;
    private GuiFrame guiFrame;
    private ChecklistPanel taskPanel;
    private ChecklistPanel resourcePanel;
    private TimerPanel timerPanel;

    // EFFECTS: creates new gui frame for space and displays space info
    public SpaceGUI(Space space, GuiFrame parent) {
        this.space = space;
        guiFrame = new GuiFrame("Workspace");

        init(parent);
    }

    // MODIFIES: this
    // EFFECTS: populates frame with components
    private void init(GuiFrame parent) {
        guiFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parent.refresh();
            }
        });
        addSplitPanes();
    }

    // MODIFIES: this
    // EFFECTS: adds split panes including timer, resources, and tasks to frame
    private void addSplitPanes() {
        JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createResourcePanel(),
                createTaskPanel());
        splitPane1.setDividerLocation((int) (WIDTH / 2));

        JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createTimerPanel(), splitPane1);
        splitPane2.setDividerLocation((int) HEIGHT / 4);

        guiFrame.getContentPane().add(splitPane2, BorderLayout.CENTER);
    }

    // EFFECTS: creates and returns resource panel with list of resources
    private JPanel createResourcePanel() {
        resourcePanel = new ChecklistPanel("Resources", space.getAllResourceNames(),
                createNormalResourceToolbar(),
                new ChecklistListener() {
                    @Override
                    public void elementDeleted(String name) {
                        space.removeResource(name);
                        guiFrame.refresh();
                    }

                    @Override
                    public void elementSelected(JCheckBox checkBox) {
                        // do nothing
                    }
                });

        return resourcePanel;
    }

    // EFFECTS: creates and returns toolbar with buttons for resource pane in normal mode
    private JPanel createNormalResourceToolbar() {
        JPanel resourceToolbar = new JPanel();
        resourceToolbar.setLayout(new FlowLayout(FlowLayout.CENTER, MARGIN, MARGIN));
        resourceToolbar.setBackground(PANEL_COLOUR);

        FormattedJButton openSelected = new FormattedJButton(OPEN_SELECTED_RESOURCES, SMALL_BTN_COLOUR, SMALL_BTN_FONT);
        openSelected.prepareButton(null, OPEN_SELECTED_RESOURCES, this::actionPerformed);
        resourceToolbar.add(openSelected);

        FormattedJButton openAll = new FormattedJButton(OPEN_ALL_RESOURCES, SMALL_BTN_COLOUR, SMALL_BTN_FONT);
        openAll.prepareButton(null, OPEN_ALL_RESOURCES, this::actionPerformed);
        resourceToolbar.add(openAll);

        FormattedJButton addButton = new FormattedJButton("+", SMALL_BTN_COLOUR, SMALL_BTN_FONT);
        addButton.prepareButton(ADD_RESOURCE, ADD_RESOURCE, this::actionPerformed);
        resourceToolbar.add(addButton);

        FormattedJButton delButton = new FormattedJButton("-", SMALL_BTN_COLOUR, SMALL_BTN_FONT);
        delButton.prepareButton(DELETE_RESOURCE, DELETE_RESOURCE, this::actionPerformed);
        resourceToolbar.add(delButton);

        return resourceToolbar;
    }

    // EFFECTS: creates and returns task panel with list of tasks
    private JPanel createTaskPanel() {
        taskPanel = new ChecklistPanel("To-Do", space.getTodo().getAllTaskDescriptions(),
                createNormalTaskToolbar(),
                new ChecklistListener() {
                    @Override
                    public void elementDeleted(String name) {
                        space.getTodo().removeTask(name);
                        guiFrame.refresh();
                    }

                    @Override
                    public void elementSelected(JCheckBox checkBox) {
                        checkBox.setEnabled(false);
                        checkBox.setForeground(new Color(230, 230, 230));
                        space.getTodo().completeTask(checkBox.getText());
                    }
                });

        for (Task t : space.getTodo()) {
            if (t.getComplete()) {
                taskPanel.setSelected(t.getDescription());
            }
        }
        return taskPanel;
    }

    // MODIFIES: this
    // EFFECTS: deletes all tasks whose check-boxes are selected
    private void deleteSelectedTasks() {
        space.getTodo().deleteCompletedTasks();
        taskPanel.deleteSelected();
        guiFrame.refresh();
    }

    // EFFECTS: creates and returns toolbar for task pane in normal mode
    private JPanel createNormalTaskToolbar() {
        JPanel taskToolbar = new JPanel();
        taskToolbar.setLayout(new FlowLayout(FlowLayout.CENTER, MARGIN, MARGIN));
        taskToolbar.setBackground(PANEL_COLOUR);

        FormattedJButton deleteSelectedButton = new FormattedJButton(DELETE_COMPLETED_TASKS, SMALL_BTN_COLOUR,
                SMALL_BTN_FONT);
        deleteSelectedButton.prepareButton(null, DELETE_COMPLETED_TASKS, this::actionPerformed);
        taskToolbar.add(deleteSelectedButton);

        FormattedJButton addButton = new FormattedJButton("+", SMALL_BTN_COLOUR, SMALL_BTN_FONT);
        addButton.prepareButton(ADD_TASK, ADD_TASK, this::actionPerformed);
        taskToolbar.add(addButton);

        FormattedJButton delButton = new FormattedJButton("-", SMALL_BTN_COLOUR, SMALL_BTN_FONT);
        delButton.prepareButton(DELETE_TASK, DELETE_TASK, this::actionPerformed);
        taskToolbar.add(delButton);

        return taskToolbar;
    }

    // EFFECTS: creates and returns timer panel with timer
    private JPanel createTimerPanel() {
        timerPanel = new TimerPanel(space.getName());
        return timerPanel;
    }

    // MODIFIES: this
    // EFFECTS: gets user input to add a new task to this space
    private void addTask() {
        String taskDescription;

        try {
            taskDescription = GuiFrame.popupTextField("New task:");
        } catch (CancelledException e) {
            return;
        }

        space.getTodo().addTask(new Task(taskDescription));
        taskPanel.addElement(taskDescription);
        guiFrame.refresh();
    }

    // MODIFIES: this
    // EFFECTS: gets user input and adds a new resource to this space
    private void addResourceDialog() {
        String[] resourceTypes = new String[] {Resource.ResourceType.LINK.toString(),
                Resource.ResourceType.FILE.toString(),
                Resource.ResourceType.APP.toString()};
        JComboBox<String> resourceType = new JComboBox<>(resourceTypes);
        JTextField nameField = new JTextField();
        JTextField pathField = new JTextField();
        JButton fileChooserButton = new JButton("Open file chooser");
        JPanel panel = setupResourceDialog(resourceType, nameField, pathField, fileChooserButton);

        int result = JOptionPane.showConfirmDialog(guiFrame, panel,
                "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            addResource((String) resourceType.getSelectedItem(), nameField.getText(), pathField.getText());
        }
    }

    // EFFECTS: creates a fileChooser dialog to load a resource
    //          returns the file, or null if user cancels
    private File resourceFileChooser(Boolean exeFilesOnly) {
        JFileChooser fileChooser = new JFileChooser();
        if (exeFilesOnly) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("EXE FILES", "exe");
            fileChooser.setFileFilter(filter);
        }

        int result = fileChooser.showOpenDialog(guiFrame);

        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        } else {
            return null;
        }
    }

    // MODIFIES: JComboBox resourceType
    // EFFECTS: sets up panel for adding a new resource with components and listeners
    public JPanel setupResourceDialog(JComboBox<String> resourceType, JTextField nameField, JTextField pathField,
                                      JButton fileChooserButton) {
        JPanel panel = createResourceDialogPanel(resourceType, nameField, pathField, fileChooserButton);

        resourceType.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (!resourceType.getSelectedItem().equals(Resource.ResourceType.LINK.toString())) {
                    fileChooserButton.setEnabled(true);
                } else {
                    fileChooserButton.setEnabled(false);
                }
            }
        });

        fileChooserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Boolean exeFilesOnly = resourceType.getSelectedItem().equals(Resource.ResourceType.APP.toString());
                File file = resourceFileChooser(exeFilesOnly);
                if (file != null) {
                    pathField.setText(file.getAbsolutePath());
                    nameField.setText(file.getName());
                }
            }
        });

        return panel;
    }

    // MODIFIES: JComboBox resourceType
    // EFFECTS: creates and returns panel for add resource dialog
    private JPanel createResourceDialogPanel(JComboBox<String> resourceType, JTextField nameField, JTextField pathField,
                                             JButton fileChooserButton) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Resource type:"));
        panel.add(resourceType);
        panel.add(Box.createRigidArea(new Dimension(0, MARGIN)));
        panel.add(new JLabel("Resource name:"));
        panel.add(nameField);
        panel.add(Box.createRigidArea(new Dimension(0, MARGIN)));
        panel.add(new JLabel("Resource path (link/filepath):"));
        panel.add(pathField);
        panel.add(Box.createRigidArea(new Dimension(0, MARGIN)));
        panel.add(fileChooserButton);
        fileChooserButton.setEnabled(false);

        return panel;
    }

    // REQUIRES: type is one of the possible Resource.ResourceTypes
    // MODIFIES: this
    // EFFECTS: attempts to add a new resource of given type, name, and path.
    //          if resource is not valid, displays an error message
    private void addResource(String type, String name, String path) {
        try {
            Resource r;
            if (type.equals(Resource.ResourceType.LINK.toString())) {
                r = new WebsiteLink(name, path);
            } else if (type.equals(Resource.ResourceType.APP.toString())) {
                r = new AppShortcut(name, path);
            } else {
                r = new FilePath(name, path);
            }
            space.addResource(r);
            resourcePanel.addElement(r.getName());
            guiFrame.refresh();
        } catch (Exception e) {
            guiFrame.displayMessage("Failed to create resource.");
        }
    }

    // MODIFIES: this
    // EFFECTS: processes button clicks
    private void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(OPEN_SELECTED_RESOURCES)) {
            openSelectedResources();
        } else if (e.getActionCommand().equals(OPEN_ALL_RESOURCES)) {
            openAllResources();
        } else if (e.getActionCommand().equals(ADD_RESOURCE)) {
            addResourceDialog();
        } else if (e.getActionCommand().equals(DELETE_RESOURCE)) {
            resourcePanel.setDeleteMode(true);
        } else if (e.getActionCommand().equals(DELETE_COMPLETED_TASKS)) {
            deleteSelectedTasks();
        } else if (e.getActionCommand().equals(ADD_TASK)) {
            addTask();
        } else if (e.getActionCommand().equals(DELETE_TASK)) {
            taskPanel.setDeleteMode(true);
        }
    }

    // EFFECTS: opens resources whose checkboxes are selected, or displays popup error message if unable
    private void openSelectedResources() {
        for (JCheckBox resourceCheckBox : resourcePanel.getCheckBoxes()) {
            if (resourceCheckBox.isSelected()) {
                try {
                    space.getResourceOfName(resourceCheckBox.getText()).launch();
                } catch (SystemNotSupportedException e) {
                    GuiFrame.displayMessage("System does not support launching resources.");
                } catch (FailedToOpenException ee) {
                    GuiFrame.displayMessage("One or more resources failed to launch.");
                }
            }
        }
    }

    // EFFECTS: opens all, or displays popup error message if unable
    private void openAllResources() {
        try {
            space.launchAllResources();
        } catch (SystemNotSupportedException e) {
            GuiFrame.displayMessage("System does not support launching resources.");
        } catch (FailedToOpenException ee) {
            GuiFrame.displayMessage("One or more resources failed to launch.");
        }
    }
}
