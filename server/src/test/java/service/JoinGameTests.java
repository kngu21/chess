package service;

import handlers.LoginHandler;
import dataaccess.*;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JoinGameTests {
    private final GameDAO game;
    private final GameService service;
    private final UserService service2;
    public JoinGameTests() {
        UserDAO user = new UserDataAccess();
        AuthDAO auth = new AuthDataAccess();
        game = new GameDataAccess();
        service = new GameService(game, auth);
        service2 = new UserService(user, auth);
}
    @Test
    void testCorrect() throws AlreadyTakenException, DataAccessException {
        UserData user = new UserData("noOne", "oh yeah!", "email.com");
        service2.register(user);

        LoginHandler.LoginResult loginResult = service2.login("noOne", "oh yeah!");
        String authToken = loginResult.authToken();

        int gameId1 = service.createGame(authToken, "help me").gameID();
        int gameId2 = service.createGame(authToken, "here we go").gameID();

        // Act
        service.joinGame(authToken, "WHITE", gameId1);
        service.joinGame(authToken,"BLACK", gameId2);

        // Assert
        GameData game1 = game.getGame(gameId1);
        GameData game2 = game.getGame(gameId2);

        assertEquals("noOne", game1.whiteUsername());
        assertNull(game1.blackUsername());

        assertEquals("noOne", game2.blackUsername());
        assertNull(game2.whiteUsername());
    }
    @Test
    void testIncorrect() throws AlreadyTakenException, DataAccessException {
        // Arrange
        UserData user = new UserData("noOne", "oh yeah!", "email.com");
        service2.register(user);
        String token = service2.login("noOne", "oh yeah!").authToken();
        int gameId = service.createGame(token, "test").gameID();
        assertThrows(service.BadRequestException.class, () ->
                service.joinGame(token, "GREEN", gameId)
        );
    }

}
