package persistence;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Writer {
    private PrintWriter printWriter;

    // EFFECTS: constructs Writer that will write to given file
    public Writer(File file) throws IOException {
        printWriter = new PrintWriter(file);
    }

    // MODIFIES: this
    // EFFECTS: writes saveable to file
    public void write(Saveable saveable) {
        saveable.save(printWriter);
    }

    // MODIFIES: this
    // EFFECTS: closes file writer
    public void close() {
        printWriter.close();
    }
}
