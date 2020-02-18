package persistence;

import java.io.FileWriter;
import java.io.PrintWriter;

public interface Saveable {

    // MODIFIES: file
    // EFFECTS: writes the saveable to print writer
    void save(PrintWriter writer);
}
