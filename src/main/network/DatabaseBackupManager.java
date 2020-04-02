package network;

import model.Account;
import model.exception.InvalidFormatException;
import model.exception.NoBackupFoundException;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import persistence.Reader;
import persistence.Writer;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static model.WorkspaceApp.WORKSPACE_FILE;

// Backs up and restores data stored in WORKSPACE_FILE to online database
public class DatabaseBackupManager {

    // EFFECTS: stores saved data in database, or displays error message if unable
    public static void backupData(Account account) throws SQLException, IOException, ParseException {
        DatabaseTool databaseTool = new DatabaseTool();
        JSONArray data = Reader.readFile(new File(WORKSPACE_FILE));
        databaseTool.backupData(account, data);
    }

    // MODIFIES: this
    // EFFECTS: overwrites local save data with backed up data
    public static void restoreBackup(Account account) throws IOException, SQLException,
            NoBackupFoundException {
        DatabaseTool databaseTool = new DatabaseTool();
        JSONArray data = databaseTool.retrieveBackup(account);

        Writer writer = new Writer(new File(WORKSPACE_FILE));
        writer.write(data.toString());
        writer.close();
    }
}
