package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;

import static model.FilePath.FILE_RESOURCE_TYPE;
import static org.junit.jupiter.api.Assertions.*;

class FilePathTest {
    File testFile;
    String fileSeparator;
    FilePath filePath;

    // source for how to create file with default file separator:
    // https://www.journaldev.com/825/java-create-new-file
    @BeforeEach
    public void setUp() {
        fileSeparator = System.getProperty("file.separator");
        testFile = new File("TestDirectory" + fileSeparator + "test.txt");

        try {
            testFile.getParentFile().mkdirs();
            testFile.createNewFile();
        } catch (Exception e) {
            fail();
        }

        try {
            filePath = new FilePath("TestFile", testFile.getPath());
        } catch (Exception e) {
            fail();
        }
    }

    @AfterEach
    public void deleteFile() {
        testFile.delete();
        testFile.getParentFile().delete();
    }

    @Test
    public void testConstructor() {
        assertEquals("TestFile", filePath.getName());
        assertEquals(testFile.getPath(), filePath.getPath());
        assertEquals(FILE_RESOURCE_TYPE, filePath.getResourceType());
    }

    @Test
    public void testEmptyConstructor() {
        filePath = new FilePath();
        assertEquals("", filePath.getName());
    }

    @Test
    public void testSetNonexistantFile() {
        boolean failed = false;
        try {
            filePath.setPath("TestDirectory" + fileSeparator + "assuming_this_file_doesnt_exist.txt");
        } catch (Exception e) {
            failed = true;
        }

        assertTrue(failed);
        assertEquals(testFile.getPath(), filePath.getPath());
    }

    @Test
    public void testGetFileExtension() {
        assertEquals("txt", filePath.getFileExtension());
        try {
            assertEquals("",
                    new FilePath("Directory", testFile.getParentFile().getPath()).getFileExtension());
        } catch (Exception e) {
            fail();
        }
    }
}