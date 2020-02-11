package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class WorkTimerTest {
    private static final int DELAY = 200;
    private static final String TIMER_ICON_FILE = "timer.png";
    WorkTimer timer;
    Image icon;

    @BeforeEach
    public void setUp() {
        try {
            icon = ImageIO.read(new File("./data/timer.png"));
        } catch (IOException e) {
            fail();
        }
        timer = new WorkTimer(10, Thread.currentThread(), icon);
        timer.setDelayForTesting(DELAY);
    }

    @Test
    public void testConstructor() {
        assertEquals("0:10:00", timer.getTime());
        assertEquals(10, timer.getMinutes());
        assertEquals(0, timer.getSeconds());
    }

    @Test
    public void testRunTimer() {
        timer.run();
        try {
            Thread.sleep(DELAY * 2 + 100);
        } catch (InterruptedException e) {
            fail();
        }
        int minutes = timer.getMinutes();
        int seconds = timer.getSeconds();
        assertEquals("0:09:58", timer.getTime());
        assertEquals(9, minutes);
        assertEquals(58, seconds);
    }

    @Test
    public void testForcedCancelTimer() {
        timer = new WorkTimer(0, Thread.currentThread(), icon);
        timer.run();

        try {
            Thread.sleep(DELAY);
            timer.cancelTimer();
            Thread.sleep(DELAY * 2);
        } catch (InterruptedException e) {
            fail();
        }
    }

    @Test
    public void testTimeUp() {
        timer = new WorkTimer(0, Thread.currentThread(), icon);
        boolean interrupted = false;
        timer.run();

        try {
            Thread.sleep(DELAY + 1000);
        } catch (InterruptedException e) {
            interrupted = true;
        } finally {
            assertTrue(interrupted);
            assertEquals("0:00:00", timer.getTime());
        }
    }

    @Test
    public void testAddTime() {
        timer.run();
        timer.addTime(5);
        assertEquals(15, timer.getMinutes());
    }
}
