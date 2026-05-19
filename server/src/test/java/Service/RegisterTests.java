package Service;
import dataaccess.AuthDAO;
import dataaccess.AuthDataAccess;
import dataaccess.UserDAO;
import dataaccess.UserDataAccess;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterTests {
    private UserDAO user;
    private AuthDAO auth;
    private UserService service;
    public RegisterTests() {
        user = new UserDataAccess();
        auth = new AuthDataAccess();
        service = new UserService(user, auth);
    }
    @Test
    void testNewUser() throws AlreadyTakenException {
        // Arrange
        UserData notTaken = new UserData("noOne", "oh yeah!", "email.com");
        service.register(notTaken);
         assertNotNull(user.getUser("noOne"));
    }
    @Test
    void testExistingUser(){
        user.createUser(new UserData("kngu21", "oh yeah!", "email.com"));
        UserData taken = new UserData("kngu21", "oh yeah!", "email.com");
        assertThrows(AlreadyTakenException.class, () -> service.register(taken));

    }
    @Test
    void testAuthToken(){
        auth.createAuth("kngu21");
    }
}
