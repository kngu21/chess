package Service;

import dataaccess.AuthDAO;
import dataaccess.AuthDataAccess;
import dataaccess.UserDAO;
import dataaccess.UserDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTests {
    private UserDAO user;
    private AuthDAO auth;
    private UserService service;
    public LoginTests() {
        user = new UserDataAccess();
        auth = new AuthDataAccess();
        service = new UserService(user, auth);
    }
    @Test
    void testLogin() throws AlreadyTakenException {
        // Arrange
        UserData notTaken = new UserData("noOne", "oh yeah!", "email.com");
        service.register(notTaken);
        AuthData authData = auth.getAuth(notTaken.username());
        String authToken = authData.authToken();
        assertNotNull(service.login(notTaken.username(), notTaken.password(), authToken));
    }
    @Test
    void testWrongPassword(){
        user.createUser(new UserData("kngu21", "oh yeah!", "email.com"));
        UserData taken = new UserData("kngu21", "yeah!", "email.com");
        assertThrows(BadRequestException.class, () -> service.login(taken.username(), taken.password(), "1234"));
    }
    @Test
    void testNotRegistered(){
        UserData taken = new UserData("kngu21", "oh yeah!", "email.com");
        assertThrows(BadRequestException.class, () -> service.login(taken.username(), taken.password(), "1234"));
    }
}
