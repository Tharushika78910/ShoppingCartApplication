package com.example.shoppingcart;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    static String getDatabaseUrl() {
        return System.getenv().getOrDefault(
                "DB_URL",
                "jdbc:mysql://localhost:3306/shopping_cart_localization"
        );
    }

    static String getDatabaseUser() {
        return System.getenv().getOrDefault(
                "DB_USER",
                "root"
        );
    }

    static String getDatabasePassword() {
        return System.getenv("DB_PASSWORD");
    }

    static boolean isPasswordMissing(String password) {
        return password == null || password.isBlank();
    }

    public static Connection getConnection() throws SQLException {
        String password = getDatabasePassword();

        if (isPasswordMissing(password)) {
            throw new SQLException("Database password is not set. Please define DB_PASSWORD environment variable.");
        }

        return DriverManager.getConnection(
                getDatabaseUrl(),
                getDatabaseUser(),
                password
        );
    }
}