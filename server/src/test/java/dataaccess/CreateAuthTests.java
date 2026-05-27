package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;
import service.AlreadyTakenException;

import static org.junit.jupiter.api.Assertions.*;

public class CreateAuthTests extends BaseTest{
    @Test
    void successfulCreateAuth() throws DataAccessException {
        service2.register(new UserData("kai", "password", "cool@email.com"));
        assertDoesNotThrow(() ->{
            service2.register(new UserData("jim", "howdy", "coolio@email.com"));
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
