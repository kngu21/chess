package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;
import service.AlreadyTakenException;

import static org.junit.jupiter.api.Assertions.*;

public class CreateUserTests extends BaseTest{
    @Test
    void successfulCreateAuth() throws DataAccessException {
        service2.register(new UserData("jim", "howdy", "cool@email.com"));
        assertDoesNotThrow(() ->{
            service2.login("jim", "howdy");
        });
    }
    @Test
    void failedCreateAuth() throws AlreadyTakenException, DataAccessException {
        service2.register(new UserData("kai", "password", "cool@email.com"));

        assertThrows(AlreadyTakenException.class, () -> {
            service2.register(new UserData("kai", "password", "cool@email.com"));
        });
    }
}