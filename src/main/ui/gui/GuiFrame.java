package ui.gui;

import model.exception.CancelledException;

import javax.swing.*;
import java.awt.*;

public class GuiFrame extends JFrame implements GuiComponent {
    private int width;
    private int height;

    // EFFECTS: Creates, formats, and displays GUI frame with given title and default width and height
    GuiFrame(String title) {
        this(title, GuiComponent.WIDTH, GuiComponent.HEIGHT);
    }

    // EFFECTS: Creates, formats, and displays GUI frame with given title, width, and height
    GuiFrame(String title, int width, int height) {
        super(title);
        this.width = width;
        this.height = height;
        setUIAppearance();
        initWindow();
        refresh();
    }
    
    // EFFECTS: refreshes gui
    public void refresh() {
        pack();
        repaint();
        setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS:  makes the JFrame window for this workspace app gui
    private void initWindow() {
        JPanel contentPane = formatPanel(new JPanel());
        contentPane.setBorder(BorderFactory.createEmptyBorder(MARGIN,MARGIN,MARGIN,MARGIN));
        contentPane.setLayout(new BorderLayout(MARGIN, MARGIN));
        setContentPane(contentPane);
        setMinimumSize(new Dimension(width, height));
        setLocationRelativeTo(null);
        setVisible(true);
        setUIAppearance();
    }

    // MODIFIES: JFrame ui
    // EFFECTS: Set fonts and appearance preferences
    private void setUIAppearance() {
        UIManager.put("OptionPane.buttonFont", GENERAL_FONT);
        UIManager.put("OptionPane.messageFont", GENERAL_FONT);
        UIManager.put("OptionPane.font", GENERAL_FONT);
        UIManager.put("TextField.font", GENERAL_FONT);
        UIManager.put("ToolTip.background", ACCENT_1);
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // No huge error, only means look will be different
        }
    }

    // MODIFIES: panel
    // EFFECTS: returns panel formatted with background colour
    public static JPanel formatPanel(JPanel panel) {
        panel.setOpaque(true);
        panel.setVisible(true);
        panel.setBackground(BACKGROUND);
        return panel;
    }

    // EFFECTS: displays a popup with the given message
    protected void displayMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    // EFFECTS: creates a popup and returns a string with user response to given String question
    //          or throws CancelledException if the user clicks cancel
    public String popupTextField(String question) throws CancelledException {
        String input = JOptionPane.showInputDialog(this, question, "", JOptionPane.PLAIN_MESSAGE);

        if (input == null || input.equals("")) {
            throw new CancelledException();
        } else {
            return input;
        }
    }

    // EFFECTS: creates a popup with given String question and yes/no options
    //          returns true if user clicks YES_BUTTON, false if user selects NO_BUTTON
    public boolean popupQuestion(String question) {
        int selection = JOptionPane.showConfirmDialog(this, question, "", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        return  (selection == JOptionPane.OK_OPTION);
    }
}
