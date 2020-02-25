package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.NoSuchFileException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppShortcutTest {
    private static final String TEST_TEXT_FILE = ".\\data\\test_file.txt";
    private static final String TEST_APP_FILE = ".\\data\\test_app.exe";
    Resource appShortcut;
    String fileSeparator;
    String testAppPath;

    @BeforeEach
    public void setUp(){
        try {
            appShortcut = new AppShortcut("Test App", TEST_APP_FILE);
        } catch (NoSuchFileException e) {
            fail("Could not find test app file " + testAppPath);
        }
    }

    @Test
    public void testConstructor() {
        assertEquals(TEST_APP_FILE, appShortcut.getPath());
        assertEquals("Test App", appShortcut.getName());
        assertEquals(Resource.ResourceType.APP, appShortcut.getResourceType());
    }

    @Test
    public void testSetInvalidFile() {
        boolean failed = false;
        try {
            appShortcut.setPath(TEST_TEXT_FILE);
        } catch (Exception e) {
            failed = true;
        }

        assertTrue(failed);
        assertEquals(TEST_APP_FILE, appShortcut.getPath());
    }
}
