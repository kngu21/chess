package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

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
              `password` varchar(128) NOT NULL,
              `email` varchar(128) NOT NULL,
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
            var statement = "SELECT password, email FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()){
                    if(rs.next()){
                        return new UserData(username, rs.getString("password"), rs.getString("email"));
                    }
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to get userData");
        }
        return null;
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, userData.username());
                ps.setString(2, hashedPassword);
                ps.setString(3, userData.email());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to insert userData");
        }
    }

    @Override
    public void clear() throws SQLException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE TABLE users";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear table users");
        }
    }
}
