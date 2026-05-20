package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTest {
    protected GameService service;
    protected VoidService service1;
    protected UserService service2;
    @BeforeEach
    void setup() {
        UserDAO user = new UserDataAccess();
        AuthDAO auth = new AuthDataAccess();
        GameDAO game = new GameDataAccess();
        service = new GameService(game, auth);
        service1 = new VoidService(user, auth, game);
        service2 = new UserService(user, auth);
    }
}
