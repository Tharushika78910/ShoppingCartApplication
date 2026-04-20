package com.example.shoppingcart;

import java.util.List;

public class CartCalculator {

    public double calculateItemTotal(double price, int quantity) {
        return price * quantity;
    }

    public double calculateCartTotal(List<CartItem> items) {
        return items.stream()
                .mapToDouble(CartItem::getTotalCost)
                .sum();
    }
}