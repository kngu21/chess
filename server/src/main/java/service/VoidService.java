package service;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.GameInfo;

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

    public ArrayList<GameInfo> listGames(String authToken) throws UnauthorizedException {
        AuthData data = authDAO.getAuth(authToken);
        if(data == null){
            throw new UnauthorizedException("Unauthorized");
        }
        ArrayList<GameData> list = gameDAO.listGames();
        ArrayList<GameInfo> newList = new ArrayList<>();
        for(GameData g : list){
            newList.add(new GameInfo(g.gameID(), g.whiteUsername(), g.blackUsername(), g.gameName()));
        }
        return newList;
    }
}
