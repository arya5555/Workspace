package ui.gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

public class FormattedJButton extends JButton implements GuiComponent {
    Color background;
    Color accentColour;
    Font font;

    // EFFECTS: creates button formatted to fit app theme, with default background of MAIN_COLOUR
    FormattedJButton(String name) {
        this(name, LARGE_BTN_COLOUR);
    }

    // EFFECTS: creates button formatted with given background color
    FormattedJButton(String name, Color background) {
        this(name, background, LARGE_BTN_FONT);
    }

    // EFFECTS: creates button formatted with given background color and font
    FormattedJButton(String name, Color background, Font font) {
        this(name, background, ACCENT_1, font);
    }

    // EFFECTS: creates button formatted with given background colour, accent colour, and font
    FormattedJButton(String name, Color background, Color accentColour, Font font) {
        super("  " + name + "  ");
        this.background = background;
        this.font = font;
        this.accentColour = accentColour;
        initButton();
    }

    // MODIFIES: this
    // EFFECTS: gives the button new background color
    @Override
    public void setBackground(Color bg) {
        background = bg;
        super.setBackground(background);
        setBorder(createBorder(background));
    }

    // MODIFIES: this
    // EFFECTS: formats button with desired styles
    private void initButton() {
        setBorderPainted(true);
        setFocusPainted(true);
        setContentAreaFilled(false);
        setBackground(background);
        setForeground(BUTTON_FG);
        setFont(font);
        setBorder(createBorder(background));

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBorder(createBorder(accentColour));
                requestFocus();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBorder(createBorder(background));
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isPressed()) {
            g.setColor(accentColour);
        } else if (getModel().isRollover()) {
            g.setColor(background);
        } else {
            g.setColor(background);
        }
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    // MODIFIES: this
    // EFFECTS: adds given tooltip text, action command, and action listener to button
    public void prepareButton(String tooltip, String actionCommand, ActionListener listener) {
        setToolTipText(tooltip);
        setActionCommand(actionCommand);
        addActionListener(listener);
    }

    // EFFECTS: returns default formatted border with given color
    private Border createBorder(Color color) {
        return BorderFactory.createLineBorder(color, 2);
    }
}
