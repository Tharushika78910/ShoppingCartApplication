import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShoppingCartController {

    @FXML
    private Label titleLabel;

    @FXML
    private VBox rootVBox;

    @FXML
    private ComboBox<String> languageComboBox;

    @FXML
    private TextField numberOfItemsField;

    @FXML
    private GridPane itemsGrid;

    @FXML
    private Label cartTotalLabel;

    @FXML
    private Label languageLabel;

    @FXML
    private Label numberOfItemsLabel;

    @FXML
    private Button confirmLanguageButton;

    @FXML
    private Button enterItemsButton;

    @FXML
    private Button calculateTotalButton;

    private final List<TextField> priceFields = new ArrayList<>();
    private final List<TextField> quantityFields = new ArrayList<>();
    private final List<Label> itemTotalLabels = new ArrayList<>();

    private final LocalizationService localizationService = new LocalizationService();
    private final CartService cartService = new CartService();

    private Map<String, String> texts;

    @FXML
    public void initialize() {
        languageComboBox.getItems().addAll(
                "English",
                "Finnish",
                "Swedish",
                "Japanese",
                "Arabic"
        );

        Locale current = ShoppingCartApplication.getCurrentLocale();
        if (current.equals(new Locale("fi", "FI"))) {
            languageComboBox.setValue("Finnish");
        } else if (current.equals(new Locale("sv", "SE"))) {
            languageComboBox.setValue("Swedish");
        } else if (current.equals(new Locale("ja", "JP"))) {
            languageComboBox.setValue("Japanese");
        } else if (current.equals(new Locale("ar", "AR"))) {
            languageComboBox.setValue("Arabic");
        } else {
            languageComboBox.setValue("English");
        }

        loadTexts();
        applyTexts();
        applyLanguageDirection(current);
    }

    private void loadTexts() {
        String languageCode = ShoppingCartApplication.getCurrentLanguageCode();
        texts = localizationService.getLocalizedStrings(languageCode);
    }

    private String t(String key, String fallback) {
        return texts.getOrDefault(key, fallback);
    }

    private void applyTexts() {
        titleLabel.setText(t("app.title", "Shopping Cart"));
        languageLabel.setText(t("language.label", "Select Language"));
        numberOfItemsLabel.setText(t("number.of.items", "Number of Items"));
        confirmLanguageButton.setText(t("confirm.language", "Confirm Language"));
        enterItemsButton.setText(t("enter.items", "Enter Items"));
        calculateTotalButton.setText(t("calculate.total", "Calculate Total"));
        cartTotalLabel.setText(t("cart.total", "Cart Total:") + " 0.0");
    }

    @FXML
    private void handleConfirmLanguage() {
        String selected = languageComboBox.getValue();

        Locale locale = switch (selected) {
            case "Finnish" -> new Locale("fi", "FI");
            case "Swedish" -> new Locale("sv", "SE");
            case "Japanese" -> new Locale("ja", "JP");
            case "Arabic" -> new Locale("ar", "AR");
            default -> Locale.US;
        };

        ShoppingCartApplication.switchLanguage(locale);
    }

    @FXML
    private void handleEnterItems() {
        itemsGrid.getChildren().clear();
        priceFields.clear();
        quantityFields.clear();
        itemTotalLabels.clear();

        int count;
        try {
            count = Integer.parseInt(numberOfItemsField.getText().trim());
            if (count <= 0) {
                showError(t("error.number.gt.zero", "Please enter a number greater than 0."));
                return;
            }
        } catch (NumberFormatException e) {
            showError(t("error.invalid.integer", "Please enter a valid integer."));
            return;
        }

        Locale current = ShoppingCartApplication.getCurrentLocale();
        boolean isArabic = isArabic(current);

        for (int i = 0; i < count; i++) {
            Label itemLabel = new Label(t("item.label", "Item") + " " + (i + 1));

            TextField priceField = new TextField();
            priceField.setPromptText(t("enter.price", "Enter price"));

            TextField quantityField = new TextField();
            quantityField.setPromptText(t("enter.quantity", "Enter quantity"));

            Label totalLabel = new Label(t("item.total", "Item Total:") + " 0.0");

            if (isArabic) {
                itemLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                totalLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                priceField.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                quantityField.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                priceField.setAlignment(Pos.CENTER_RIGHT);
                quantityField.setAlignment(Pos.CENTER_RIGHT);
            } else {
                itemLabel.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
                totalLabel.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
                priceField.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
                quantityField.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
                priceField.setAlignment(Pos.CENTER_LEFT);
                quantityField.setAlignment(Pos.CENTER_LEFT);
            }

            priceFields.add(priceField);
            quantityFields.add(quantityField);
            itemTotalLabels.add(totalLabel);

            itemsGrid.add(itemLabel, 0, i);
            itemsGrid.add(priceField, 1, i);
            itemsGrid.add(quantityField, 2, i);
            itemsGrid.add(totalLabel, 3, i);
        }
    }

    @FXML
    private void handleCalculateTotal() {
        CartCalculator calculator = new CartCalculator();
        List<CartItem> items = new ArrayList<>();

        try {
            for (int i = 0; i < priceFields.size(); i++) {
                double price = Double.parseDouble(priceFields.get(i).getText().trim());
                int quantity = Integer.parseInt(quantityFields.get(i).getText().trim());

                CartItem item = new CartItem(price, quantity);
                items.add(item);

                itemTotalLabels.get(i).setText(
                        t("item.total", "Item Total:") + " " + item.getTotalCost()
                );
            }

            double total = calculator.calculateCartTotal(items);
            cartTotalLabel.setText(t("cart.total", "Cart Total:") + " " + total);

            cartService.saveCart(
                    total,
                    ShoppingCartApplication.getCurrentLanguageCode(),
                    items
            );

        } catch (NumberFormatException e) {
            showError(t("error.invalid.price.quantity",
                    "Please enter valid numbers for price and quantity."));
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void applyLanguageDirection(Locale locale) {
        if (isArabic(locale)) {
            rootVBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            numberOfItemsField.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            numberOfItemsField.setAlignment(Pos.CENTER_RIGHT);
            cartTotalLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        } else {
            rootVBox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            numberOfItemsField.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            numberOfItemsField.setAlignment(Pos.CENTER_LEFT);
            cartTotalLabel.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        }
    }

    private boolean isArabic(Locale locale) {
        return locale.equals(new Locale("ar", "AR"));
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}