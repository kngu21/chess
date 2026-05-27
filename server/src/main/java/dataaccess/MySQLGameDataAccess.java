package dataaccess;
import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import java.sql.*;
import java.util.ArrayList;

import static dataaccess.DatabaseManager.configureDatabase;

public class MySQLGameDataAccess implements GameDAO{

    private String[] createStatements = {
            """ 
              CREATE TABLE IF NOT EXISTS  games (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(128),
              `blackUsername` varchar(128),
              `gameName` varchar(128) NOT NULL,
              `chessGame` TEXT NOT NULL,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public MySQLGameDataAccess() throws SQLException, DataAccessException {
        configureDatabase(createStatements);
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        ChessGame game = new ChessGame();
        String json = new Gson().toJson(game);
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                ps.setNull(1, Types.VARCHAR);
                ps.setNull(2, Types.VARCHAR);
                ps.setString(3, gameName);
                ps.setString(4, json);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if(rs.next()){
                        int gameID = rs.getInt(1);
                        return new GameData(gameID, null, null, gameName, game);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create gameData");
        }
        return null;
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
    public ArrayList<GameData> listGames() throws DataAccessException {
        ArrayList<GameData> games = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    int id = rs.getInt("gameID");
                    String white = rs.getString("whiteUsername");
                    String black = rs.getString("blackUsername");
                    String name = rs.getString("gameName");
                    ChessGame game = new Gson().fromJson(rs.getString("chessGame"), ChessGame.class);
                    games.add(new GameData(id, white, black, name, game));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to list games");
        }
        return games;
    }

    @Override
    public void replaceGame(GameData newGame) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE games SET whiteUsername=?, blackUsername=?, gameName=?, chessGame=? WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, newGame.whiteUsername());
                ps.setString(2, newGame.blackUsername());
                ps.setString(3, newGame.gameName());
                ps.setString(4, new Gson().toJson(newGame));
                ps.setInt(5, newGame.gameID());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to replace game");
        }
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
