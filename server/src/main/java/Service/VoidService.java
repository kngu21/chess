package Service;
import Handlers.CreateGameHandler;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import java.util.ArrayList;

public class VoidService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    public VoidService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }
    public void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

    public ArrayList<GameData> listGames(String authToken) throws UnauthorizedException {
        AuthData data = authDAO.getAuth(authToken);
        if(data == null){
            throw new UnauthorizedException("Unauthorized");
        }
        return gameDAO.listGames();
    }

    public CreateGameHandler.CreateGameResult createGame(String authToken, String gameName) throws UnauthorizedException{
        AuthData exists = authDAO.getAuth(authToken);
        if(exists == null){
            throw new UnauthorizedException("Unauthorized");
        }
        GameData data = gameDAO.createGame(gameName);
        return new CreateGameHandler.CreateGameResult(data.GameID(), data.whiteUsername(), data.blackUsername(), gameName);
    }
}
