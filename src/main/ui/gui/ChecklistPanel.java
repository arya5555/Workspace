package ui.gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ChecklistPanel extends JPanel implements GuiComponent {
    private static final Color PANEL_COLOUR = MAIN_COLOUR;
    private static final String NORMAL_TOOLBAR = "Normal";
    private static final String DELETE_TOOLBAR = "Delete";
    private static final String CANCEL = "Done";

    private Set<JCheckBox> checkBoxes;
    private JPanel toolbar;
    private CardLayout toolbarLayout;
    private JPanel listPanel;
    private ChecklistListener checklistListener;
    private Boolean deleteMode;

    // EFFECTS: creates checklist panel with given title as header and populates it with checklist
    //          when not in delete mode, has given toolbar
    ChecklistPanel(String title, Collection<String> checklist, JPanel normalToolbar,
                   ChecklistListener checklistListener) {
        super(new BorderLayout());
        this.checklistListener = checklistListener;
        checkBoxes = new HashSet<>();
        deleteMode = false;

        init(title, checklist, normalToolbar);
    }

    // MODIFIES: this
    // EFFECTS: sets up checklist panel
    private void init(String title, Collection<String> checklist, JPanel normalToolbar) {
        GuiFrame.formatPanel(this);
        setBackground(PANEL_COLOUR);
        setBorder(new LineBorder(PANEL_COLOUR, MARGIN));
        setMinimumSize(new Dimension(0,0));

        add(createHeaderLabel(title), BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(PANEL_COLOUR);
        populateList(checklist);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
        add(createToolbar(normalToolbar), BorderLayout.SOUTH);
        setDeleteMode(false);
    }

    // MODIFIES: this
    // EFFECTS: fills list, each string in checklist is the text for one checkbox
    private void populateList(Collection<String> checklist) {
        listPanel.removeAll();

        for (String text : checklist) {
            addElement(text);
        }
    }

    // EFFECTS: creates header label with given text
    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(LARGE_BOLD_FONT);
        label.setForeground(WHITE);
        return label;
    }

    // EFFECTS: creates and returns toolbar with buttons for panel
    //          creates its own toolbar for when deleting
    private JPanel createToolbar(JPanel normalToolbar) {
        JPanel deleteToolbar = createDeleteToolbar();

        toolbar = GuiFrame.formatPanel(new JPanel());
        toolbarLayout = new CardLayout();
        toolbar.setLayout(toolbarLayout);
        toolbar.add(deleteToolbar, DELETE_TOOLBAR);
        toolbar.add(normalToolbar, NORMAL_TOOLBAR);

        toolbar.setVisible(true);
        toolbar.setOpaque(true);

        return toolbar;
    }

    // EFFECTS: creates and returns toolbar with buttons for resource pane in delete mode
    private JPanel createDeleteToolbar() {
        JPanel resourceToolbar = new JPanel();
        resourceToolbar.setLayout(new FlowLayout(FlowLayout.CENTER, MARGIN, MARGIN));
        resourceToolbar.setBackground(PANEL_COLOUR);

        JLabel label = new JLabel("Choose item to delete");
        label.setFont(MEDIUM_FONT);
        label.setForeground(WHITE);
        resourceToolbar.add(label);

        JButton delButton = new FormattedJButton(CANCEL, SMALL_BTN_COLOUR, SMALL_BTN_FONT);
        delButton.setActionCommand(CANCEL);
        delButton.addActionListener(this::actionPerformed);
        resourceToolbar.add(delButton);

        return resourceToolbar;
    }

    // MODIFIES: this
    // EFFECTS: if setDeleteTaskMode is true, sets each task background to red, and clicking a task will delete it
    //          otherwise, sets task backgrounds to normal and clicking a task has no effect
    private void setListDeleteMode(Boolean deleteMode) {
        this.deleteMode = deleteMode;
        if (deleteMode) {
            listPanel.setBackground(ACCENT_2);
            for (JCheckBox checkBox : checkBoxes) {
                checkBox.setEnabled(false);
            }
        } else {
            listPanel.setBackground(MAIN_COLOUR);
            for (JCheckBox checkBox : checkBoxes) {
                checkBox.setEnabled(true);
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: adds a checkbox with the given text string
    public void addElement(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        formatCheckBox(checkBox);
        checkBox.addItemListener(new CheckboxItemListener(checkBox));
        checkBox.addMouseListener(new CheckboxMouseAdapter(checkBox));
        checkBoxes.add(checkBox);
        listPanel.add(checkBox);
    }

    // MODIFIES: this
    // EFFECTS: if boolean deleteMode is true, enters delete resource mode
    //          otherwise, enters normal resource mode
    public void setDeleteMode(Boolean deleteMode) {
        setListDeleteMode(deleteMode);
        if (deleteMode) {
            toolbarLayout.show(toolbar, DELETE_TOOLBAR);
        } else {
            toolbarLayout.show(toolbar, NORMAL_TOOLBAR);
        }
    }

    // EFFECTS: when done button is selected, exits delete mode
    private void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(CANCEL)) {
            setDeleteMode(false);
        }
    }

    // MODIFIES: checkBox
    // EFFECTS: formats a checkbox
    private void formatCheckBox(JCheckBox checkBox) {
        checkBox.setContentAreaFilled(false);
        checkBox.setForeground(WHITE);
        checkBox.setMinimumSize(new Dimension(0, 20));
        checkBox.setFont(MEDIUM_FONT);
    }

    // MODIFIES: this
    // EFFECTS: removes all checkboxes that are currently selected
    public void deleteSelected() {
        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                listPanel.remove(checkBox);
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: sets the checkbox with given text as selected
    public void setSelected(String text) {
        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.getText().equals(text)) {
                checkBox.setSelected(true);
            }
        }
    }

    // getters
    public Collection<JCheckBox> getCheckBoxes() {
        return checkBoxes;
    }

    private class CheckboxMouseAdapter extends MouseAdapter {
        JCheckBox checkBox;

        // EFFECTS: creates new DeleteModeMouseAdapter with given checkbox
        CheckboxMouseAdapter(JCheckBox checkBox) {
            this.checkBox = checkBox;
        }

        // MODIFIES: this
        // EFFECTS: removes clicked checkbox from checklist, calls given delete handler
        @Override
        public void mousePressed(MouseEvent e) {
            if (deleteMode) {
                checkBox.setFocusable(false);
                listPanel.remove(checkBox);
                checklistListener.elementDeleted(checkBox.getText());
            }
        }

        // MODIFIES: this
        // EFFECTS: gives checkbox focus when hovered over
        @Override
        public void mouseEntered(MouseEvent e) {
            checkBox.requestFocus();
        }
    }

    private class CheckboxItemListener implements ItemListener {
        JCheckBox checkBox;

        CheckboxItemListener(JCheckBox checkBox) {
            this.checkBox = checkBox;
        }

        // EFFECTS: calls listener provided in ChecklistPanel constructor for selected element
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (checkBox.isSelected()) {
                checklistListener.elementSelected(checkBox);
            }
        }
    }
}
