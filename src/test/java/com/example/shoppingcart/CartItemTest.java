package com.example.shoppingcart;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CartItemTest {

    @Test
    void testGetPrice() {
        CartItem item = new CartItem(10.0, 2);
        assertEquals(10.0, item.getPrice());
    }

    @Test
    void testGetQuantity() {
        CartItem item = new CartItem(10.0, 2);
        assertEquals(2, item.getQuantity());
    }

    @Test
    void testGetTotalCost() {
        CartItem item = new CartItem(12.5, 2);
        assertEquals(25.0, item.getTotalCost());
    }

    @Test
    void testZeroValues() {
        CartItem item = new CartItem(0.0, 0);
        assertEquals(0.0, item.getPrice());
        assertEquals(0, item.getQuantity());
        assertEquals(0.0, item.getTotalCost());
    }

    @Test
    void testLargeValues() {
        CartItem item = new CartItem(1000.0, 5);
        assertEquals(1000.0, item.getPrice());
        assertEquals(5, item.getQuantity());
        assertEquals(5000.0, item.getTotalCost());
    }

    @Test
    void testNegativePriceThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CartItem(-5.0, 2)
        );
        assertEquals("Price cannot be negative.", exception.getMessage());
    }

    @Test
    void testNegativeQuantityThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CartItem(5.0, -2)
        );
        assertEquals("Quantity cannot be negative.", exception.getMessage());
    }
}