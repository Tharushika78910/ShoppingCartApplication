package com.example.shoppingcart;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartCalculatorTest {

    @Test
    void testCalculateItemTotal() {
        CartCalculator calculator = new CartCalculator();
        assertEquals(30.0, calculator.calculateItemTotal(10.0, 3));
    }

    @Test
    void testCalculateCartTotal() {
        CartCalculator calculator = new CartCalculator();

        List<CartItem> items = List.of(
                new CartItem(10.0, 2),
                new CartItem(5.0, 4)
        );

        assertEquals(40.0, calculator.calculateCartTotal(items));
    }

    @Test
    void testEmptyCart() {
        CartCalculator calculator = new CartCalculator();
        assertEquals(0.0, calculator.calculateCartTotal(List.of()));
    }

    @Test
    void testCartItemGetTotalCost() {
        CartItem item = new CartItem(12.5, 2);
        assertEquals(25.0, item.getTotalCost());
    }

    @Test
    void testGetPriceAndQuantity() {
        CartItem item = new CartItem(10.0, 2);
        assertEquals(10.0, item.getPrice());
        assertEquals(2, item.getQuantity());
    }

    @Test
    void testZeroValues() {
        CartItem item = new CartItem(0.0, 0);
        assertEquals(0.0, item.getTotalCost());
    }

    @Test
    void testLargeValues() {
        CartItem item = new CartItem(1000.0, 5);
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