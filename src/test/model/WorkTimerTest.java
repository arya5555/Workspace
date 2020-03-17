package model;

import model.event.TimerEvent;
import model.listener.TimerListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class WorkTimerTest {
    private static final int DELAY = 200;
    private WorkTimer timer;
    private int numTicks;
    private boolean timeUp;

    Image icon;

    @BeforeEach
    public void setUp() {
        timer = new WorkTimer(0, 10);
        timer.setDelayForTesting(DELAY);
        numTicks = 0;
        timeUp = false;
    }

    @Test
    public void testConstructor() {
        assertEquals("0:10:00", timer.getTime());
        assertEquals(0, timer.getHours());
        assertEquals(10, timer.getMinutes());
        assertEquals(0, timer.getSeconds());
    }

    @Test
    public void testRunTimer() {
        timer.addTimerListener(new TimerListener() {
            @Override
            public void timerTick(TimerEvent e) {
                incrementTicks();
            }

            @Override
            public void timeUp(TimerEvent e) {
                fail("Timer should not have fired time up event.");
            }
        });

        timer.run();
        try {
            Thread.sleep(DELAY*3);
        } catch (InterruptedException e) {
            fail("Thread should not have been interrupted.");
        }
        timer.cancelTimer();

        assertEquals("0:09:57", timer.getTime());
        assertEquals(3, numTicks);
    }

    private void incrementTicks() {
        numTicks++;
    }

    @Test
    public void testForcedCancelTimer() {
        timer = new WorkTimer(0, 10);
        timer.addTimerListener(new TimerListener() {
            @Override
            public void timerTick(TimerEvent e) {
                // do nothing
            }

            @Override
            public void timeUp(TimerEvent e) {
                fail("Timer should not have fired time up event.");
            }
        });

        timer.run();
        timer.cancelTimer();

        try {
            Thread.sleep(DELAY * 2);
        } catch (InterruptedException e) {
            fail("Thread should not have been interrupted.");
        }
    }

    @Test
    public void testTimeUp() {
        timer = new WorkTimer(0, 0);

        timer.addTimerListener(new TimerListener() {
            @Override
            public void timerTick(TimerEvent e) {
                // do nothing
            }

            @Override
            public void timeUp(TimerEvent e) {
                setTimeUp(true);
            }
        });

        timer.run();

        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            fail("Thread should not have been interrupted.");
        }

        assertEquals("0:00:00", timer.getTime());
        assertTrue(timeUp);
    }

    private void setTimeUp(Boolean timeUp) {
        this.timeUp = timeUp;
    }

    @Test
    public void testAddTime() {
        timer.addTime(5);
        assertEquals(15, timer.getMinutes());
    }
}
