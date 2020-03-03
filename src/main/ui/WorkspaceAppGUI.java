package ui;

import model.WorkspaceApp;
import model.exception.InvalidFormatException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

// Graphic interface for a workspace app
// Source: SimpleDrawingPlayer app
public class WorkspaceAppGUI extends JFrame {
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 700;

    WorkspaceApp workspace;

    public WorkspaceAppGUI() {
        super("Workspace");

        workspace = new WorkspaceApp();
        initWorkspace();

        initWindow();
    }

    // MODIFIES: this
    // EFFECTS:  makes the JFrame window for this workspace app gui
    private void initWindow() {
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: lists this workspace's spaces
    private void listSpaces() {
        JPanel toolArea = new JPanel();
        toolArea.setLayout(new GridLayout(0,1));
        toolArea.setSize(new Dimension(0, 0));
        add(toolArea, BorderLayout.SOUTH);
    }

    // MODIFIES: this
    // EFFECTS: 
    private void initWorkspace() {
        try {
            loadSpaces();
        } catch (IOException e) {
            System.out.println("Could not find previous save file " + WORKSPACE_FILE);
            System.out.println("No saved spaces were loaded.");
        } catch (InvalidFormatException e) {
            System.out.println("Save file " + WORKSPACE_FILE
                    + " was found, but contents were not in the correct format");
            System.out.println("No saved spaces were loaded.");
        }
    }
}
