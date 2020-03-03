//package tools;
//
//import java.io.File;
//import java.sql.*;
//
//import model.Account;
//import model.exception.InvalidAccountException;
//import model.exception.NoBackupFoundException;
//import model.exception.UsernameAlreadyExistsException;
//import org.json.simple.JSONArray;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.runners.MockitoJUnitRunner;
//import persistence.Reader;
//
//import static org.junit.Assert.*;
//import static org.mockito.Matchers.*;
//import static org.mockito.Mockito.*;
//
//@RunWith(MockitoJUnitRunner.class)
//public class DatabaseToolTest {
//    private static final String TEST_DATA_FILE = "./data/test_save_data.json";
//    private static final String USERNAME = "testuser";
//    private static final String PASSWORD = "testpass";
//    private static final String NONEXISTANT_USER = "nonexistantuser";
//    private static final String CORRUPTED_BACKUP_USERNAME = "testCorruptedUser";
//    private static final String ACCOUNT_TABLE = "accounts";
//    private static final String ACCOUNT_ID_COLUMN = "id";
//    private static final String ACCOUNT_USER_COLUMN = "user";
//    private static final String ACCOUNT_PASS_COLUMN = "pass";
//    private static final String BACKUPS_TABLE = "backups";
//    private static final String BACKUPS_ID_COLUMN = "userid";
//    private static final String BACKUPS_DATA_COLUMN = "json_backup";
//
//    @Mock
//    private Statement statement;
//    @Mock
//    private Connection connection;
//    @Mock
//    private ResultSet testAcctResultSet;
//    @Mock
//    private ResultSet testCorruptedAcctResultSet;
//    @Mock
//    private ResultSet testBackupResultSet;
//    @Mock
//    private ResultSet testCorruptedBackupResultSet;
//    @Mock
//    private ResultSet emptyResultSet;
//
//    @Before
//    public void setUpMocking() {
//        JSONArray testBackupData = null;
//        try {
//            testBackupData = Reader.readFile(new File(TEST_DATA_FILE));
//        } catch (Exception e) {
//            fail("Failed to read in test data file.");
//        }
//
//        try {
//            when(testAcctResultSet.getString(ACCOUNT_PASS_COLUMN)).thenReturn(PASSWORD);
//            when(testAcctResultSet.getInt(ACCOUNT_ID_COLUMN)).thenReturn(1);
//            when(testAcctResultSet.next()).thenReturn(true);
//            when(testCorruptedAcctResultSet.getString(ACCOUNT_PASS_COLUMN)).thenReturn(PASSWORD);
//            when(testCorruptedAcctResultSet.getInt(ACCOUNT_ID_COLUMN)).thenReturn(2);
//            when(testCorruptedAcctResultSet.next()).thenReturn(true);
//            when(testBackupResultSet.getString(BACKUPS_DATA_COLUMN)).thenReturn(testBackupData.toString());
//            when(testBackupResultSet.next()).thenReturn(true);
//            when(testCorruptedBackupResultSet.getString(BACKUPS_DATA_COLUMN)).thenReturn("Invalid backup data.");
//            when(testCorruptedBackupResultSet.next()).thenReturn(true);
//            when(emptyResultSet.next()).thenReturn(false);
//
//            when(statement.execute(any(String.class))).thenReturn(true);
//            when(statement.executeQuery("SELECT * FROM " + ACCOUNT_TABLE + " WHERE "
//                    + ACCOUNT_USER_COLUMN + " = '" + USERNAME + "'")).thenReturn(emptyResultSet);
//            when(statement.executeQuery("SELECT * FROM " + ACCOUNT_TABLE + " WHERE " + ACCOUNT_USER_COLUMN
//                    + " = '" + CORRUPTED_BACKUP_USERNAME + "'")).thenReturn(testCorruptedAcctResultSet);
//            when(statement.executeQuery("SELECT * FROM " + ACCOUNT_TABLE + " WHERE "
//                    + ACCOUNT_USER_COLUMN + " = '" + NONEXISTANT_USER + "'")).thenReturn(emptyResultSet);
//            when(statement.executeQuery("SELECT * FROM " + BACKUPS_TABLE + " WHERE " + BACKUPS_ID_COLUMN
//                    + " in(3)")).thenReturn(emptyResultSet);
//            when(statement.executeQuery("SELECT * FROM " + BACKUPS_TABLE + " WHERE " + BACKUPS_ID_COLUMN +
//                    " in(1)")).thenReturn(testBackupResultSet);
//            when(statement.executeQuery("SELECT * FROM " + BACKUPS_TABLE + " WHERE " + BACKUPS_ID_COLUMN +
//                    " in(2)")).thenReturn(testCorruptedBackupResultSet);
//
//            try {
//                databaseTool = new DatabaseTool(statement, connection);
//                databaseTool.createAccount(USERNAME, PASSWORD);
//                when(statement.executeQuery("SELECT * FROM " + ACCOUNT_TABLE + " WHERE "
//                        + ACCOUNT_USER_COLUMN + " = '" + USERNAME + "'")).thenReturn(testAcctResultSet);
//                testAccount = databaseTool.signIn(USERNAME, PASSWORD);
//            } catch (InvalidAccountException e) {
//                fail("Failed to sign in to test account.");
//            } catch (UsernameAlreadyExistsException e) {
//                fail("Test account shouldn't already exist.");
//            }
//        } catch (SQLException e) {
//            fail("SQLException should not have been thrown.");
//        }
//    }
//
//    private DatabaseTool databaseTool;
//    private Account testAccount;
//
//    @After
//    public void close() {
//        try {
//            databaseTool.deleteAccount(testAccount);
//            databaseTool.close();
//        } catch (SQLException e) {
//            fail("Failed to communicate with database to delete test account.");
//        }
//
//        try {
//            databaseTool.close();
//        } catch (SQLException e) {
//            fail("Failed to close connection to database.");
//        }
//    }
//
//    @Test
//    public void testSignIn() {
//        assertEquals(USERNAME, testAccount.getUserName());
//    }
//
//    @Test
//    public void testSignInWrongPassword() {
//        try {
//            Account account = databaseTool.signIn(USERNAME, "wrongPassword");
//            fail("Should have returned InvalidAccountException, returned no exceptions.");
//        } catch (SQLException e) {
//            fail("Failed to communicate with database to sign in to account.");
//        } catch (InvalidAccountException e) {
//            // Expected result
//        }
//    }
//
//    @Test
//    public void testSignInNonexistantAccount() {
//        try {
//            Account account = databaseTool.signIn(NONEXISTANT_USER, "wrongPassword");
//            fail("Should have returned InvalidAccountException, returned no exceptions.");
//        } catch (SQLException e) {
//            fail("Failed to communicate with database to sign in to account.");
//        } catch (InvalidAccountException e) {
//            // Expected result
//        }
//    }
//
//    @Test
//    public void testGetCorruptedBackup() {
//        Account corruptedAccount;
//
//        try {
//            corruptedAccount = databaseTool.signIn(CORRUPTED_BACKUP_USERNAME, PASSWORD);
//        } catch (SQLException e) {
//            fail("Failed to communicate with database to sign in to account.");
//            return;
//        } catch (InvalidAccountException e) {
//            fail("Failed to sign in to " + CORRUPTED_BACKUP_USERNAME + "account.");
//            return;
//        }
//
//        try {
//            databaseTool.retrieveBackup(corruptedAccount);
//            fail("Expected NoBackupFoundException for account with invalid backup data,"
//                    + " but no backup was found.");
//        } catch (SQLException e) {
//            fail("Failed to communicate with database to retrieve backup.");
//        } catch (NoBackupFoundException e) {
//            // Expected result
//        }
//    }
//
//    @Test
//    public void testBackupData() {
//        JSONArray data = null;
//
//        try {
//            data = Reader.readFile(new File(TEST_DATA_FILE));
//            databaseTool.backupData(testAccount, data);
//        } catch (SQLException e) {
//            fail("Failed to communicate with database to back up data.");
//        } catch (Exception e) {
//            fail("Failed to read test data file.");
//        }
//
//        try {
//            when(statement.executeQuery("SELECT * FROM " + BACKUPS_TABLE + " WHERE " + BACKUPS_ID_COLUMN +
//                    " in(1)")).thenReturn(testBackupResultSet);
//        } catch (SQLException e) {
//            fail("SQLException should not have been thrown.");
//        }
//
//        JSONArray retrievedBackup;
//        try {
//            retrievedBackup = databaseTool.retrieveBackup(testAccount);
//            assertEquals(data.toString(), retrievedBackup.toString());
//        } catch (SQLException e) {
//            fail("Failed to communicate with database to retrieve backup data.");
//        } catch (NoBackupFoundException e) {
//            fail("No save data found for account which should have backup.");
//        }
//    }
//
//    @Test
//    public void testCreateAccountAlreadyExists() {
//        try {
//            databaseTool.createAccount(USERNAME, "newPassword");
//            System.out.println("Expected UsernameAlreadyExistsException, but no exception was thrown.");
//        } catch (SQLException e) {
//            System.out.println("Failed to communicate with database to create new account.");
//        } catch (UsernameAlreadyExistsException e) {
//            // Expected result
//        }
//    }
//
//    @Test
//    public void testGetBackupNoneExists() {
//        try {
//            databaseTool.retrieveBackup(new Account("accountWithNoBackup", 3));
//            fail("Expected NoBackupFoundException, but no exception was thrown.");
//        } catch (SQLException e) {
//            fail("Failed to communicate with database to retrieve backup.");
//        } catch (NoBackupFoundException e) {
//            // Expected result
//        }
//    }
//
//    @Test
//    public void testOverwriteBackup() {
//        try {
//            when(statement.executeQuery("SELECT * FROM " + BACKUPS_TABLE + " WHERE " + BACKUPS_ID_COLUMN +
//                    " in(1)")).thenReturn(emptyResultSet);
//        } catch (SQLException e) {
//            fail("SQLException should not have been thrown.");
//        }
//        testBackupData();
//        testBackupData();
//    }
//
//    @Test
//    public void testDeleteBackup() {
//        try {
//            databaseTool.deleteBackup(testAccount);
//        } catch (SQLException e) {
//            fail("Failed to delete backup for test account.");
//        }
//    }
//}
