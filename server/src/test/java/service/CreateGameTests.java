package service;

import handlers.ListGamesHandler;
import handlers.LoginHandler;
import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreateGameTests {
    private final GameService service;
    private final VoidService service1;
    private final UserService service2;
    public CreateGameTests() {
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
        service.createGame(loginResult.authToken(), "here we go");
        ListGamesHandler.ListGamesResult result = new ListGamesHandler.ListGamesResult(service1.listGames(loginResult.authToken()));
        assertEquals(2, result.games().size());
    }
    @Test
    void incorrectTest() throws AlreadyTakenException {
        UserData user = new UserData("noOne", "oh yeah!", "email.com");
        service2.register(user);
        assertThrows(UnauthorizedException.class, () -> service.createGame("yay", "let's go"));
    }
}
