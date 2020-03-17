package model;

import model.event.TimerEvent;
import model.exception.SystemNotSupportedException;
import model.listener.TimerListener;
import platformspecific.SystemTrayTool;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class WorkTimer {
    private static int UPDATE_DELAY = 1000;
    private Timer timer;
    private TimerTask task;
    private int hours;
    private int minutes;
    private int seconds;
    private int updateDelay;
    private EventListenerList listenerList;

    // EFFECTS: creates new timer with given hours and minutes
    public WorkTimer(int hours, int minutes) {
        listenerList = new EventListenerList();
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = 0;
        this.updateDelay = UPDATE_DELAY;
    }

    // EFFECTS: runs the timer
    public void run() {
        timer = new Timer();
        task = new Tick();
        timer.scheduleAtFixedRate(task, 0, updateDelay);
    }

    // MODIFIES: this
    // EFFECTS: adds timer listener to this timer
    public void addTimerListener(TimerListener listener) {
        listenerList.add(TimerListener.class, listener);
    }

    // MODIFIES: this
    // EFFECTS: removes timer listener from this timer
    public void removeTimerListener(TimerListener listener) {
        listenerList.remove(TimerListener.class, listener);
    }

    // EFFECTS: fires a tick event to all TimerListeners
    void fireTickEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] == TimerListener.class) {
                ((TimerListener) listeners[i + 1]).timerTick(
                        new TimerEvent(this, hours, minutes, seconds, getTime()));
            }
        }
    }

    // EFFECTS: fires a time up event to all TimerListeners
    void fireTimeUpEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] == TimerListener.class) {
                ((TimerListener) listeners[i + 1]).timeUp(new TimerEvent(this));
            }
        }
    }

    //setters
    public void setTime(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = 0;
    }

    //getters
    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    // EFFECTS: returns time formatted as <hours>:<minutes>:<seconds>
    public String getTime() {
        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }

    // MODIFIES: this
    // EFFECTS: cancels timer and fires timeUp event
    private void timeUp() {
        timer.cancel();
        fireTimeUpEvent();
    }

    // MODIFIES: this
    // EFFECTS: cancels timer without event
    public void cancelTimer() {
        timer.cancel();
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
        // EFFECTS: ticks the timer down by 1 second and fires timerTick event
        @Override
        public void run() {
            if (seconds == 0 && minutes == 0 && hours == 0) {
                timeUp();
            } else {
                if (seconds == 0) {
                    if (minutes == 0) {
                        hours--;
                        minutes = 59;
                        seconds = 59;
                    } else {
                        minutes--;
                        seconds = 59;
                    }
                } else {
                    seconds--;
                }
            }

            fireTickEvent();
        }
    }
}
