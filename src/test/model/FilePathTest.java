package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import platformspecific.ResourceLauncher;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FilePathTest {
    private static final String TEST_FILE = "./TestDirectory/test.txt";
    private File testFile;
    private String fileSeparator;
    private FilePath filePath;

    // source for how to create file with default file separator:
    // https://www.journaldev.com/825/java-create-new-file
    @BeforeEach
    public void setUp() {
        testFile = new File(TEST_FILE);

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
        assertEquals(Resource.ResourceType.FILE, filePath.getResourceType());
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

    @Test
    public void testOpenFile() {
        boolean successful = true;
        try {
            filePath.launch();
        } catch (Exception e) {
            successful = false;
        } finally {
            assertEquals(ResourceLauncher.isDesktopSupported(), successful);
        }
    }
}