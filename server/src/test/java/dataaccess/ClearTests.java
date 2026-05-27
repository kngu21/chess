package dataaccess;
import model.UserData;
import org.junit.jupiter.api.Test;
import service.VoidService;

import java.sql.SQLException;

public class ClearTests {
    private final UserDAO user;
    private final AuthDAO auth;
    private final GameDAO game;
    private final VoidService service;

    public ClearTests() throws SQLException, DataAccessException {
        user = new MySQLUserDataAccess();
        auth = new MySQLAuthDataAccess();
        game = new MySQLGameDataAccess();
        service = new VoidService(user, auth, game);
    }
    @Test
    void testNewUser() throws DataAccessException, SQLException {
        // Arrange
        user.createUser(new UserData("kngu21", "oh yeah!", "email.com"));
        auth.createAuth("kngu21");
        game.createGame("gamey");
        service.clear();
        if (user.getUser("kngu21") == null && auth.getAuth("kngu21") == null) {
            game.getGame(3);
        }
    }
}
