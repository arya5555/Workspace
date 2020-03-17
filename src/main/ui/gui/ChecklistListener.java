package ui.gui;

import javax.swing.*;

public abstract class ChecklistListener {

    public abstract void elementDeleted(String name);

    public abstract void elementSelected(JCheckBox checkBox);
}
