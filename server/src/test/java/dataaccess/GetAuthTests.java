package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;
import service.AlreadyTakenException;
import service.UnauthorizedException;

import static org.junit.jupiter.api.Assertions.*;

public class GetAuthTests extends BaseTest{
    @Test
    void successfulGetAuth() throws DataAccessException {
        service2.register(new UserData("kai", "password", "cool@email.com"));
        var login = service2.login("kai", "password");
        assertDoesNotThrow(() ->{
            service.createGame(login.authToken(), "The Best Game");
        });
    }
    @Test
    void failedGetAuth() throws AlreadyTakenException, DataAccessException {
        service2.register(new UserData("kai", "password", "cool@email.com"));
        var loginResult = service2.login("kai", "password");
        var authData = auth.getAuth(loginResult.authToken());
        assertThrows(UnauthorizedException.class, () -> {
            service2.logout(authData.authToken()+1);
        });
    }
}