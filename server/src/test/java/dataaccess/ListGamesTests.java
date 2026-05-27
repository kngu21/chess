package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;
import service.AlreadyTakenException;
import service.UnauthorizedException;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ListGamesTests extends BaseTest{
    @Test
    void successfulListGames() throws DataAccessException {
        UserData newData = new UserData("kai", "password", "cool@email.com");
        service2.register(newData);
        var loginResult = service2.login("kai", "password");
        String authToken = loginResult.authToken();
        var gameData = service.createGame(authToken, "myGame");
        assertTrue(gameData.gameID() > 0, "Game ID should be positive");
        var storedGames = game.listGames();
        assertNotNull(storedGames);
    }
    @Test
    void failedListGames() throws AlreadyTakenException, DataAccessException, SQLException {
        UserData newData = new UserData("kai", "password", "cool@email.com");
        service2.register(newData);
        var loginResult = service2.login("kai", "password");
        String authToken = loginResult.authToken();
        int gameID = service.createGame(authToken, "myGame").gameID();
        service2.logout(authToken);
        assertThrows(UnauthorizedException.class, ()  -> service1.listGames(authToken));
    }
}