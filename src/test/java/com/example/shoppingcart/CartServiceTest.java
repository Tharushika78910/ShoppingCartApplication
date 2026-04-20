package com.example.shoppingcart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CartServiceTest {

    @BeforeEach
    void clearDatabase() throws Exception {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM cart_items");
            statement.executeUpdate("DELETE FROM cart_records");
            statement.executeUpdate("ALTER TABLE cart_items AUTO_INCREMENT = 1");
            statement.executeUpdate("ALTER TABLE cart_records AUTO_INCREMENT = 1");
        }
    }

    @Test
    void testSaveCartSuccessfully() {
        CartService cartService = new CartService();

        List<CartItem> items = List.of(
                new CartItem(5.0, 2),
                new CartItem(4.0, 3)
        );

        assertDoesNotThrow(() -> cartService.saveCart(22.0, "en_US", items));

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet cartResult = statement.executeQuery(
                    "SELECT total_items, total_cost, language FROM cart_records WHERE id = 1"
            );

            assertTrue(cartResult.next());
            assertEquals(5, cartResult.getInt("total_items"));
            assertEquals(22.0, cartResult.getDouble("total_cost"));
            assertEquals("en_US", cartResult.getString("language"));

            ResultSet itemCountResult = statement.executeQuery(
                    "SELECT COUNT(*) AS item_count FROM cart_items WHERE cart_record_id = 1"
            );

            assertTrue(itemCountResult.next());
            assertEquals(2, itemCountResult.getInt("item_count"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSavedCartItemsData() {
        CartService cartService = new CartService();

        List<CartItem> items = List.of(
                new CartItem(10.0, 1),
                new CartItem(7.5, 2)
        );

        assertDoesNotThrow(() -> cartService.saveCart(25.0, "fi_FI", items));

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT item_number, price, quantity, subtotal FROM cart_items WHERE cart_record_id = ? ORDER BY item_number"
             )) {

            statement.setInt(1, 1);
            ResultSet resultSet = statement.executeQuery();

            assertTrue(resultSet.next());
            assertEquals(1, resultSet.getInt("item_number"));
            assertEquals(10.0, resultSet.getDouble("price"));
            assertEquals(1, resultSet.getInt("quantity"));
            assertEquals(10.0, resultSet.getDouble("subtotal"));

            assertTrue(resultSet.next());
            assertEquals(2, resultSet.getInt("item_number"));
            assertEquals(7.5, resultSet.getDouble("price"));
            assertEquals(2, resultSet.getInt("quantity"));
            assertEquals(15.0, resultSet.getDouble("subtotal"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSaveCartWithSingleItem() {
        CartService cartService = new CartService();

        List<CartItem> items = List.of(
                new CartItem(8.0, 2)
        );

        assertDoesNotThrow(() -> cartService.saveCart(16.0, "sv_SE", items));

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet cartResult = statement.executeQuery(
                    "SELECT total_items, total_cost, language FROM cart_records WHERE id = 1"
            );

            assertTrue(cartResult.next());
            assertEquals(2, cartResult.getInt("total_items"));
            assertEquals(16.0, cartResult.getDouble("total_cost"));
            assertEquals("sv_SE", cartResult.getString("language"));

            ResultSet itemResult = statement.executeQuery(
                    "SELECT item_number, price, quantity, subtotal FROM cart_items WHERE cart_record_id = 1"
            );

            assertTrue(itemResult.next());
            assertEquals(1, itemResult.getInt("item_number"));
            assertEquals(8.0, itemResult.getDouble("price"));
            assertEquals(2, itemResult.getInt("quantity"));
            assertEquals(16.0, itemResult.getDouble("subtotal"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}