import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class LocalizationService {

    public Map<String, String> getLocalizedStrings(String language) {
        Map<String, String> texts = new HashMap<>();

        String sql = "SELECT `key`, value FROM localization_strings WHERE language = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, language);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                texts.put(resultSet.getString("key"), resultSet.getString("value"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return texts;
    }
}