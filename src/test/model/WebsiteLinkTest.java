package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static model.WebsiteLink.WEBSITE_RESOURCE_TYPE;

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
        assertEquals(WEBSITE_RESOURCE_TYPE, websiteLink.getResourceType());
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
            websiteLink.setPath("ubc.ca");
        } catch (Exception e){
            failed = true;
        }

        assertFalse(failed);
        assertEquals("http://ubc.ca", websiteLink.getPath());
    }
}