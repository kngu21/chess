package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;
import service.AlreadyTakenException;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGameTests extends BaseTest{
    @Test
    void successfulCreateGame() throws DataAccessException {
        service2.register(new UserData("jim", "howdy", "cool@email.com"));
        var loginResult = service2.login("jim", "howdy");
        var authToken = loginResult.authToken();
        assertNotNull(authToken, "Auth token should not be null");
        assertDoesNotThrow(() -> {
            var gameData = service.createGame(authToken, "myGame");
            assertTrue(gameData.gameID() > 0, "Game ID should be positive");
            var storedGame = game.getGame(gameData.gameID());
            assertNotNull(storedGame, "Game should exist in the database");
            assertEquals("myGame", storedGame.gameName());
        });
    }
    @Test
    void failedCreateGame() throws AlreadyTakenException, DataAccessException {
        service2.register(new UserData("jim", "howdy", "cool@email.com"));
        var loginResult = service2.login("jim", "howdy");
        var authToken = loginResult.authToken();
        assertNotNull(authToken, "Auth token should not be null");
        var gameData = service.createGame(authToken, "myGame");
        assertThrows(() -> service.createGame(authToken, gameData.gameName())
        };
    }
}