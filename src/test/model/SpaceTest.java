package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        assertEquals(0, space.getResources().size());
        assertEquals(0, space.getTodo().getNumToDos());
    }

    @Test
    public void testGetResourceOfName() {
        space.addResource(textbookLink);
        assertEquals(textbookLink, space.getResourceOfName("Textbook"));
    }

    @Test
    public void testGetNonExistentResourceOfName() {
        space.addResource(textbookLink);
        assertNull(space.getResourceOfName("Google"));
    }

    @Test
    public void testLaunchResource() {
        space.addResource(textbookLink);
        assertTrue(space.launchResource(0));
    }

    @Test
    public void testLaunchAllResources() {
        space.addResource(textbookLink);
        space.addResource(googleLink);
        assertTrue(space.launchAllResources());
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

        assertEquals(1, space.getResources().size());
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
}
