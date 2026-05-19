package dataaccess;
import model.AuthData;
import model.UserData;
import java.util.HashMap;

public class UserDataAccess implements UserDAO{
    private final HashMap<String, UserData> data;
    public UserDataAccess(HashMap<String, UserData> data){
        this.data = data;
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
    }
}
