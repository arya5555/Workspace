package model;

import model.exception.FailedToOpenException;
import model.exception.SystemNotSupportedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import platformspecific.ResourceLauncher;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SpaceTest {
    //Note: do I have to test the functions that are only 1 line and use list library functions?
    // also I can't really test launch all resources.

    Space space;
    Resource textbookLink;
    Resource googleLink;
    List<Resource> testResources;

    @BeforeEach
    public void setUp() {
        space = new Space("MATH");
        try {
            textbookLink = new WebsiteLink("Textbook", "http://www.math.ubc.ca/~CLP/CLP2/clp_2_ic/index.html");
            googleLink = new WebsiteLink("Google", "https://www.google.com/");
            testResources = new ArrayList<>(Arrays.asList(textbookLink, googleLink));
        } catch (Exception e) {
            fail();
        }
        space.setTodo(new ToDoList());
    }

    @Test
    public void testConstructor() {
        assertEquals("MATH", space.getName());
        assertEquals(0, space.numResources());
        assertEquals(0, space.getTodo().getNumToDos());
        assertFalse(space.isTimerRunning());
    }

    @Test
    public void testGetResourceOfName() {
        space.addResource(textbookLink);
        assertEquals(textbookLink, space.getResourceOfName("Textbook"));
    }

    @Test
    public void testRunTimer() {
        space.startTimer(0, 5);
        assertEquals("0:05:00", space.getTimeOnTimer());
        assertTrue(space.isTimerRunning());
        space.cancelTimer();
        assertFalse(space.isTimerRunning());
    }

    @Test
    public void testTimeUp() {
        space.startTimer(0, 0);
        space.timeUp();
        assertFalse(space.isTimerRunning());
        space.cancelTimer();
        assertFalse(space.isTimerRunning());
    }

    @Test
    public void testGetNonExistentResourceOfName() {
        space.addResource(textbookLink);
        assertNull(space.getResourceOfName("Google"));
    }

    @Test
    public void testRemoveResource() {
        space.setResources(testResources);

        space.removeResource(1);

        assertEquals(1, space.numResources());
        assertTrue(space.getResources().contains(textbookLink));
    }

    @Test
    public void testRemoveResourceByName() {
        space.setResources(testResources);

        space.removeResource("Textbook");

        assertEquals(1, space.numResources());
        assertTrue(space.getResources().contains(googleLink));
    }

    @Test
    public void testGetAllResources() {
        space.setResources(testResources);

        List<String> resourceNames = space.getAllResourceNames();
        assertEquals(2, resourceNames.size());
        assertTrue(resourceNames.contains("Textbook"));
        assertTrue(resourceNames.contains("Google"));
    }

    @Test
    public void testLaunchAllResources() {
        boolean successfulLaunch = true;

        space.setResources(testResources);

        try {
            space.launchAllResources();
        } catch (Exception e) {
            successfulLaunch = false;
        } finally {
            assertEquals(ResourceLauncher.isDesktopSupported(), successfulLaunch);
        }
    }

    @Test
    public void testLaunchResource() {
        boolean successfulLaunch = true;

        space.addResource(textbookLink);

        try {
            space.launchResource(0);
        } catch (Exception e) {
            successfulLaunch = false;
        } finally {
            assertEquals(ResourceLauncher.isDesktopSupported(), successfulLaunch);
        }
    }

    @Test
    public void testLaunchInvalidResource() {
        boolean successfulLaunch = true;

        space.addResource(textbookLink);

        try {
            space.launchResource(4);
        } catch (Exception e) {
            successfulLaunch = false;
        } finally {
            assertFalse(successfulLaunch);
        }
    }
}
