package dataaccess;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;

public interface GameDAO {
    GameData createGame(String username) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    ArrayList<GameData> listGames() throws DataAccessException;
    void replaceGame(GameData newGame) throws DataAccessException;
    void clear() throws SQLException, DataAccessException;
}
