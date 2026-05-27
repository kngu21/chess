package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.configureDatabase;

public class MySQLUserDataAccess implements UserDAO{

    private String[] createStatements = {
            """ 
              CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(128) NOT NULL,
              `userData` TEXT NOT NULL,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public MySQLUserDataAccess() throws SQLException, DataAccessException {
        configureDatabase(createStatements);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT userData FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()){
                    if(rs.next()){
                        return readUser(rs);
                    }
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to get userData");
        }
        return null;
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var json = rs.getString("userData");
        return new Gson().fromJson(json, UserData.class);
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO users (username, userData) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                String json = new Gson().toJson(userData);
                ps.setString(1, userData.username());
                ps.setString(2, json);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to insert userData");
        }
    }

    @Override
    public void clear() throws SQLException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "DROP TABLE users";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to remove table users");
        }
        DatabaseManager.configureDatabase(createStatements);
    }
}
