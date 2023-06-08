package me.emojiado.emarry.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.emojiado.emarry.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class MarryPlayer {
    private UUID playerId;
    private UUID partnerId;
    private long marriageDate;

    public static MarryPlayer loadFromDatabase(UUID playerId) {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM marriage_players WHERE player_id = ?")) {
            statement.setString(1, playerId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    UUID partnerId = UUID.fromString(resultSet.getString("partner_id"));
                    long marriageDate = resultSet.getLong("marriage_date");
                    return new MarryPlayer(playerId, partnerId, marriageDate);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveToDatabase() {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "REPLACE INTO marriage_players (player_id, partner_id, marriage_date) VALUES (?, ?, ?)")) {
            statement.setString(1, playerId.toString());
            statement.setString(2, partnerId.toString());
            statement.setLong(3, marriageDate);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

