import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CartCalculatorTest {

    private final CartCalculator calculator = new CartCalculator();

    @Test
    void shouldCalculateItemTotalCorrectly() {
        double result = calculator.calculateItemTotal(10.0, 3);
        assertEquals(30.0, result, 0.001);
    }

    @Test
    void shouldCalculateCartTotalCorrectly() {
        List<CartItem> items = List.of(
                new CartItem(10.0, 2), // 20
                new CartItem(5.5, 4),  // 22
                new CartItem(3.0, 1)   // 3
        );

        double result = calculator.calculateCartTotal(items);
        assertEquals(45.0, result, 0.001);
    }

    @Test
    void shouldReturnZeroForEmptyCart() {
        double result = calculator.calculateCartTotal(List.of());
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void shouldThrowExceptionForNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> new CartItem(-5.0, 1));
    }

    @Test
    void shouldThrowExceptionForNegativeQuantity() {
        assertThrows(IllegalArgumentException.class, () -> new CartItem(5.0, -1));
    }

    @Test
    void shouldCalculateCartItemTotal() {
        CartItem item = new CartItem(7.5, 2);
        assertEquals(15.0, item.getTotalCost(), 0.001);
    }
}