package Service;

import Handlers.LoginHandler;
import Handlers.RegisterHandler;
import dataaccess.AuthDAO;
import dataaccess.AuthDataAccess;
import dataaccess.UserDAO;
import dataaccess.UserDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutTests {
    private UserDAO user;
    private AuthDAO auth;
    private UserService service;
    public LogoutTests() {
        user = new UserDataAccess();
        auth = new AuthDataAccess();
        service = new UserService(user, auth);
    }
    @Test
    void testLogout() throws AlreadyTakenException {
        // Arrange
        UserData notTaken = new UserData("noOne", "oh yeah!", "email.com");
        service.register(notTaken);
        LoginHandler.LoginResult result = service.login("noOne", "oh yeah!");
        String newAuth = result.authToken();
        assertDoesNotThrow(() -> service.logout(newAuth));
    }
    @Test
    void testBadAuth() throws AlreadyTakenException{
        UserData notTaken = new UserData("noOne", "oh yeah!", "email.com");
        service.register(notTaken);
        assertThrows(UnauthorizedException.class, () -> service.logout("124"));
    }
}
