package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.NoSuchFileException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppShortcutTest {
    Resource appShortcut;
    String testAppPath;

    @BeforeEach
    public void setUp(){
        testAppPath = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";
        try {
            appShortcut = new AppShortcut("Chrome", testAppPath);
        } catch (NoSuchFileException e) {
            fail();
        }
    }

    @Test
    public void testSetInvalidFile() {
        boolean failed = false;
        try {
            appShortcut.setPath("C:\\Program Files (x86)\\Google\\Chrome\\Application");
        } catch (Exception e) {
            failed = true;
        }

        assertTrue(failed);
        assertEquals(testAppPath, appShortcut.getPath());
    }
}
