package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.NoSuchFileException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppShortcutTest {
    Resource appShortcut;
    String fileSeparator;
    String testAppPath;

    @BeforeEach
    public void setUp(){
        fileSeparator = System.getProperty("file.separator");
        testAppPath = "data" + fileSeparator + "DoNothing.exe";
        try {
            appShortcut = new AppShortcut("DoNothing", testAppPath);
        } catch (NoSuchFileException e) {
            fail();
        }
    }

    @Test
    public void testSetInvalidFile() {
        boolean failed = false;
        try {
            appShortcut.setPath("data" + fileSeparator + "invalid_file.txt");
        } catch (Exception e) {
            failed = true;
        }

        assertTrue(failed);
        assertEquals(testAppPath, appShortcut.getPath());
    }
}
