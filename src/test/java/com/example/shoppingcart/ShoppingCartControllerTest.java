package com.example.shoppingcart;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShoppingCartControllerTest {

    @BeforeAll
    static void initJavaFx() {
        new JFXPanel();
    }

    @BeforeEach
    void resetState() throws Exception {
        setCurrentLocale(Locale.US);
        clearCartTables();
    }

    @Test
    void testInitializeSetsDefaultLanguageAndTexts() throws Exception {
        TestableShoppingCartController controller = createController();

        runOnFxThreadAndWait(controller::initialize);

        ComboBox<String> languageComboBox =
                (ComboBox<String>) getField(controller, "languageComboBox");
        Label titleLabel =
                (Label) getField(controller, "titleLabel");

        assertEquals("English", languageComboBox.getValue());
        assertTrue(titleLabel.getText() != null && !titleLabel.getText().isEmpty());
    }

    @ParameterizedTest
    @MethodSource("languageTestCases")
    void testInitializeSetsSelectedLanguage(Locale locale, String expectedLanguage) throws Exception {
        setCurrentLocale(locale);
        TestableShoppingCartController controller = createController();

        runOnFxThreadAndWait(controller::initialize);

        ComboBox<String> languageComboBox =
                (ComboBox<String>) getField(controller, "languageComboBox");

        assertEquals(expectedLanguage, languageComboBox.getValue());
    }

    @Test
    void testInitializeAppliesArabicDirection() throws Exception {
        setCurrentLocale(new Locale.Builder().setLanguage("ar").setRegion("AR").build());
        TestableShoppingCartController controller = createController();

        runOnFxThreadAndWait(controller::initialize);

        VBox rootVBox = (VBox) getField(controller, "rootVBox");
        TextField numberOfItemsField = (TextField) getField(controller, "numberOfItemsField");
        Label cartTotalLabel = (Label) getField(controller, "cartTotalLabel");

        assertEquals(NodeOrientation.RIGHT_TO_LEFT, rootVBox.getNodeOrientation());
        assertEquals(NodeOrientation.RIGHT_TO_LEFT, numberOfItemsField.getNodeOrientation());
        assertEquals(NodeOrientation.RIGHT_TO_LEFT, cartTotalLabel.getNodeOrientation());
        assertEquals(Pos.CENTER_RIGHT, numberOfItemsField.getAlignment());
    }

    @Test
    void testInitializeAppliesLeftToRightDirection() throws Exception {
        TestableShoppingCartController controller = createController();

        runOnFxThreadAndWait(controller::initialize);

        VBox rootVBox = (VBox) getField(controller, "rootVBox");
        TextField numberOfItemsField = (TextField) getField(controller, "numberOfItemsField");
        Label cartTotalLabel = (Label) getField(controller, "cartTotalLabel");

        assertEquals(NodeOrientation.LEFT_TO_RIGHT, rootVBox.getNodeOrientation());
        assertEquals(NodeOrientation.LEFT_TO_RIGHT, numberOfItemsField.getNodeOrientation());
        assertEquals(NodeOrientation.LEFT_TO_RIGHT, cartTotalLabel.getNodeOrientation());
        assertEquals(Pos.CENTER_LEFT, numberOfItemsField.getAlignment());
    }

    @Test
    void testHandleConfirmLanguageSetsEnglishLocaleByDefault() throws Exception {
        TestableShoppingCartController controller = createController();

        runOnFxThreadAndWait(controller::initialize);

        ComboBox<String> languageComboBox =
                (ComboBox<String>) getField(controller, "languageComboBox");

        runOnFxThreadAndWait(() -> languageComboBox.setValue("English"));
        runPrivateMethod(controller, "handleConfirmLanguage");

        assertEquals(Locale.US, ShoppingCartApplication.getCurrentLocale());
    }

    @ParameterizedTest
    @MethodSource("confirmLanguageTestCases")
    void testHandleConfirmLanguageSwitchesLocale(String selectedLanguage, Locale expectedLocale) throws Exception {
        TestableShoppingCartController controller = createController();

        runOnFxThreadAndWait(controller::initialize);

        ComboBox<String> languageComboBox =
                (ComboBox<String>) getField(controller, "languageComboBox");

        runOnFxThreadAndWait(() -> languageComboBox.setValue(selectedLanguage));
        runPrivateMethod(controller, "handleConfirmLanguage");

        assertEquals(expectedLocale, ShoppingCartApplication.getCurrentLocale());
    }

    @Test
    void testHandleEnterItemsCreatesDynamicFields() throws Exception {
        TestableShoppingCartController controller = createController();

        runOnFxThreadAndWait(controller::initialize);

        TextField numberOfItemsField =
                (TextField) getField(controller, "numberOfItemsField");
        GridPane itemsGrid =
                (GridPane) getField(controller, "itemsGrid");

        runOnFxThreadAndWait(() -> numberOfItemsField.setText("2"));
        runPrivateMethod(controller, "handleEnterItems");

        List<?> priceFields = (List<?>) getField(controller, "priceFields");
        List<?> quantityFields = (List<?>) getField(controller, "quantityFields");
        List<?> itemTotalLabels = (List<?>) getField(controller, "itemTotalLabels");

        assertEquals(2, priceFields.size());
        assertEquals(2, quantityFields.size());
        assertEquals(2, itemTotalLabels.size());
        assertEquals(8, itemsGrid.getChildren().size());
    }

    @Test
    void testHandleEnterItemsWithInvalidNumberDoesNotCreateFieldsAndShowsError() throws Exception {
        TestableShoppingCartController controller = createController();

        runOnFxThreadAndWait(controller::initialize);

        TextField numberOfItemsField =
                (TextField) getField(controller, "numberOfItemsField");

        runOnFxThreadAndWait(() -> numberOfItemsField.setText("abc"));
        runPrivateMethod(controller, "handleEnterItems");

        List<?> priceFields = (List<?>) getField(controller, "priceFields");
        List<?> quantityFields = (List<?>) getField(controller, "quantityFields");
        List<?> itemTotalLabels = (List<?>) getField(controller, "itemTotalLabels");

        assertEquals(0, priceFields.size());
        assertEquals(0, quantityFields.size());
        assertEquals(0, itemTotalLabels.size());
        assertTrue(controller.getLastErrorMessage() != null && !controller.getLastErrorMessage().isEmpty());
    }

    @Test
    void testHandleEnterItemsWithZeroDoesNotCreateFieldsAndShowsError() throws Exception {
        TestableShoppingCartController controller = createController();

        runOnFxThreadAndWait(controller::initialize);

        TextField numberOfItemsField =
                (TextField) getField(controller, "numberOfItemsField");

        runOnFxThreadAndWait(() -> numberOfItemsField.setText("0"));
        runPrivateMethod(controller, "handleEnterItems");

        List<?> priceFields = (List<?>) getField(controller, "priceFields");
        List<?> quantityFields = (List<?>) getField(controller, "quantityFields");
        List<?> itemTotalLabels = (List<?>) getField(controller, "itemTotalLabels");

        assertEquals(0, priceFields.size());
        assertEquals(0, quantityFields.size());
        assertEquals(0, itemTotalLabels.size());
        assertTrue(controller.getLastErrorMessage() != null && !controller.getLastErrorMessage().isEmpty());
    }

    @Test
    void testHandleEnterItemsWithNegativeNumberDoesNotCreateFieldsAndShowsError() throws Exception {
        TestableShoppingCartController controller = createController();

        runOnFxThreadAndWait(controller::initialize);

        TextField numberOfItemsField =
                (TextField) getField(controller, "numberOfItemsField");

        runOnFxThreadAndWait(() -> numberOfItemsField.setText("-2"));
        runPrivateMethod(controller, "handleEnterItems");

        List<?> priceFields = (List<?>) getField(controller, "priceFields");
        List<?> quantityFields = (List<?>) getField(controller, "quantityFields");
        List<?> itemTotalLabels = (List<?>) getField(controller, "itemTotalLabels");

        assertEquals(0, priceFields.size());
        assertEquals(0, quantityFields.size());
        assertEquals(0, itemTotalLabels.size());
        assertTrue(controller.getLastErrorMessage() != null && !controller.getLastErrorMessage().isEmpty());
    }

    @Test
    void testHandleEnterItemsAppliesArabicOrientationToDynamicFields() throws Exception {
        setCurrentLocale(new Locale.Builder().setLanguage("ar").setRegion("AR").build());
        TestableShoppingCartController controller = createController();

        runOnFxThreadAndWait(controller::initialize);

        TextField numberOfItemsField = (TextField) getField(controller, "numberOfItemsField");
        runOnFxThreadAndWait(() -> numberOfItemsField.setText("1"));
        runPrivateMethod(controller, "handleEnterItems");

        List<TextField> priceFields = (List<TextField>) getField(controller, "priceFields");
        List<TextField> quantityFields = (List<TextField>) getField(controller, "quantityFields");
        List<Label> itemTotalLabels = (List<Label>) getField(controller, "itemTotalLabels");

        assertEquals(NodeOrientation.RIGHT_TO_LEFT, priceFields.get(0).getNodeOrientation());
        assertEquals(NodeOrientation.RIGHT_TO_LEFT, quantityFields.get(0).getNodeOrientation());
        assertEquals(NodeOrientation.RIGHT_TO_LEFT, itemTotalLabels.get(0).getNodeOrientation());
        assertEquals(Pos.CENTER_RIGHT, priceFields.get(0).getAlignment());
        assertEquals(Pos.CENTER_RIGHT, quantityFields.get(0).getAlignment());
    }

    @Test
    void testHandleEnterItemsAppliesLeftToRightOrientationToDynamicFields() throws Exception {
        TestableShoppingCartController controller = createController();

        runOnFxThreadAndWait(controller::initialize);

        TextField numberOfItemsField = (TextField) getField(controller, "numberOfItemsField");
        runOnFxThreadAndWait(() -> numberOfItemsField.setText("1"));
        runPrivateMethod(controller, "handleEnterItems");

        List<TextField> priceFields = (List<TextField>) getField(controller, "priceFields");
        List<TextField> quantityFields = (List<TextField>) getField(controller, "quantityFields");
        List<Label> itemTotalLabels = (List<Label>) getField(controller, "itemTotalLabels");

        assertEquals(NodeOrientation.LEFT_TO_RIGHT, priceFields.get(0).getNodeOrientation());
        assertEquals(NodeOrientation.LEFT_TO_RIGHT, quantityFields.get(0).getNodeOrientation());
        assertEquals(NodeOrientation.LEFT_TO_RIGHT, itemTotalLabels.get(0).getNodeOrientation());
        assertEquals(Pos.CENTER_LEFT, priceFields.get(0).getAlignment());
        assertEquals(Pos.CENTER_LEFT, quantityFields.get(0).getAlignment());
    }

    @Test
    void testHandleCalculateTotalUpdatesCartTotalAndSavesToDatabase() throws Exception {
        TestableShoppingCartController controller = createController();

        runOnFxThreadAndWait(controller::initialize);

        TextField numberOfItemsField =
                (TextField) getField(controller, "numberOfItemsField");
        Label cartTotalLabel =
                (Label) getField(controller, "cartTotalLabel");

        runOnFxThreadAndWait(() -> numberOfItemsField.setText("2"));
        runPrivateMethod(controller, "handleEnterItems");

        List<TextField> priceFields = (List<TextField>) getField(controller, "priceFields");
        List<TextField> quantityFields = (List<TextField>) getField(controller, "quantityFields");

        runOnFxThreadAndWait(() -> {
            priceFields.get(0).setText("5");
            quantityFields.get(0).setText("2");
            priceFields.get(1).setText("4");
            quantityFields.get(1).setText("3");
        });

        runPrivateMethod(controller, "handleCalculateTotal");

        assertTrue(cartTotalLabel.getText().contains("22.0"));

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            var resultSet = statement.executeQuery(
                    "SELECT total_items, total_cost, language FROM cart_records ORDER BY id DESC LIMIT 1"
            );

            assertTrue(resultSet.next());
            assertEquals(5, resultSet.getInt("total_items"));
            assertEquals(22.0, resultSet.getDouble("total_cost"));
            assertEquals("en_US", resultSet.getString("language"));
        }
    }

    @Test
    void testHandleCalculateTotalUpdatesEachItemTotalLabel() throws Exception {
        TestableShoppingCartController controller = createController();

        runOnFxThreadAndWait(controller::initialize);

        TextField numberOfItemsField =
                (TextField) getField(controller, "numberOfItemsField");

        runOnFxThreadAndWait(() -> numberOfItemsField.setText("2"));
        runPrivateMethod(controller, "handleEnterItems");

        List<TextField> priceFields = (List<TextField>) getField(controller, "priceFields");
        List<TextField> quantityFields = (List<TextField>) getField(controller, "quantityFields");
        List<Label> itemTotalLabels = (List<Label>) getField(controller, "itemTotalLabels");

        runOnFxThreadAndWait(() -> {
            priceFields.get(0).setText("5");
            quantityFields.get(0).setText("2");
            priceFields.get(1).setText("4");
            quantityFields.get(1).setText("3");
        });

        runPrivateMethod(controller, "handleCalculateTotal");

        assertTrue(itemTotalLabels.get(0).getText().contains("10.0"));
        assertTrue(itemTotalLabels.get(1).getText().contains("12.0"));
    }

    @Test
    void testHandleCalculateTotalWithInvalidInputDoesNotSaveToDatabaseAndShowsError() throws Exception {
        TestableShoppingCartController controller = createController();

        runOnFxThreadAndWait(controller::initialize);

        TextField numberOfItemsField =
                (TextField) getField(controller, "numberOfItemsField");

        runOnFxThreadAndWait(() -> numberOfItemsField.setText("1"));
        runPrivateMethod(controller, "handleEnterItems");

        List<TextField> priceFields = (List<TextField>) getField(controller, "priceFields");
        List<TextField> quantityFields = (List<TextField>) getField(controller, "quantityFields");

        runOnFxThreadAndWait(() -> {
            priceFields.get(0).setText("abc");
            quantityFields.get(0).setText("2");
        });

        runPrivateMethod(controller, "handleCalculateTotal");

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            var resultSet = statement.executeQuery("SELECT COUNT(*) AS count FROM cart_records");
            assertTrue(resultSet.next());
            assertEquals(0, resultSet.getInt("count"));
        }

        assertTrue(controller.getLastErrorMessage() != null && !controller.getLastErrorMessage().isEmpty());
    }

    @Test
    void testHandleCalculateTotalWithNegativePriceDoesNotSaveToDatabaseAndShowsError() throws Exception {
        TestableShoppingCartController controller = createController();

        runOnFxThreadAndWait(controller::initialize);

        TextField numberOfItemsField =
                (TextField) getField(controller, "numberOfItemsField");

        runOnFxThreadAndWait(() -> numberOfItemsField.setText("1"));
        runPrivateMethod(controller, "handleEnterItems");

        List<TextField> priceFields = (List<TextField>) getField(controller, "priceFields");
        List<TextField> quantityFields = (List<TextField>) getField(controller, "quantityFields");

        runOnFxThreadAndWait(() -> {
            priceFields.get(0).setText("-5");
            quantityFields.get(0).setText("2");
        });

        runPrivateMethod(controller, "handleCalculateTotal");

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            var resultSet = statement.executeQuery("SELECT COUNT(*) AS count FROM cart_records");
            assertTrue(resultSet.next());
            assertEquals(0, resultSet.getInt("count"));
        }

        assertTrue(controller.getLastErrorMessage() != null && !controller.getLastErrorMessage().isEmpty());
    }

    private static Stream<Object[]> languageTestCases() {
        return Stream.of(
                new Object[]{new Locale.Builder().setLanguage("fi").setRegion("FI").build(), "Finnish"},
                new Object[]{new Locale.Builder().setLanguage("sv").setRegion("SE").build(), "Swedish"},
                new Object[]{new Locale.Builder().setLanguage("ja").setRegion("JP").build(), "Japanese"},
                new Object[]{new Locale.Builder().setLanguage("ar").setRegion("AR").build(), "Arabic"}
        );
    }

    private static Stream<Object[]> confirmLanguageTestCases() {
        return Stream.of(
                new Object[]{"Finnish", new Locale.Builder().setLanguage("fi").setRegion("FI").build()},
                new Object[]{"Swedish", new Locale.Builder().setLanguage("sv").setRegion("SE").build()},
                new Object[]{"Japanese", new Locale.Builder().setLanguage("ja").setRegion("JP").build()},
                new Object[]{"Arabic", new Locale.Builder().setLanguage("ar").setRegion("AR").build()}
        );
    }

    private TestableShoppingCartController createController() throws Exception {
        TestableShoppingCartController controller = new TestableShoppingCartController();

        setField(controller, "titleLabel", new Label());
        setField(controller, "rootVBox", new VBox());
        setField(controller, "languageComboBox", new ComboBox<String>());
        setField(controller, "numberOfItemsField", new TextField());
        setField(controller, "itemsGrid", new GridPane());
        setField(controller, "cartTotalLabel", new Label());
        setField(controller, "languageLabel", new Label());
        setField(controller, "numberOfItemsLabel", new Label());
        setField(controller, "confirmLanguageButton", new Button());
        setField(controller, "enterItemsButton", new Button());
        setField(controller, "calculateTotalButton", new Button());

        return controller;
    }

    private static void clearCartTables() throws Exception {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM cart_items");
            statement.executeUpdate("DELETE FROM cart_records");
            statement.executeUpdate("ALTER TABLE cart_items AUTO_INCREMENT = 1");
            statement.executeUpdate("ALTER TABLE cart_records AUTO_INCREMENT = 1");
        }
    }

    private static void setCurrentLocale(Locale locale) throws Exception {
        Field field = ShoppingCartApplication.class.getDeclaredField("currentLocale");
        field.setAccessible(true);
        field.set(null, locale);
    }

    private static Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = findField(target.getClass(), fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static Object getField(Object target, String fieldName) throws Exception {
        Field field = findField(target.getClass(), fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    private static void runPrivateMethod(Object target, String methodName) throws Exception {
        Method method = target.getClass().getSuperclass().getDeclaredMethod(methodName);
        method.setAccessible(true);

        runOnFxThreadAndWait(() -> {
            try {
                method.invoke(target);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
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

    private static class TestableShoppingCartController extends ShoppingCartController {
        private String lastErrorMessage;

        @Override
        protected void showError(String message) {
            lastErrorMessage = message;
        }

        public String getLastErrorMessage() {
            return lastErrorMessage;
        }
    }
}