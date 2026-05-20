package service;

import Handlers.ListGamesHandler;
import Handlers.LoginHandler;
import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreateGameTests {
    private UserDAO user;
    private AuthDAO auth;
    private GameDAO game;
    private GameService service;
    private VoidService service1;
    private UserService service2;
    public CreateGameTests() {
        user = new UserDataAccess();
        auth = new AuthDataAccess();
        game = new GameDataAccess();
        service = new GameService(game, user, auth);
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
        assertEquals(result.games().size(), 2);
    }
    @Test
    void incorrectTest() throws AlreadyTakenException {
        UserData user = new UserData("noOne", "oh yeah!", "email.com");
        service2.register(user);

        LoginHandler.LoginResult loginResult = service2.login("noOne", "oh yeah!");
        assertThrows(UnauthorizedException.class, () -> service.createGame("yay", "let's go"));
    }
}
