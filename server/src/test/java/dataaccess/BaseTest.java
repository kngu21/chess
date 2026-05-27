package dataaccess;

import org.junit.jupiter.api.BeforeEach;
import service.GameService;
import service.UserService;
import service.VoidService;

import java.sql.SQLException;

public abstract class BaseTest {
    protected GameService service;
    protected VoidService service1;
    protected UserService service2;
    protected UserDAO user;
    protected AuthDAO auth;
    protected GameDAO game;

    @BeforeEach
    void setup() throws SQLException, DataAccessException {
        user = new MySQLUserDataAccess();
        auth = new MySQLAuthDataAccess();
        game = new MySQLGameDataAccess();
        service = new GameService(game, auth);
        service1 = new VoidService(user, auth, game);
        service2 = new UserService(user, auth);
        user.clear();
        auth.clear();
        game.clear();
    }
}