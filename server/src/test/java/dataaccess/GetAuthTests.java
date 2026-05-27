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
            service2.login("kai", "password");
        });
    }
    @Test
    void failedGetAuth() throws AlreadyTakenException, DataAccessException {
        service2.register(new UserData("kai", "password", "cool@email.com"));

        assertThrows(UnauthorizedException.class, () -> {
            service2.login("kai", "passWord");
        });
    }
}