import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ShoppingCartApplication {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Locale locale = chooseLocale(scanner);
        ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", locale);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);

        CartCalculator calculator = new CartCalculator();
        List<CartItem> items = new ArrayList<>();

        System.out.println(messages.getString("welcome"));

        int numberOfItems = readNonNegativeInt(scanner, messages.getString("enter.number.of.items"));

        for (int i = 1; i <= numberOfItems; i++) {
            System.out.println(messages.getString("item.label") + " " + i);

            double price = readNonNegativeDouble(scanner, messages.getString("enter.price"));
            int quantity = readNonNegativeInt(scanner, messages.getString("enter.quantity"));

            CartItem item = new CartItem(price, quantity);
            items.add(item);

            double itemTotal = calculator.calculateItemTotal(price, quantity);
            System.out.println(messages.getString("item.total") + " " + currencyFormat.format(itemTotal));
        }

        double cartTotal = calculator.calculateCartTotal(items);
        System.out.println(messages.getString("cart.total") + " " + currencyFormat.format(cartTotal));

        scanner.close();
    }

    private static Locale chooseLocale(Scanner scanner) {
        System.out.println("Select language / Valitse kieli / Välj språk:");
        System.out.println("1. English");
        System.out.println("2. Suomi");
        System.out.println("3. Svenska");
        System.out.print("Choice: ");

        String choice = scanner.nextLine().trim();

        return switch (choice) {
            case "2" -> new Locale("fi", "FI");
            case "3" -> new Locale("sv", "SE");
            default -> new Locale("en", "US");
        };
    }

    private static int readNonNegativeInt(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt + " ");
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value < 0) {
                    System.out.println("Value cannot be negative. Try again.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid integer. Try again.");
            }
        }
    }

    private static double readNonNegativeDouble(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt + " ");
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value < 0) {
                    System.out.println("Value cannot be negative. Try again.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }
}