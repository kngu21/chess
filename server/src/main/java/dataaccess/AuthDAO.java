package dataaccess;
import model.AuthData;

public interface AuthDAO {
    AuthData createAuth(String username);
    AuthData getAuth(String username);
    void clear();
}
