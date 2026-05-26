package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.*;
import java.util.ArrayList;

public class MySQLGameDataAccess implements GameDAO{

    public MySQLGameDataAccess() throws SQLException, DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {

    };

    private void configureDatabase() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()){
            for (String statement : createStatements){
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database");
        }
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
