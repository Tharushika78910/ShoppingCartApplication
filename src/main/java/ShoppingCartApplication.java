import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

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

    private static void loadView() throws IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("MessagesBundle", currentLocale);

        FXMLLoader loader = new FXMLLoader(
                ShoppingCartApplication.class.getResource("/shopping-cart-view.fxml"),
                bundle
        );

        Scene scene = new Scene(loader.load(), 700, 500);
        primaryStage.setTitle(bundle.getString("app.title"));
        primaryStage.setScene(scene);
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static void main(String[] args) {
        launch(args);
    }
}