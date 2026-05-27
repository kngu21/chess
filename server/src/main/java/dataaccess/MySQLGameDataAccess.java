package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.*;
import java.util.ArrayList;

import static dataaccess.DatabaseManager.configureDatabase;

public class MySQLGameDataAccess implements GameDAO{

    public MySQLGameDataAccess() throws SQLException, DataAccessException {
        String[] createStatements = {
                """ 
              CREATE TABLE IF NOT EXISTS  games (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(128) NOT NULL,
              `blackUsername` varchar(128) NOT NULL,
              `gameName` varchar(128) NOT NULL,
              `chessGame` varchar(256) NOT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(whiteUsername),
              INDEX(blackUsername),
              INDEX(gameName),
              INDEX(chessGame)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };
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
    public void clear() {

    }
}
