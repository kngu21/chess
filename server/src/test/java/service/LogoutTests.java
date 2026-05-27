package service;

import dataaccess.*;
import handlers.LoginHandler;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutTests {
    private final UserService service;
    public LogoutTests() {
        UserDAO user = new UserDataAccess();
        AuthDAO auth = new AuthDataAccess();
        service = new UserService(user, auth);
    }
    @Test
    void testLogout() throws AlreadyTakenException, DataAccessException {

        UserData user = new UserData("noOne", "oh yeah!", "email.com");

        service.register(user);

        LoginHandler.LoginResult loginResult = service.login("noOne", "oh yeah!");
        String authToken = loginResult.authToken();
        service.logout(authToken);
        assertThrows(UnauthorizedException.class, () -> service.logout(authToken));
    }
    @Test
    void testBadAuth() throws AlreadyTakenException, DataAccessException {
        UserData notTaken = new UserData("noOne", "oh yeah!", "email.com");
        service.register(notTaken);
        assertThrows(UnauthorizedException.class, () -> service.logout("124"));
    }
}
