package com.example.shoppingcart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalizationServiceTest {

    @BeforeEach
    void setupTestData() throws Exception {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM localization_strings");

            statement.executeUpdate("""
                    INSERT INTO localization_strings (`key`, value, language) VALUES
                    ('app.title', 'Shopping Cart', 'en_US'),
                    ('language.label', 'Select Language', 'en_US'),
                    ('app.title', 'Varukorg', 'sv_SE'),
                    ('language.label', 'Välj språk', 'sv_SE')
                    """);
        }
    }

    @Test
    void testGetLocalizedStringsForEnglish() {
        LocalizationService service = new LocalizationService();

        Map<String, String> texts = service.getLocalizedStrings("en_US");

        assertEquals("Shopping Cart", texts.get("app.title"));
        assertEquals("Select Language", texts.get("language.label"));
        assertEquals(2, texts.size());
    }

    @Test
    void testGetLocalizedStringsForSwedish() {
        LocalizationService service = new LocalizationService();

        Map<String, String> texts = service.getLocalizedStrings("sv_SE");

        assertEquals("Varukorg", texts.get("app.title"));
        assertEquals("Välj språk", texts.get("language.label"));
        assertEquals(2, texts.size());
    }

    @Test
    void testGetLocalizedStringsForUnknownLanguage() {
        LocalizationService service = new LocalizationService();

        Map<String, String> texts = service.getLocalizedStrings("ja_JP");

        assertTrue(texts.isEmpty());
    }

    @Test
    void testGetLocalizedStringsWithSingleKey() throws Exception {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM localization_strings");
            statement.executeUpdate("""
                    INSERT INTO localization_strings (`key`, value, language)
                    VALUES ('app.title', 'Only Title', 'fi_FI')
                    """);
        }

        LocalizationService service = new LocalizationService();
        Map<String, String> texts = service.getLocalizedStrings("fi_FI");

        assertEquals(1, texts.size());
        assertEquals("Only Title", texts.get("app.title"));
    }
}