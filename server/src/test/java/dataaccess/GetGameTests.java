package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;
import service.AlreadyTakenException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class GetGameTests extends BaseTest{
    @Test
    void successfulGetGame() throws DataAccessException {
        UserData newData = new UserData("kai", "password", "cool@email.com");
        service2.register(newData);
        var loginResult = service2.login("kai", "password");
        String authToken = loginResult.authToken();
        var gameData = service.createGame(authToken, "myGame");
        assertTrue(gameData.gameID() > 0, "Game ID should be positive");
        var storedGame = game.getGame(gameData.gameID());
        assertNotNull(storedGame, "Game should exist in the database");
        assertEquals("myGame", storedGame.gameName());
    }
    @Test
    void failedGetGame() throws AlreadyTakenException, DataAccessException, SQLException {
        UserData newData = new UserData("kai", "password", "cool@email.com");
        service2.register(newData);
        var loginResult = service2.login("kai", "password");
        String authToken = loginResult.authToken();
        int gameID = service.createGame(authToken, "myGame").gameID();
        int missingGameID = gameID + 3;
        assertNull (
            game.getGame(missingGameID));
    }
}