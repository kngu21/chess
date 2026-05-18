package dataaccess;
import model.AuthData;
import model.UserData;

public interface UserDAO {
    UserData getUser(String username);
    void createUser(UserData userData);
    AuthData createAuth(String authData);
    void clear();
}
