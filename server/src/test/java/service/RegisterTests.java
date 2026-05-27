package service;
import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterTests {
    private final UserDAO user;
    private final AuthDAO auth;
    private final UserService service;

    public RegisterTests() {
        user = new UserDataAccess();
        auth = new AuthDataAccess();
        service = new UserService(user, auth);
    }
    @Test
    void testNewUser() throws AlreadyTakenException, DataAccessException {
        // Arrange
        UserData notTaken = new UserData("noOne", "oh yeah!", "email.com");
        service.register(notTaken);
         assertNotNull(user.getUser("noOne"));
    }
    @Test
    void testExistingUser() throws DataAccessException {
        user.createUser(new UserData("kngu21", "oh yeah!", "email.com"));
        UserData taken = new UserData("kngu21", "oh yeah!", "email.com");
        assertThrows(AlreadyTakenException.class, () -> service.register(taken));
    }
    @Test
    void testAuthToken() throws DataAccessException {
        auth.createAuth("kngu21");
    }
}
