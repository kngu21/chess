package dataaccess;
import model.AuthData;
import model.UserData;
import java.util.HashMap;

public class UserDataAccess implements UserDAO{
    private final HashMap<String, UserData> data;
    private final HashMap<String, AuthData> authTokens;
    public UserDataAccess(HashMap<String, UserData> data, HashMap<String, AuthData> authTokens){
        this.data = data;
        this.authTokens = authTokens;
    }
    public UserData getUser(String username){
        return data.get(username);
    }

    public void createUser(UserData userData) {
        data.put(userData.username(), userData);
    }

    @Override
    public void clear() {
        data.clear();
        authTokens.clear();
    }
}
