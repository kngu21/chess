package service;
import dataaccess.DataAccessException;
import handlers.CreateGameHandler;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.Objects;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    public GameService(GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }
    public CreateGameHandler.CreateGameResult createGame(String authToken, String gameName) throws UnauthorizedException, DataAccessException {
        AuthData exists = authDAO.getAuth(authToken);
        if(exists == null){
            throw new UnauthorizedException("Unauthorized");
        }
        GameData data = gameDAO.createGame(gameName);
        return new CreateGameHandler.CreateGameResult(data.gameID(), data.whiteUsername(), data.blackUsername(), gameName);
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws AlreadyTakenException, DataAccessException {
        AuthData exists = authDAO.getAuth(authToken);
        if(exists == null){
            throw new UnauthorizedException("Unauthorized");
        }
        GameData game = gameDAO.getGame(gameID);
        if(game == null){
            throw new BadRequestException("Bad request");
        }
        if(Objects.equals(playerColor, "WHITE")){
            if(game.whiteUsername() != null){
                throw new AlreadyTakenException();
            }
            gameDAO.replaceGame(new GameData(gameID, exists.username(), game.blackUsername(), game.gameName(), game.game()));
            return;
        }
        if(Objects.equals(playerColor, "BLACK")){
            if(game.blackUsername() != null){
                throw new AlreadyTakenException();
            }
            gameDAO.replaceGame(new GameData(gameID, game.whiteUsername(), exists.username(), game.gameName(), game.game()));
            return;
        }
        throw new BadRequestException("Bad request");
    }
}
