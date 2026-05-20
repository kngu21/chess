package dataaccess;
import model.AuthData;
import java.util.UUID;
import java.util.HashMap;

public class AuthDataAccess implements AuthDAO{
    private final HashMap<String, AuthData> authTokens;
    public AuthDataAccess(){
        this.authTokens = new HashMap<>();
    }
    public AuthData createAuth(String username){
        String newUUID = UUID.randomUUID().toString();
        AuthData newAuth = new AuthData(newUUID, username);
        authTokens.put(newUUID, newAuth);
        return newAuth;
    }

    public AuthData getAuth(String authToken){
        return authTokens.get(authToken);
    }

    public void removeAuth(String authToken){
        authTokens.remove(authToken);
    }

    public void clear(){
        authTokens.clear();
    }
}
