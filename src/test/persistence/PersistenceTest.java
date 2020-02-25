package persistence;

import model.*;
import model.exception.InvalidFormatException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.NoSuchFileException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenceTest {
    private static final String TEST_DATA_FILE = "./data/test_save_data.json";
    private static final String TEST_FILE_RESOURCE = "./data/test_file.txt";
    private static final String TEST_APP_RESOURCE = "./data/test_app.exe";
    private static final String TEST_CORRUPTED_FILE = "./data/invalid_save_data.txt";

    WorkspaceApp workspace;

    @BeforeEach
    public void setUp() {
        workspace = new WorkspaceApp();
    }

    @Test
    public void testWriteAndReadData() {
        writeData();

        try {
            workspace.setSpaces(Reader.readSpaces(new File(TEST_DATA_FILE)));
        } catch (IOException e) {
            fail("Test data file not found.");
        } catch (InvalidFormatException e) {
            fail("Test data file formatted incorrectly.");
        }

        Space mathSpace = workspace.getSpaceOfName("MATH 101");
        Space englSpace = workspace.getSpaceOfName("ENGL 110");
        assertEquals(1, mathSpace.numResources());
        assertEquals(2, englSpace.numResources());
        assertEquals(2, mathSpace.getTodo().getNumToDos());
        assertEquals(1, englSpace.getTodo().getNumToDos());
        assertEquals("http://www.math.ubc.ca/~CLP/CLP2/clp_2_ic/",
                mathSpace.getResourceOfName("CLP").getPath());
        assertEquals(Resource.ResourceType.FILE, englSpace.getResourceOfName("Test file").getResourceType());
        assertEquals("Webwork assignment", mathSpace.getTodo().getTasks().get(1).getDescription());
        assertTrue(mathSpace.getTodo().getTasks().get(1).getComplete());
    }

    @Test
    public void testReadNonexistantFile() {
        try {
            List<Space> spaces = Reader.readSpaces(new File("./data/nonexistant_file.com"));
            fail("No exception thrown for nonexistant file.");
        } catch (InvalidFormatException e) {
            fail("Expected IOException, but InvalidFormatException was thrown.");
        } catch (IOException e) {
            // Expected to be thrown
        }
    }

    @Test
    public void testWriteString() {
        File file = new File(TEST_FILE_RESOURCE);
        BufferedReader bufferedReader;

        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            Writer writer = new Writer(file);
            writer.write("Test string");
            writer.close();
            assertEquals("Test string", bufferedReader.readLine());
            bufferedReader.close();
        } catch (IOException e) {
            fail("IOException was thrown, failed to write to test file.");
        }
    }

    @Test
    public void testReadInvalidFormatFile() {
        try {
            List<Space> spaces = Reader.readSpaces(new File(TEST_FILE_RESOURCE));
            fail("No exception thrown for invalid format file.");
        } catch (InvalidFormatException e) {
            // Expected to be thrown
        } catch (IOException e) {
            fail("Expected InvalidFormatException, but IOException was thrown.");
        }
    }

    @Test
    public void testReadCorruptedFile() {
        try {
            List<Space> spaces = Reader.readSpaces(new File(TEST_CORRUPTED_FILE));
            fail("No exception thrown for invalid format file.");
        } catch (InvalidFormatException e) {
            // Expected to be thrown
        } catch (IOException e) {
            fail("Expected InvalidFormatException, but IOException was thrown.");
        }
    }

    private void writeData() {
        initSpace();

        try {
            Writer writer = new Writer(new File(TEST_DATA_FILE));
            writer.write(workspace);
            writer.close();
        } catch (Exception e) {
            fail("Writer was not able to find or create test data file.");
        }
    }

    private void initSpace() {
        Space mathSpace = new Space("MATH 101");
        try {
            mathSpace.addResource(new WebsiteLink("CLP", "http://www.math.ubc.ca/~CLP/CLP2/clp_2_ic/"));
        } catch (MalformedURLException e) {
            fail("MalformedUrlException returned for valid url");
        }
        mathSpace.getTodo().addTask(new Task("Study for midterm"));
        Task webworkTask = new Task("Webwork assignment");
        webworkTask.setComplete(true);
        mathSpace.getTodo().addTask(webworkTask);

        Space englSpace = new Space ("ENGL 110");
        englSpace.getTodo().addTask(new Task("Read the Secret Agent"));
        try {
            englSpace.addResource(new FilePath("Test file", TEST_FILE_RESOURCE));
        } catch (NoSuchFileException e) {
            fail("Could not find " + TEST_FILE_RESOURCE);
        }

        try {
            englSpace.addResource(new AppShortcut("Test app", TEST_APP_RESOURCE));
        } catch (NoSuchFileException e) {
            fail("Could not find " + TEST_APP_RESOURCE);
        }

        workspace.addSpace(mathSpace);
        workspace.addSpace(englSpace);
    }
}
