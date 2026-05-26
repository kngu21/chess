package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLUserDataAccess implements UserDAO{

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
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void createUser(UserData userData) {

    }

    @Override
    public void clear() {

    }
}
