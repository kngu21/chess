package dataaccess;
import model.AuthData;
import java.util.UUID;
import java.util.HashMap;

public class AuthDataAccess implements AuthDAO{
    private HashMap<String, AuthData> authTokens;
    public AuthDataAccess(){
        this.authTokens = new HashMap<>();
    }
    public AuthData createAuth(String username){
        String newUUID = UUID.randomUUID().toString();
        AuthData newAuth = new AuthData(newUUID, username);
        authTokens.put(newUUID, newAuth);
        return newAuth;
    }

    public AuthData getAuth(String username){
        return authTokens.get(username);
    }

    public void clear(){
        authTokens.clear();
    }
}
