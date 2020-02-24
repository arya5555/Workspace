package tools;

import model.Account;
import model.exception.InvalidAccountException;
import model.exception.NoBackupFoundException;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.*;

public class DatabaseTool {
    private static final String SQL_URL = "jdbc:mysql://34.95.8.137:3306/workspace";
    private static final String SQL_USER = "workspaceApp";
    private static final String SQL_PASS = "8ecM&a!32hN";
    private static final String ACCOUNT_TABLE = "accounts";
    private static final String ACCOUNT_ID_COLUMN = "id";
    private static final String ACCOUNT_USER_COLUMN = "user";
    private static final String ACCOUNT_PASS_COLUMN = "pass";
    private static final String BACKUPS_TABLE = "backups";
    private static final String BACKUPS_ID_COLUMN = "userid";
    private static final String BACKUPS_DATA_COLUMN = "json_backup";
    Connection connection;
    Statement statement;
    ResultSet resultSet;

    // EFFECTS: creates connection to google cloud sql server instance
    //          throws SQLException if connection fails
    public DatabaseTool() throws SQLException {
        connection = DriverManager.getConnection(SQL_URL,SQL_USER,SQL_PASS);
        statement = connection.createStatement();
    }

    // EFFECTS: if can't connect to sql database, throws SQLException
    //          if password doesn't match password stored in database for given username, throws InvalidAccountException
    //          otherwise, returns account stored in database
    public Account signIn(String userName, String password) throws SQLException, InvalidAccountException {
        resultSet = statement.executeQuery("SELECT * FROM " + ACCOUNT_TABLE + " WHERE "
                + ACCOUNT_USER_COLUMN + " = '" + userName + "'");
        if (resultSet.next()) {
            if (password.equals(resultSet.getString(ACCOUNT_PASS_COLUMN))) {
                return new Account(userName, resultSet.getInt(ACCOUNT_ID_COLUMN));
            }
        }

        throw new InvalidAccountException();
    }

    // EFFECTS: if can't connect to sql database, throws SQLException
    //          otherwise, adds account to database with given username and password
    public void createAccount(String userName, String password) throws SQLException {
        statement.execute("INSERT INTO " + ACCOUNT_TABLE + " (" + ACCOUNT_USER_COLUMN + ","
                + ACCOUNT_PASS_COLUMN + ") VALUES ('" + userName + "', '" + password + "')");
    }

    // EFFECTS: if can't connect to sql database, throws SQLException
    //          otherwise, stores backup data in database
    public void backupData(Account account, JSONArray data) throws SQLException {
        boolean backupAlreadyExists;
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + BACKUPS_TABLE + " WHERE "
                + BACKUPS_ID_COLUMN + " in(" + account.getId() + ")");

        if (resultSet.next()) {
            backupAlreadyExists = true;
        } else {
            backupAlreadyExists = false;
        }

        if (backupAlreadyExists) {
            statement.executeUpdate("UPDATE " + BACKUPS_TABLE + " SET " + BACKUPS_DATA_COLUMN + " = '"
                    + data.toString() + "' WHERE " + BACKUPS_ID_COLUMN + " in(" + account.getId() + ")");
        } else {
            statement.execute("INSERT INTO " + BACKUPS_TABLE + " (" + BACKUPS_ID_COLUMN + ","
                    + BACKUPS_DATA_COLUMN + ") VALUES ('" + account.getId() + "', '" + data.toString() + "')");
        }
    }

    // EFFECTS: if can't connect to sql database, throws SQLException
    //          if there is a backup for this account, deletes the backup
    public void deleteBackup(Account account) throws SQLException {
        statement.execute("DELETE FROM " + BACKUPS_TABLE + " WHERE " + BACKUPS_ID_COLUMN
                + " in(" + account.getId() + ")");
    }

    // EFFECTS: if can't connect to sql database, throws SQLException
    //          if there is no backup for this account, throws NoBackupFoundException
    //          otherwise, returns JSONArray containing backed up space data
    public JSONArray retrieveBackup(Account account) throws SQLException, NoBackupFoundException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + BACKUPS_TABLE + " WHERE "
                + BACKUPS_ID_COLUMN + " in(" + account.getId() + ")");

        if (resultSet.next()) {
            JSONParser jsonParser = new JSONParser();
            String result = (String) resultSet.getObject(BACKUPS_DATA_COLUMN);
            JSONArray jsonArray = null;
            try {
                jsonArray = (JSONArray) jsonParser.parse(result);
            } catch (ParseException e) {
                throw new NoBackupFoundException();
            }
            return jsonArray;
        } else {
            throw new NoBackupFoundException();
        }
    }

    public void close() throws SQLException {
        statement.close();
        connection.close();
        resultSet.close();
    }
}
