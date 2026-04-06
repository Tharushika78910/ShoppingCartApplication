import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class CartService {

    public void saveCart(double totalCost, String language, List<CartItem> items) {
        String insertCartRecord = "INSERT INTO cart_records (total_items, total_cost, language) VALUES (?, ?, ?)";
        String insertCartItem = "INSERT INTO cart_items (cart_record_id, item_number, price, quantity, subtotal) VALUES (?, ?, ?, ?, ?)";

        int totalItems = items.stream().mapToInt(CartItem::getQuantity).sum();

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement cartStatement =
                         connection.prepareStatement(insertCartRecord, Statement.RETURN_GENERATED_KEYS)) {

                cartStatement.setInt(1, totalItems);
                cartStatement.setDouble(2, totalCost);
                cartStatement.setString(3, language);
                cartStatement.executeUpdate();

                ResultSet keys = cartStatement.getGeneratedKeys();
                int cartRecordId = -1;

                if (keys.next()) {
                    cartRecordId = keys.getInt(1);
                }

                try (PreparedStatement itemStatement = connection.prepareStatement(insertCartItem)) {
                    for (int i = 0; i < items.size(); i++) {
                        CartItem item = items.get(i);

                        itemStatement.setInt(1, cartRecordId);
                        itemStatement.setInt(2, i + 1);
                        itemStatement.setDouble(3, item.getPrice());
                        itemStatement.setInt(4, item.getQuantity());
                        itemStatement.setDouble(5, item.getTotalCost());
                        itemStatement.addBatch();
                    }

                    itemStatement.executeBatch();
                }

                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}