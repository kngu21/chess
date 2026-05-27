package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import service.AlreadyTakenException;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ReplaceGameTests extends BaseTest{
    @Test
    void successfulReplaceGame() throws DataAccessException {
        UserData newData = new UserData("kai", "password", "cool@email.com");
        service2.register(newData);
        var loginResult = service2.login("kai", "password");
        String authToken = loginResult.authToken();
        var gameData = service.createGame(authToken, "myGame");
        assertTrue(gameData.gameID() > 0, "Game ID should be positive");
        GameData newGame = new GameData(1, "no", "yes", "party", new ChessGame());
        game.replaceGame(newGame);
        GameData stored = game.getGame(1);
        assertEquals("no", stored.whiteUsername());
        assertEquals("yes", stored.blackUsername());
        assertEquals("party", stored.gameName());
        assertNotNull(stored.game());
    }
    @Test
    void failedReplaceGames() throws AlreadyTakenException, DataAccessException, SQLException {
        UserData newData = new UserData("kai", "password", "cool@email.com");
        service2.register(newData);
        var loginResult = service2.login("kai", "password");
        String authToken = loginResult.authToken();
        int gameID = service.createGame(authToken, "myGame").gameID();
        service2.logout(authToken);
        GameData newGame = new GameData(gameID, "no", "yes", "party", new ChessGame());
        assertThrows(NullPointerException.class, ()  -> {game.replaceGame(null); });
    }
}