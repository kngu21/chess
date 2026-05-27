package service;

import handlers.ListGamesHandler;
import handlers.LoginHandler;
import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ListGamesTests extends BaseTest{
    @Test
    void testCorrect() throws AlreadyTakenException, DataAccessException {
        UserData user = new UserData("noOne", "oh yeah!", "email.com");
        service2.register(user);

        LoginHandler.LoginResult loginResult = service2.login("noOne", "oh yeah!");
        service.createGame(loginResult.authToken(), "help me");
        ListGamesHandler.ListGamesResult result = new ListGamesHandler.ListGamesResult(service1.listGames(loginResult.authToken()));
        assertEquals(1, result.games().size());
    }
    @Test
    void incorrectTest() throws AlreadyTakenException, DataAccessException {
        UserData user = new UserData("noOne", "oh yeah!", "email.com");
        service2.register(user);

        LoginHandler.LoginResult loginResult = service2.login("noOne", "oh yeah!");
        service.createGame(loginResult.authToken(), "help me");
        assertThrows(UnauthorizedException.class, () -> service1.listGames("yay"));
    }
}
