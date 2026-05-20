package service;

import handlers.ListGamesHandler;
import handlers.LoginHandler;
import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ListGamesTests {
    private final GameService service;
    private final VoidService service1;
    private final UserService service2;
    public ListGamesTests() {
        UserDAO user = new UserDataAccess();
        AuthDAO auth = new AuthDataAccess();
        GameDAO game = new GameDataAccess();
        service = new GameService(game, auth);
        service1 = new VoidService(user, auth, game);
        service2 = new UserService(user, auth);
    }
    @Test
    void testCorrect() throws AlreadyTakenException {
        UserData user = new UserData("noOne", "oh yeah!", "email.com");
        service2.register(user);

        LoginHandler.LoginResult loginResult = service2.login("noOne", "oh yeah!");
        service.createGame(loginResult.authToken(), "help me");
        ListGamesHandler.ListGamesResult result = new ListGamesHandler.ListGamesResult(service1.listGames(loginResult.authToken()));
        assertEquals(1, result.games().size());
    }
    @Test
    void incorrectTest() throws AlreadyTakenException {
        UserData user = new UserData("noOne", "oh yeah!", "email.com");
        service2.register(user);

        LoginHandler.LoginResult loginResult = service2.login("noOne", "oh yeah!");
        service.createGame(loginResult.authToken(), "help me");
        assertThrows(UnauthorizedException.class, () -> service1.listGames("yay"));
    }
}
