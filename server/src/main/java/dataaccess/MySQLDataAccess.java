package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import com.google.gson.Gson;
import model.*;
import dataaccess.DataAccessException;

import java.sql.*;
import java.util.ArrayList;

public class MySQLDataAccess implements AuthDAO, UserDAO, GameDAO{

    public MySQLDataAccess() throws SQLException, DataAccessException {
        configureDatabase();
    }
    @Override
    public AuthData createAuth(String username) {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void removeAuth(String authToken) {

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
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void createUser(UserData userData) {

    }

    @Override
    public void clear() {

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
}
