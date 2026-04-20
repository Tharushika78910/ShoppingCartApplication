package com.example.shoppingcart;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public class ShoppingCartApplication extends Application {

    private static Stage primaryStage;
    private static Locale currentLocale = Locale.US;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        loadView();
        primaryStage.show();
    }

    public static void switchLanguage(Locale locale) {
        currentLocale = locale;
        try {
            loadView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String buildLanguageCode(Locale locale) {
        return locale.getLanguage() + "_" + locale.getCountry();
    }

    static String buildWindowTitle(String appTitle) {
        return appTitle + " / Dilmi Tharushika";
    }

    private static void loadView() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                ShoppingCartApplication.class.getResource("/shopping-cart-view.fxml")
        );

        Scene scene = new Scene(loader.load(), 700, 500);
        primaryStage.setScene(scene);

        LocalizationService localizationService = new LocalizationService();
        Map<String, String> texts = localizationService.getLocalizedStrings(getCurrentLanguageCode());
        String appTitle = texts.getOrDefault("app.title", "Shopping Cart");

        primaryStage.setTitle(buildWindowTitle(appTitle));
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static String getCurrentLanguageCode() {
        return buildLanguageCode(getCurrentLocale());
    }

    public static void main(String[] args) {
        launch(args);
    }
}