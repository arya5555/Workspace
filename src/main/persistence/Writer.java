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

    // EFFECTS: writes saveable to file
    public void write(Saveable saveable) {
        saveable.save(printWriter);
    }

    // EFFECTS: writes given string to file
    public void write(String string) {
        printWriter.write(string);
    }

    // MODIFIES: this
    // EFFECTS: closes file writer
    public void close() {
        printWriter.close();
    }
}
