package dataaccess;
import model.GameData;
import java.sql.*;
import java.util.ArrayList;

import static dataaccess.DatabaseManager.configureDatabase;

public class MySQLGameDataAccess implements GameDAO{

    private String[] createStatements = {
            """ 
              CREATE TABLE IF NOT EXISTS  games (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `gameData` TEXT NOT NULL,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public MySQLGameDataAccess() throws SQLException, DataAccessException {
        configureDatabase(createStatements);
    }

    @Override
    public GameData createGame(String username) {
        return null;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() {
        return null;
    }

    @Override
    public void replaceGame(GameData newGame) {

    }

    @Override
    public void clear() throws SQLException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "DROP TABLE games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to remove table games");
        }
        DatabaseManager.configureDatabase(createStatements);
    }
}
