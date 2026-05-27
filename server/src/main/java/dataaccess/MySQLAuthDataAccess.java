package dataaccess;

import model.AuthData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLAuthDataAccess implements AuthDAO{

    private String[] createStatements = {
            """ 
              CREATE TABLE IF NOT EXISTS  auths (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(128) NOT NULL,
              PRIMARY KEY (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public MySQLAuthDataAccess() throws DataAccessException, SQLException {
        DatabaseManager.configureDatabase(createStatements);
    }


    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String newUUID = UUID.randomUUID().toString();
        AuthData newAuth = new AuthData(newUUID, username);
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, newUUID);
                ps.setString(2, username);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to insert authData");
        }
        return newAuth;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM auths WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()){
                    if(rs.next()){
                        return new AuthData(authToken, rs.getString("username"));
                    }
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to get authData");
        }
        return null;
    }

    @Override
    public void removeAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM auths WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to remove authData");
        }
    }

    @Override
    public void clear() throws SQLException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE TABLE auths";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear table auths");
        }
    }
}
