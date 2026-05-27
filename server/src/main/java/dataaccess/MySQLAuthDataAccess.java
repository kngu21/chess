package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLAuthDataAccess implements AuthDAO{

    private final String[] createStatements = {

    };

    private void configureDatabase() throws DataAccessException, SQLException {
        DatabaseManager.createAuthTable();
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
    public void clear() {

    }
}
