package dataaccess;
import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import java.sql.*;
import java.util.ArrayList;

import static dataaccess.DatabaseManager.configureDatabase;

public class MySQLGameDataAccess implements GameDAO{

    private int gameID;
    private String[] createStatements = {
            """ 
              CREATE TABLE IF NOT EXISTS  games (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(128) NOT NULL,
              `blackUsername` varchar(128) NOT NULL,
              `gameName` varchar(128) NOT NULL,
              `chessGame` TEXT NOT NULL,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public MySQLGameDataAccess() throws SQLException, DataAccessException {
        this.gameID = 1;
        configureDatabase(createStatements);
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData newGame = new GameData(gameID, null, null, gameName, game);
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO users (gameID, whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                ps.setString(2, null);
                ps.setString(3, null);
                ps.setString(4, gameName);
                ps.setString(5, new Gson().toJson(game));
                ps.executeUpdate();
                gameID++;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to insert userData");
        }
        return newGame;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT whiteUsername, blackUsername, gameName, chessGame FROM games WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()){
                    if(rs.next()){
                        ChessGame game = new Gson().fromJson(rs.getString("chessGame"), ChessGame.class);
                        return new GameData(gameID, rs.getString("whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"), game);
                    }
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to get gameData");
        }
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
            var statement = "TRUNCATE TABLE games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear table games");
        }
    }
}
