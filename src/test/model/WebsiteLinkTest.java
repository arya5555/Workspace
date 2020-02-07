package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class WebsiteLinkTest {
    Resource websiteLink;

    @BeforeEach
    public void setUp() {
        try {
            websiteLink = new WebsiteLink("Canvas", "https://canvas.ubc.ca/");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testConstructor() {
        assertEquals("Canvas", websiteLink.getName());
        assertEquals("https://canvas.ubc.ca/", websiteLink.getPath());
    }

    @Test
    public void testSetInvalidPath() {
        boolean failed = false;
        try {
            websiteLink.setPath("fake\\site*bad");
        } catch (Exception e){
            failed = true;
        }

        assertTrue(failed);
        assertEquals("https://canvas.ubc.ca/", websiteLink.getPath());
    }

    @Test
    public void testSetValidPath() {
        boolean failed = false;
        try {
            websiteLink.setPath("https://ubc.ca");
        } catch (Exception e){
            failed = true;
        }

        assertFalse(failed);
        assertEquals("https://ubc.ca", websiteLink.getPath());
    }

    @Test
    public void testLaunch() {
        Desktop desktop = Desktop.getDesktop();
        if (desktop.isSupported(Desktop.Action.BROWSE)) {
            assertTrue(websiteLink.launch());
        } else {
            assertFalse(websiteLink.launch());
        }
    }
}