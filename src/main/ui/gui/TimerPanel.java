package ui.gui;

import model.WorkTimer;
import model.TimerEvent;
import model.exception.SystemNotSupportedException;
import model.TimerListener;
import platformspecific.SystemTrayTool;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// JPanel that displays a timer and buttons for controlling the timer
public class TimerPanel extends JPanel implements GuiComponent {
    private static final String TIMER_SOUND_FILE = "./data/timer_tone.wav";
    private static final Color PANEL_COLOUR = ACCENT_1;
    private static final Color TEXT_COLOUR = MAIN_COLOUR_2;
    private static final String NOT_RUNNING_TOOLBAR = "not running";
    private static final String RUNNING_TOOLBAR = "running";
    private static final String PAUSED_TOOLBAR = "paused";
    private static final String SET_TIME = "Set time";
    private static final String START_TIMER = "Start";
    private static final String CANCEL_TIMER = "Cancel";
    private static final String PAUSE_TIMER = "Pause";
    private static final String RESUME_TIMER = "Resume";
    private static final String TRAY_ICON = "./data/timer.png";
    private static final int DEFAULT_TIME = 25;

    private WorkTimer timer;
    private JLabel timeLabel;
    private JPanel toolbar;
    private CardLayout toolbarLayout;
    private String spaceName;
    private SystemTrayTool trayTool;
    private boolean isTraySupported;

    // EFFECTS: constructs new timerPanel with timer not running
    TimerPanel(String title) {
        super(new BorderLayout());
        timer = new WorkTimer(0, DEFAULT_TIME);
        spaceName = title;
        isTraySupported = true;

        init(title);
    }

    // MODIFIES: this
    // EFFECTS: sets up timer panel
    private void init(String title) {
        GuiFrame.formatPanel(this);
        setBackground(PANEL_COLOUR);
        setBorder(new LineBorder(PANEL_COLOUR, MARGIN));
        setMinimumSize(new Dimension(0,0));

        addTimerListener();
        add(createHeaderLabel(title), BorderLayout.NORTH);
        add(createTimerLabel(), BorderLayout.CENTER);
        add(createToolbar(), BorderLayout.SOUTH);
    }

    // EFFECTS: creates header label for timer panel
    private JLabel createHeaderLabel(String title) {
        JLabel label = new JLabel(title, JLabel.CENTER);
        label.setFont(LARGE_BOLD_FONT);
        label.setForeground(TEXT_COLOUR);
        return label;
    }

