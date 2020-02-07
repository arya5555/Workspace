package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    Task task;

    @BeforeEach
    public void setUp() {
        task = new Task("Finish assignment 1");
    }

    @Test
    public void testConstructor() {
        assertEquals("Finish assignment 1", task.getDescription());
        assertFalse(task.getComplete());

        task = new Task("Finish assignment 2", true);
        assertEquals("Finish assignment 2", task.getDescription());
        assertTrue(task.getComplete());
    }

    @Test
    public void testCompleteTask() {
        task.setComplete(true);
        assertTrue(task.getComplete());
    }
}
