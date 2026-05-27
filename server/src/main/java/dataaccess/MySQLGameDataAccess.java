package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.*;
import java.util.ArrayList;

import static dataaccess.DatabaseManager.configureDatabase;

public class MySQLGameDataAccess implements GameDAO{

    private final String[] createStatements = {

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
    public void clear() {

    }
}