    // MODIFIES: this
    // EFFECTS: creates label which displays the time
    private JLabel createTimerLabel() {
        //timeLabel = new JLabel(timer.getTime(), JLabel.CENTER);
        timeLabel = new JLabel(timer.getTime(), JLabel.CENTER);
        timeLabel.setFont(LARGE_BOLD_FONT);
        timeLabel.setForeground(TEXT_COLOUR);

        timeLabel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                if (e.getComponent().getHeight() < 50) {
                    timeLabel.setFont(LARGE_BOLD_FONT);
                } else {
                    Font resizedFont = new Font(LARGE_BOLD_FONT.getName(), LARGE_BOLD_FONT.getStyle(),
                            e.getComponent().getHeight() / 50 * 24);

                    timeLabel.setFont(resizedFont);
                }
            }
        });

        return timeLabel;
    }

    // MODIFIES: this
    // EFFECTS: updates label with current time
    private void updateTimerLabel() {
        timeLabel.setText(timer.getTime());
        timeLabel.repaint();
        timeLabel.setVisible(true);
    }

    // EFFECTS: creates toolbar with timer control buttons
    private JPanel createToolbar() {
        JPanel notRunningToolbar = createNotRunningToolbar();
        JPanel runningToolbar = createRunningToolbar();
        JPanel pausedToolbar = createPausedToolbar();

        toolbar = GuiFrame.formatPanel(new JPanel());
        toolbarLayout = new CardLayout();
        toolbar.setLayout(toolbarLayout);
        toolbar.add(notRunningToolbar, NOT_RUNNING_TOOLBAR);
        toolbar.add(runningToolbar, RUNNING_TOOLBAR);
        toolbar.add(pausedToolbar, PAUSED_TOOLBAR);

        toolbarLayout.show(toolbar, NOT_RUNNING_TOOLBAR);

        toolbar.setVisible(true);
        toolbar.setOpaque(true);

        return toolbar;
    }

    // EFFECTS: creates toolbar for when the timer is not running
    private JPanel createNotRunningToolbar() {
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout(FlowLayout.CENTER, MARGIN, MARGIN));
        toolbar.setBackground(PANEL_COLOUR);

        FormattedJButton setTimeButton = new FormattedJButton(SET_TIME, SMALL_BTN_COLOUR, MAIN_COLOUR, SMALL_BTN_FONT);
        setTimeButton.prepareButton(null, SET_TIME, this::actionPerformed);
        toolbar.add(setTimeButton);

        FormattedJButton startTimerButton = new FormattedJButton(START_TIMER, SMALL_BTN_COLOUR,
                MAIN_COLOUR, SMALL_BTN_FONT);
        startTimerButton.prepareButton(null, START_TIMER, this::actionPerformed);
        toolbar.add(startTimerButton);

        return toolbar;
    }

    // EFFECTS: creates toolbar for when the timer is running
    private JPanel createRunningToolbar() {
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout(FlowLayout.CENTER, MARGIN, MARGIN));
        toolbar.setBackground(PANEL_COLOUR);

        FormattedJButton pauseButton = new FormattedJButton(PAUSE_TIMER, SMALL_BTN_COLOUR,
                MAIN_COLOUR, SMALL_BTN_FONT);
        pauseButton.prepareButton(null, PAUSE_TIMER, this::actionPerformed);
        toolbar.add(pauseButton);

        FormattedJButton cancelButton = new FormattedJButton(CANCEL_TIMER, SMALL_BTN_COLOUR,
                MAIN_COLOUR, SMALL_BTN_FONT);
        cancelButton.prepareButton(null, CANCEL_TIMER, this::actionPerformed);
        toolbar.add(cancelButton);

        return toolbar;
    }

    // EFFECTS: creates toolbar for when the timer is paused
    private JPanel createPausedToolbar() {
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout(FlowLayout.CENTER, MARGIN, MARGIN));
        toolbar.setBackground(PANEL_COLOUR);

        FormattedJButton pauseButton = new FormattedJButton(RESUME_TIMER, SMALL_BTN_COLOUR,
                MAIN_COLOUR, SMALL_BTN_FONT);
        pauseButton.prepareButton(null, RESUME_TIMER, this::actionPerformed);
        toolbar.add(pauseButton);

        FormattedJButton cancelButton = new FormattedJButton(CANCEL_TIMER, SMALL_BTN_COLOUR,
                MAIN_COLOUR, SMALL_BTN_FONT);
        cancelButton.prepareButton(null, CANCEL_TIMER, this::actionPerformed);
        toolbar.add(cancelButton);

        return toolbar;
    }

    // MODIFIES: this
    // EFFECTS: processes button clicks
    private void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(START_TIMER)) {
            try {
                trayTool = new SystemTrayTool(loadTrayIcon());
            } catch (SystemNotSupportedException ex) {
                isTraySupported = false;
            }
            timer.run();
            toolbarLayout.show(toolbar, RUNNING_TOOLBAR);
        } else if (e.getActionCommand().equals(SET_TIME)) {
            setTime();
        } else if (e.getActionCommand().equals(PAUSE_TIMER)) {
            timer.cancelTimer();
            toolbarLayout.show(toolbar, PAUSED_TOOLBAR);
        } else if (e.getActionCommand().equals(RESUME_TIMER)) {
            timer.run();
            toolbarLayout.show(toolbar, RUNNING_TOOLBAR);
        } else if (e.getActionCommand().equals(CANCEL_TIMER)) {
            cancelTimer();
        }
    }

    // MODIFIES: this
    // EFFECTS: cancels timer and sets toolbar to not-running state
    private void cancelTimer() {
        timer.cancelTimer();
        timer.setTime(0, DEFAULT_TIME);
        updateTimerLabel();
        if (isTraySupported) {
            trayTool.deleteTrayIcon();
        }
        toolbarLayout.show(toolbar, NOT_RUNNING_TOOLBAR);
    }

    // MODIFIES: this
    // EFFECTS: shows dialog to set timer time
    private void setTime() {
        JSpinner hourSpinner = new JSpinner();
        hourSpinner.setModel(new SpinnerNumberModel(0, 0, null, 1));
        hourSpinner.setMaximumSize(new Dimension(40,40));
        JSpinner minuteSpinner = new JSpinner();
        minuteSpinner.setModel(new SpinnerNumberModel(0, 0, 59, 1));
        minuteSpinner.setMaximumSize(new Dimension(40,40));

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(MARGIN, MARGIN));
        panel.add(new JLabel("(hours : minutes)", JLabel.CENTER), BorderLayout.NORTH);

        JPanel timePanel = new JPanel();
        timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.X_AXIS));
        timePanel.add(Box.createHorizontalGlue());
        timePanel.add(hourSpinner);
        timePanel.add(new JLabel(" : "));
        timePanel.add(minuteSpinner);
        timePanel.add(Box.createHorizontalGlue());
        panel.add(timePanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(null, panel,
                "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            timer.setTime((int) hourSpinner.getValue(), (int) minuteSpinner.getValue());
            updateTimerLabel();
        }
    }

    // MODIFIES: this
    // EFFECTS: creates and adds listener to timer
    private void addTimerListener() {
        timer.addTimerListener(new TimerListener() {
            @Override
            public void timerTick(TimerEvent e) {
                trayTool.changeTooltip(spaceName + " : " + e.getTimeString());
                updateTimerLabel();
            }

            @Override
            public void timeUp(TimerEvent e) {
                if (isTraySupported) {
                    trayTool.showPopup("Time up!", "Your timer for " + spaceName + " has finished.");
                    trayTool.deleteTrayIcon();
                }

                playChime();
                timer.setTime(0, DEFAULT_TIME);
                toolbarLayout.show(toolbar, NOT_RUNNING_TOOLBAR);
                updateTimerLabel();
            }
        });
    }

    // EFFECTS: loads and returns tray icon. If file can't be found, creates generic image.
    private Image loadTrayIcon() {
        Image icon;
        try {
            icon = ImageIO.read(new File(TRAY_ICON));
        } catch (IOException e) {
            icon = new BufferedImage(10, 10, 1);
        }

        return icon;
    }

    // EFFECTS: plays the timer chime sound
    private void playChime() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(TIMER_SOUND_FILE));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            // do nothing: only effect is that alarm sound will not play
        }
    }
}
