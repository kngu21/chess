package dataaccess;
import model.AuthData;
import java.util.UUID;
import java.util.HashMap;

public class AuthDataAccess implements AuthDAO{
    private final HashMap<String, AuthData> authTokens;
    public AuthDataAccess(HashMap<String, AuthData> authTokens){
        this.authTokens = authTokens;
    }
    public AuthData createAuth(String username){
        String newUUID = UUID.randomUUID().toString();
        AuthData newAuth = new AuthData(newUUID, username);
        authTokens.put(newUUID, newAuth);
        return newAuth;
    }

    @Override
    public AuthData createAuth(AuthData authData) {
        return null;
    }
    public void clear(){
        authTokens.clear();
    }
}
