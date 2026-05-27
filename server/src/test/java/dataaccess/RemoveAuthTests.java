package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;
import service.AlreadyTakenException;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class RemoveAuthTests extends BaseTest{
    @Test
    void successfulRemoveAuth() throws DataAccessException {
        var result = service2.register(new UserData("kai", "password", "cool@email.com"));
        String token = result.authToken();
        assertNotNull(auth.getAuth(token));
        auth.removeAuth(token);
        assertNull(auth.getAuth(token));
    }
    @Test
    void failedRemoveAuth() throws AlreadyTakenException, DataAccessException, SQLException {
        var result = service2.register(new UserData("kai", "password", "cool@email.com"));
        String token = result.authToken();
        auth.removeAuth(token);
        assertDoesNotThrow(() -> auth.removeAuth(token));
    }
}
