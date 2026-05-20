package dataaccess;
import model.AuthData;

public interface AuthDAO {
    AuthData createAuth(String username);
    AuthData getAuth(String authToken);
    void removeAuth(String authToken);
    void clear();
}
