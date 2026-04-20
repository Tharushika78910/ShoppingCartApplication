package com.example.shoppingcart;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseConnectionTest {

    @Test
    void testDefaultDatabaseUrl() {
        assertEquals(
                "jdbc:mysql://localhost:3306/shopping_cart_localization",
                DatabaseConnection.getDatabaseUrl()
        );
    }

    @Test
    void testDefaultDatabaseUser() {
        assertEquals("root", DatabaseConnection.getDatabaseUser());
    }

    @Test
    void testIsPasswordMissingWhenNull() {
        assertTrue(DatabaseConnection.isPasswordMissing(null));
    }

    @Test
    void testIsPasswordMissingWhenBlank() {
        assertTrue(DatabaseConnection.isPasswordMissing("   "));
    }

    @Test
    void testIsPasswordMissingWhenPresent() {
        assertFalse(DatabaseConnection.isPasswordMissing("secret123"));
    }
}