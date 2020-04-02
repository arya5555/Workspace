package ui.gui;

// Passed to a WorkspaceMenuBar listener when a user selects certain options
public class MenuBarEvent {

    public enum EventType {
        SAVE_SPACES,
        LOAD_SAVE_DATA,
        BACKUP_DATA,
        RESTORE_BACKUP;
    }

    private EventType eventType;
    private Object object;

    // EFFECTS: constructs new event argument with given EventType and Object argument
    public MenuBarEvent(EventType eventType, Object object) {
        this.eventType = eventType;
        this.object = object;
    }

    // getters
    public EventType getEventType() {
        return eventType;
    }

    public Object getObject() {
        return object;
    }
}
