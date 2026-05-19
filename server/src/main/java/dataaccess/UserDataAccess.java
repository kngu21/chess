package dataaccess;
import model.UserData;
import java.util.HashMap;

public class UserDataAccess implements UserDAO{
    private HashMap<String, UserData> data;
    public UserDataAccess(){
        this.data = new HashMap<>();
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
