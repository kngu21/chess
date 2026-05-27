package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;
import service.AlreadyTakenException;
import service.UnauthorizedException;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class GetUserTests extends BaseTest{
    @Test
    void successfulGetUser() throws DataAccessException {
        UserData newData = new UserData("kai", "password", "cool@email.com");
        service2.register(newData);
        assertEquals(user.getUser("kai").email(), newData.email() );
    }
    @Test
    void failedGetUser() throws AlreadyTakenException, DataAccessException, SQLException {
        service2.register(new UserData("kai", "password", "cool@email.com"));
        user.clear();
        assertThrows(UnauthorizedException.class, () -> {
            service2.login("kai", "passWord");
        });
    }
}
