package dataaccess;
import model.AuthData;

import java.sql.SQLException;

public interface AuthDAO {
    AuthData createAuth(String username) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void removeAuth(String authToken) throws DataAccessException;
    void clear() throws DataAccessException, SQLException;
}
