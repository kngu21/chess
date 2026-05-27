package dataaccess;
import model.UserData;

import java.sql.SQLException;

public interface UserDAO {
    UserData getUser(String username) throws DataAccessException;
    void createUser(UserData userData) throws DataAccessException;
    void clear() throws SQLException, DataAccessException;
}
