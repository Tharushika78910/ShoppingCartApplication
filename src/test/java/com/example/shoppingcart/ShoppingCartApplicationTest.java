package com.example.shoppingcart;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShoppingCartApplicationTest {

    @BeforeAll
    static void initJavaFx() {
        new JFXPanel();
        Platform.setImplicitExit(false);
    }

    @BeforeEach
    void setup() throws Exception {
        setCurrentLocale(Locale.US);
        setPrimaryStage(null);

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM localization_strings");

            statement.executeUpdate("""
                    INSERT INTO localization_strings (`key`, value, language) VALUES
                    ('app.title', 'Shopping Cart', 'en_US'),
                    ('app.title', 'Varukorg', 'sv_SE'),
                    ('app.title', 'Ostoskori', 'fi_FI'),
                    ('app.title', 'ショッピングカート', 'ja_JP'),
                    ('app.title', 'عربة التسوق', 'ar_AR'),
                    ('language.label', 'Select Language', 'en_US'),
                    ('number.of.items', 'Number of Items', 'en_US'),
                    ('confirm.language', 'Confirm Language', 'en_US'),
                    ('enter.items', 'Enter Items', 'en_US'),
                    ('calculate.total', 'Calculate Total', 'en_US'),
                    ('cart.total', 'Cart Total:', 'en_US'),
                    ('item.label', 'Item', 'en_US'),
                    ('enter.price', 'Enter price', 'en_US'),
                    ('enter.quantity', 'Enter quantity', 'en_US'),
                    ('item.total', 'Item Total:', 'en_US'),
                    ('error.invalid.integer', 'Please enter a valid integer.', 'en_US'),
                    ('error.number.gt.zero', 'Please enter a number greater than 0.', 'en_US'),
                    ('error.invalid.price.quantity', 'Please enter valid numbers for price and quantity.', 'en_US')
                    """);
        }
    }

    @Test
    void testGetCurrentLocaleDefault() throws Exception {
        setCurrentLocale(Locale.US);
        assertEquals(Locale.US, ShoppingCartApplication.getCurrentLocale());
    }

    @Test
    void testGetCurrentLanguageCodeDefault() throws Exception {
        setCurrentLocale(Locale.US);
        assertEquals("en_US", ShoppingCartApplication.getCurrentLanguageCode());
    }

    @Test
    void testGetCurrentLanguageCodeForFinnish() throws Exception {
        setCurrentLocale(new Locale.Builder().setLanguage("fi").setRegion("FI").build());
        assertEquals("fi_FI", ShoppingCartApplication.getCurrentLanguageCode());
    }

    @Test
    void testGetCurrentLanguageCodeForSwedish() throws Exception {
        setCurrentLocale(new Locale.Builder().setLanguage("sv").setRegion("SE").build());
        assertEquals("sv_SE", ShoppingCartApplication.getCurrentLanguageCode());
    }

    @Test
    void testGetCurrentLanguageCodeForJapanese() throws Exception {
        setCurrentLocale(new Locale.Builder().setLanguage("ja").setRegion("JP").build());
        assertEquals("ja_JP", ShoppingCartApplication.getCurrentLanguageCode());
    }

    @Test
    void testGetCurrentLanguageCodeForArabic() throws Exception {
        setCurrentLocale(new Locale.Builder().setLanguage("ar").setRegion("AR").build());
        assertEquals("ar_AR", ShoppingCartApplication.getCurrentLanguageCode());
    }

    @Test
    void testBuildLanguageCodeEnglish() {
        assertEquals("en_US", ShoppingCartApplication.buildLanguageCode(Locale.US));
    }

    @Test
    void testBuildLanguageCodeFinnish() {
        Locale locale = new Locale.Builder().setLanguage("fi").setRegion("FI").build();
        assertEquals("fi_FI", ShoppingCartApplication.buildLanguageCode(locale));
    }

    @Test
    void testBuildLanguageCodeJapanese() {
        Locale locale = new Locale.Builder().setLanguage("ja").setRegion("JP").build();
        assertEquals("ja_JP", ShoppingCartApplication.buildLanguageCode(locale));
    }

    @Test
    void testBuildWindowTitle() {
        assertEquals(
                "Shopping Cart / Dilmi Tharushika",
                ShoppingCartApplication.buildWindowTitle("Shopping Cart")
        );
    }

    @Test
    void testBuildWindowTitleWithTranslatedValue() {
        assertEquals(
                "Varukorg / Dilmi Tharushika",
                ShoppingCartApplication.buildWindowTitle("Varukorg")
        );
    }

    @Test
    void testStartLoadsViewAndSetsWindowTitle() throws Exception {
        ShoppingCartApplication app = new ShoppingCartApplication();
        AtomicReference<Stage> stageRef = new AtomicReference<>();

        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                stageRef.set(stage);
                app.start(stage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Stage stage = stageRef.get();
        assertNotNull(stage);
        assertNotNull(stage.getScene());
        assertTrue(stage.getTitle().contains("Shopping Cart"));
        assertTrue(stage.getTitle().contains("Dilmi Tharushika"));

        runOnFxThreadAndWait(stage::close);
    }

    @Test
    void testSwitchLanguageUpdatesWindowTitle() throws Exception {
        ShoppingCartApplication app = new ShoppingCartApplication();
        AtomicReference<Stage> stageRef = new AtomicReference<>();

        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                stageRef.set(stage);
                app.start(stage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Stage stage = stageRef.get();

        runOnFxThreadAndWait(() ->
                ShoppingCartApplication.switchLanguage(
                        new Locale.Builder().setLanguage("sv").setRegion("SE").build()
                )
        );

        assertEquals("sv_SE", ShoppingCartApplication.getCurrentLanguageCode());
        assertTrue(stage.getTitle().contains("Varukorg"));

        runOnFxThreadAndWait(stage::close);
    }

    private void setCurrentLocale(Locale locale) throws Exception {
        Field field = ShoppingCartApplication.class.getDeclaredField("currentLocale");
        field.setAccessible(true);
        field.set(null, locale);
    }

    private void setPrimaryStage(Stage stage) throws Exception {
        Field field = ShoppingCartApplication.class.getDeclaredField("primaryStage");
        field.setAccessible(true);
        field.set(null, stage);
    }

    private static void runOnFxThreadAndWait(Runnable action) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                latch.countDown();
            }
        });

        latch.await();
    }
}