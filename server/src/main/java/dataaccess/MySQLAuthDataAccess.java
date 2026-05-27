package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLAuthDataAccess implements AuthDAO{

    private final String[] createStatements = {
            """ 
              CREATE TABLE IF NOT EXISTS  auths (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(128) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public MySQLAuthDataAccess() throws SQLException, DataAccessException {
        DatabaseManager.configureDatabase(createStatements);
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
