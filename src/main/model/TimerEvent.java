package model;

import model.WorkTimer;

import java.util.EventObject;

public class TimerEvent extends EventObject {
    int hours;
    int minutes;
    int seconds;
    String timeString;

    // EFFECTS: creates timer event with given hours, minutes, and seconds as current time
    public TimerEvent(WorkTimer source, int hours, int minutes, int seconds, String timeString) {
        super(source);
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.timeString = timeString;
    }

    // EFFECTS: creates timer event with 0 as time
    public TimerEvent(WorkTimer source) {
        this(source, 0, 0, 0, "0:00:00");
    }

    // getters
    public String getTimeString() {
        return timeString;
    }
}
