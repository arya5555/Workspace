package model;

import ui.platformspecific.SystemTrayTool;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class WorkTimer implements Runnable {
    private static int UPDATE_DELAY = 1000;
    private Timer timer;
    private TimerTask task;
    private int minutes;
    private int seconds;
    private Thread callingThread;
    private SystemTrayTool systemTrayTool;
    private int updateDelay;

    // EFFECTS: creates new timer with given time in minutes
    public WorkTimer(int minutes, Thread callingThread) {
        timer = new Timer();
        task = new Tick();
        task = new Tick();
        this.minutes = minutes;
        this.seconds = 0;
        this.callingThread = callingThread;
        this.updateDelay = UPDATE_DELAY;

        ClassLoader classLoader = this.getClass().getClassLoader();
        URL imageUrl = classLoader.getResource("timer.png");
        Image taskBarIcon;
        if (imageUrl == null) {
            // creates empty image if icon is not found
            taskBarIcon = new BufferedImage(10,10, 1);
        } else {
            taskBarIcon = new ImageIcon(imageUrl).getImage();
        }
        systemTrayTool = new SystemTrayTool(taskBarIcon);
    }

    // EFFECTS: runs the timer
    @Override
    public void run() {
        timer.schedule(task, updateDelay, updateDelay);
    }

    //getters
    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    // EFFECTS: returns time formatted as <hours>:<minutes>:<seconds>
    public String getTime() {
        int hours = minutes / 60;
        int remainingMinutes = minutes - (hours * 60);
        return String.format("%d:%02d:%02d", hours, remainingMinutes, seconds);
    }

    // MODIFIES: this
    // EFFECTS: cancels timer and attempts to show popup notification
    private void timeUp() {
        timer.cancel();
        callingThread.interrupt();
        systemTrayTool.showPopup("Time's up!", "Your work timer in "
                + callingThread.getName() + " is finished.");
        systemTrayTool.deleteTrayIcon();
    }

    // MODIFIES: this
    // EFFECTS: cancels timer without notifications
    public void cancelTimer() {
        timer.cancel();
        systemTrayTool.deleteTrayIcon();
    }

    // MODIFIES: this
    // EFFECTS: adds to timer the given number of minutes
    public void addTime(int minutes) {
        this.minutes += minutes;
    }

    // MODIFIES: this
    // EFFECTS: for testing purposes only, changes update delay
    public void setDelayForTesting(int milliseconds) {
        updateDelay = milliseconds;
    }

    private class Tick extends TimerTask {
        // EFFECTS: ticks the timer down by 1 second
        @Override
        public void run() {
            if (seconds == 0) {
                if (minutes == 0) {
                    timeUp();
                } else {
                    seconds = 59;
                    minutes--;
                }
            } else  {
                seconds--;
            }

            systemTrayTool.changeTooltip(callingThread.getName() + " " + getTime());
        }
    }
}
