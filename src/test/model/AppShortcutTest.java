package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.NoSuchFileException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppShortcutTest {
    Resource appShortcut;
    String fileSeparator;
    String testAppPath;
    String testTextFilePath;
    File testAppFile;
    File testTextFile;

    @BeforeEach
    public void setUp(){
        fileSeparator = System.getProperty("file.separator");
        testAppPath = "data" + fileSeparator + "DoNothing.exe";
        try {
            appShortcut = new AppShortcut("Test App", testAppPath);
        } catch (NoSuchFileException e) {
            fail("Could not find test app file " + testAppPath);
        }
    }

    @AfterEach
    public void deleteFiles() {
        testAppFile.delete();
        testTextFile.delete();
        testAppFile.getParentFile().delete();
    }

    @Test
    public void testConstructor() {
        assertEquals(testAppPath, appShortcut.getPath());
        assertEquals("Test App", appShortcut.getName());
        assertEquals(Resource.ResourceType.APP, appShortcut.getResourceType());
    }

    @Test
    public void testSetInvalidFile() {
        boolean failed = false;
        try {
            appShortcut.setPath(testTextFilePath);
        } catch (Exception e) {
            failed = true;
        }

        assertTrue(failed);
        assertEquals(testAppPath, appShortcut.getPath());
    }
}
