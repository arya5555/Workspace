package network;

import model.Account;
import model.exception.InvalidAccountException;
import model.exception.NoBackupFoundException;
import model.exception.UsernameAlreadyExistsException;
import org.json.simple.JSONArray;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.Reader;

import java.io.File;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

// Can only be run when the database is functioning
public class DatabaseToolTest {
    private static final String TEST_DATA_FILE = "./data/test_save_data.json";
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "testpass";
    private static final String NONEXISTANT_USER = "nonexistantuser";
    private static final String CORRUPTED_BACKUP_USERNAME = "testCorruptedUser";

    // mysql -h 34.95.8.137 -u workspaceApp -p j*H3jHie382o

    private DatabaseTool databaseTool;
    private Account testAccount;

    @BeforeEach
    public void setUp() {
        try {
            databaseTool = new DatabaseTool();
            testAccount = databaseTool.signIn(USERNAME, PASSWORD);
        } catch (InvalidAccountException e) {
            fail("Failed to sign in to test account.");
        } catch (SQLException e) {
            fail("SQLException should not have been thrown.");
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
        assertEquals(USERNAME, testAccount.getUserName());
    }

    @Test
    public void testSignInWrongPassword() {
        try {
            Account account = databaseTool.signIn(USERNAME, "wrongPassword");
            fail("Should have returned InvalidAccountException, returned no exceptions.");
        } catch (SQLException e) {
            fail("Failed to communicate with database to sign in to account.");
        } catch (InvalidAccountException e) {
            // Expected result
        }
    }

    @Test
    public void testSignInNonexistantAccount() {
        try {
            Account account = databaseTool.signIn(NONEXISTANT_USER, "wrongPassword");
            fail("Should have returned InvalidAccountException, returned no exceptions.");
        } catch (SQLException e) {
            fail("Failed to communicate with database to sign in to account.");
        } catch (InvalidAccountException e) {
            // Expected result
        }
    }

    @Test
    public void testGetCorruptedBackup() {
        Account corruptedAccount;

        try {
            corruptedAccount = databaseTool.signIn(CORRUPTED_BACKUP_USERNAME, PASSWORD);
        } catch (SQLException e) {
            fail("Failed to communicate with database to sign in to account.");
            return;
        } catch (InvalidAccountException e) {
            fail("Failed to sign in to " + CORRUPTED_BACKUP_USERNAME + "account.");
            return;
        }

        try {
            databaseTool.retrieveBackup(corruptedAccount);
            fail("Expected NoBackupFoundException for account with invalid backup data,"
                    + " but no backup was found.");
        } catch (SQLException e) {
            fail("Failed to communicate with database to retrieve backup.");
        } catch (NoBackupFoundException e) {
            // Expected result
        }
    }

    @Test
    public void testBackupData() {
        JSONArray data = null;

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
    public void testCreateAccountAlreadyExists() {
        try {
            databaseTool.createAccount(USERNAME, "newPassword");
            System.out.println("Expected UsernameAlreadyExistsException, but no exception was thrown.");
        } catch (SQLException e) {
            System.out.println("Failed to communicate with database to create new account.");
        } catch (UsernameAlreadyExistsException e) {
            // Expected result
        }
    }

    @Test
    public void testGetBackupNoneExists() {
        try {
            databaseTool.retrieveBackup(new Account("accountWithNoBackup", 3));
            fail("Expected NoBackupFoundException, but no exception was thrown.");
        } catch (SQLException e) {
            fail("Failed to communicate with database to retrieve backup.");
        } catch (NoBackupFoundException e) {
            // Expected result
        }
    }

    @Test
    public void testOverwriteBackup() {
        testBackupData();
        testBackupData();
    }

    @Test
    public void testDeleteBackup() {
        try {
            databaseTool.deleteBackup(testAccount);
        } catch (SQLException e) {
            fail("Failed to delete backup for test account.");
        }
    }
}
