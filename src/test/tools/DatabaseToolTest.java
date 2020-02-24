package tools;

import model.Account;
import model.exception.InvalidAccountException;
import model.exception.NoBackupFoundException;
import org.json.simple.JSONArray;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.Reader;

import java.io.File;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseToolTest {
    private static final String TEST_DATA_FILE = "./data/test_save_data.txt";
    private DatabaseTool databaseTool;

    @BeforeEach
    public void setUp() {
        try {
            databaseTool = new DatabaseTool();
        } catch (SQLException e) {
            fail("Failed to connect to database.");
        }
    }

    @AfterEach
    public void close() {
        try {
            databaseTool.close();
        } catch (SQLException e) {
            fail("Failed to close connection to database.");
        }
    }

    @Test
    public void testSignIn() {
        Account testAccount = signInToTestAccount();
        assertEquals("Arya", testAccount.getUserName());
        assertEquals(1, testAccount.getId());
    }

    @Test
    public void testSignInWrongPassword() {
        try {
            Account account = databaseTool.signIn("Arya", "wrongPassword");
            fail("Should have returned InvalidAccountException, returned no exceptions.");
        } catch (SQLException e) {
            fail("Failed to communicate with database to sign in to account.");
        } catch (InvalidAccountException e) {
            // Expected result
        }
    }

    @Test
    public void testBackupData() {
        JSONArray data = null;
        Account testAccount = signInToTestAccount();

        try {
            data = Reader.readFile(new File(TEST_DATA_FILE));
            databaseTool.backupData(testAccount, data);
        } catch (SQLException e) {
            fail("Failed to communicate with database to back up data.");
        } catch (Exception e) {
            fail("Failed to read test data file.");
         }

        JSONArray retrievedBackup;
        try {
            retrievedBackup = databaseTool.retrieveBackup(testAccount);
            assertEquals(data.toString(), retrievedBackup.toString());
        } catch (SQLException e) {
            fail("Failed to communicate with database to retrieve backup data.");
        } catch (NoBackupFoundException e) {
            fail("No save data found for account which should have backup.");
        }
    }

    @Test
    public void testNewBackupData() {
        Account testAccount = signInToTestAccount();
        try {
            databaseTool.deleteBackup(testAccount);
        } catch (SQLException e) {
            fail("Failed to delete backup for test account.");
        }

        testBackupData();
    }

    @Test
    public void testDeleteBackup() {
        Account testAccount = signInToTestAccount();
        try {
            databaseTool.deleteBackup(testAccount);
        } catch (SQLException e) {
            fail("Failed to delete backup for test account.");
        }

        try {
            databaseTool.retrieveBackup(testAccount);
            fail("Retrieving backup for account with no backup, expected NoBackupFoundException.");
        } catch (SQLException e) {
            fail("Retrieving account backup, SQLException was thrown but NoBackupFoundException was expected.");
        } catch (NoBackupFoundException e) {
            // Expected result
        }
    }

    private Account signInToTestAccount() {
        try {
            return databaseTool.signIn("Arya", "verySecurePassword");
        } catch (SQLException e) {
            fail("Failed to communicate with database to sign in to account.");
            return null;
        } catch (InvalidAccountException e) {
            fail("Returned InvalidAccountException, account should have been valid.");
            return null;
        }
    }
}
