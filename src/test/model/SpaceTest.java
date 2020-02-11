package model;

import model.exception.FailedToOpenException;
import model.exception.SystemNotSupportedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import platformspecific.ResourceLauncher;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SpaceTest {
    //Note: do I have to test the functions that are only 1 line and use list library functions?
    // also I can't really test launch all resources.

    Space space;
    Resource textbookLink;
    Resource googleLink;

    @BeforeEach
    public void setUp() {
        space = new Space("MATH");
        try {
            textbookLink = new WebsiteLink("Textbook", "http://www.math.ubc.ca/~CLP/CLP2/clp_2_ic/index.html");
            googleLink = new WebsiteLink("Google", "https://www.google.com/");
        } catch (Exception e) {
            fail();
        }
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
        Image emptyImage = new BufferedImage(10, 10, 1);
        space.startTimer(5, Thread.currentThread(), emptyImage);
        assertEquals("0:05:00", space.getTimeOnTimer());
        assertTrue(space.isTimerRunning());
        space.cancelTimer();
        assertFalse(space.isTimerRunning());
    }

    @Test
    public void testTimeUp() {
        Image emptyImage = new BufferedImage(10, 10, 1);
        space.startTimer(5, Thread.currentThread(), emptyImage);
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
        Resource textbookLink;
        Resource googleLink;

        try {
            textbookLink = new WebsiteLink("Textbook", "http://www.math.ubc.ca/~CLP/CLP2/clp_2_ic/index.html");
            googleLink = new WebsiteLink("Google", "https://www.google.com/");
        } catch (Exception e) {
            fail();
            return;
        }

        space.addResource(textbookLink);
        space.addResource(googleLink);

        space.removeResource(1);

        assertEquals(1, space.numResources());
        assertTrue(space.getResources().contains(textbookLink));
    }

    @Test
    public void testGetAllResources() {
        space.addResource(textbookLink);
        space.addResource(googleLink);

        List<String> resourceNames = space.getAllResourceNames();
        assertEquals(2, resourceNames.size());
        assertTrue(resourceNames.contains("Textbook"));
        assertTrue(resourceNames.contains("Google"));
    }

    @Test
    public void testLaunchAllResources() {
        boolean successfulLaunch = true;

        space.addResource(textbookLink);
        space.addResource(googleLink);

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
