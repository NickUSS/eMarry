package me.emojiado.emarry.database;

import me.emojiado.emarry.configuration.ConfigUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static ConfigUtil configUtil;

    public static void setConfigUtil(ConfigUtil configUtil) {
        Database.configUtil = configUtil;
    }

    public static Connection getConnection() throws SQLException {
        String host = configUtil.getConfigString("database.host");
        int port = configUtil.getConfigInt("database.port");
        String database = configUtil.getConfigString("database.database");
        String username = configUtil.getConfigString("database.username");
        String password = configUtil.getConfigString("database.password");

        String jdbcUrl = "jdbc:mariadb://" + host + ":" + port + "/" + database + "?useSSL=false";
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(jdbcUrl, username, password);
            if (connection.isValid(5)) {
                System.out.println("Conectado con éxito a la base de datos.");
                createTables(connection);
            } else {
                System.out.println("No se ha podido conectar a la base de datos.");
            }
        } catch (SQLException e) {
            System.out.println("Se ha producido un error al establecer la conexión con la base de datos.");
            e.printStackTrace();
            throw e;
        }

        return connection;
    }

    private static void createTables(Connection connection) {
        String createPlayersTableQuery = "CREATE TABLE IF NOT EXISTS marriage_players (" +
                "player_id VARCHAR(36) PRIMARY KEY, " +
                "partner_id VARCHAR(36) NOT NULL, " +
                "marriage_date BIGINT NOT NULL)";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createPlayersTableQuery);
        } catch (SQLException e) {
            System.out.println("An error occurred while creating the tables.");
            e.printStackTrace();
        }
    }


}

