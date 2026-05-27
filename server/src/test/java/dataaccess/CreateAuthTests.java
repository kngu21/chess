package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;
import service.AlreadyTakenException;
import service.UnauthorizedException;

import static org.junit.jupiter.api.Assertions.*;

public class CreateAuthTests extends BaseTest{
    @Test
    void successfulCreateAuth() throws DataAccessException {
        service2.register(new UserData("kai", "password", "cool@email.com"));
        assertDoesNotThrow(() ->{
            service2.register(new UserData("jim", "howdy", "coolio@email.com"));
            var login = service2.login("kai", "password");
            auth.getAuth(login.authToken());
        });
    }
    @Test
    void failedCreateAuth() throws AlreadyTakenException, DataAccessException {
        service2.register(new UserData("kai", "password", "cool@email.com"));
        var first = service2.login("kai", "password");
        assertThrows(UnauthorizedException.class, () -> {
            service2.register(new UserData("jim", "howdy", "cool@email.com"));
            var second = service2.login("jim", "howdy");
            service2.logout(second.authToken() + "i");
        });
    }
}
