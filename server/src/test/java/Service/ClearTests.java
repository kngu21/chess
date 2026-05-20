package Service;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClearTests {
    private UserDAO user;
    private AuthDAO auth;
    private GameDAO game;
    private VoidService service;
    public ClearTests() {
        user = new UserDataAccess();
        auth = new AuthDataAccess();
        game = new GameDataAccess();
        service = new VoidService(user, auth, game);
    }
    @Test
    void testNewUser() throws DataAccessException {
        // Arrange
        user.createUser(new UserData("kngu21", "oh yeah!", "email.com"));
        auth.createAuth("kngu21");
        game.createGame("gamey");
        service.clear();
        assertNotNull(user.getUser("kngu21") == null && auth.getAuth("kngu21") == null && game.getGame("gamey") == null);
    }
}