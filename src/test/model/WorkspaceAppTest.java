package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WorkspaceAppTest {
    WorkspaceApp workspaceApp;

    @BeforeEach
    public void setUp() {
        workspaceApp = new WorkspaceApp();
    }

    @Test
    public void testConstructor() {
        assertEquals(0, workspaceApp.getSpaces().size());
    }

    @Test
    public void testAddSpace() {
        workspaceApp.addSpace(new Space("ENGLISH"));
        workspaceApp.addSpace(new Space("BIO"));

        assertEquals(2, workspaceApp.getSpaces().size());
        assertTrue(workspaceApp.getAllSpaceNames().contains("ENGLISH"));
        assertTrue(workspaceApp.getAllSpaceNames().contains("BIO"));
    }

    @Test
    public void testRemoveSpace() {
        workspaceApp.addSpace(new Space("ENGLISH"));
        workspaceApp.addSpace(new Space("BIO"));
        workspaceApp.removeSpace("ENGLISH");

        assertEquals(1, workspaceApp.getSpaces().size());
        assertTrue(workspaceApp.getAllSpaceNames().contains("BIO"));
    }

    @Test
    public void testGetAllSpaceNames() {
        workspaceApp.addSpace(new Space("MATH"));
        workspaceApp.addSpace(new Space("CHEM"));

        assertEquals(2, workspaceApp.getAllSpaceNames().size());
        assertTrue(workspaceApp.getAllSpaceNames().contains("MATH"));
        assertTrue(workspaceApp.getAllSpaceNames().contains("CHEM"));
    }

    @Test
    public void testGetSpaceOfName() {
        assertNull(workspaceApp.getSpaceOfName("BIO"));

        Space bioSpace = new Space("BIO");
        workspaceApp.addSpace(new Space("MATH"));
        workspaceApp.addSpace(bioSpace);

        assertEquals(bioSpace, workspaceApp.getSpaceOfName("BIO"));
    }
}
