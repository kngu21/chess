package service;

import dataaccess.AuthDAO;
import dataaccess.AuthDataAccess;
import dataaccess.UserDAO;
import dataaccess.UserDataAccess;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTests {
    private final UserDAO user;
    private final UserService service;
    public LoginTests() {
        user = new UserDataAccess();
        AuthDAO auth = new AuthDataAccess();
        service = new UserService(user, auth);
    }
    @Test
    void testLogin() throws AlreadyTakenException {
        // Arrange
        UserData notTaken = new UserData("noOne", "oh yeah!", "email.com");
        service.register(notTaken);
        assertNotNull(service.login("noOne", "oh yeah!"));
    }
    @Test
    void testWrongPassword(){
        user.createUser(new UserData("kngu21", "oh yeah!", "email.com"));
        UserData taken = new UserData("kngu21", "yeah!", "email.com");
        assertThrows(UnauthorizedException.class, () -> service.login(taken.username(), taken.password()));
    }
    @Test
    void testNotRegistered(){
        UserData taken = new UserData("kngu21", "oh yeah!", "email.com");
        assertThrows(UnauthorizedException.class, () -> service.login(taken.username(), taken.password()));
    }
}
