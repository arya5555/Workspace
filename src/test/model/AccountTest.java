package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountTest {
    private Account account;

    @BeforeEach
    public void setUp() {
        account = new Account("user", 3);
    }

    @Test
    public void testConstructor() {
        assertEquals("user", account.getUserName());
        assertEquals(3, account.getId());
    }
}
