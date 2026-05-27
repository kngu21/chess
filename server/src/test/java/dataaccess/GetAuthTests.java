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
        assertDoesNotThrow(() ->{
            var loginResult = service2.login("kai", "password");
            service2.logout(loginResult.authToken());
        });
    }
    @Test
    void failedGetAuth() throws AlreadyTakenException, DataAccessException {
        service2.register(new UserData("kai", "password", "cool@email.com"));

        assertThrows(UnauthorizedException.class, () -> {
            var loginResult = service2.login("kai", "password");
            service2.logout(loginResult.authToken() + "i");
        });
    }
}