package model;

import java.util.EventListener;

public interface TimerListener extends EventListener {

    void timerTick(TimerEvent e);

    void timeUp(TimerEvent e);
}
