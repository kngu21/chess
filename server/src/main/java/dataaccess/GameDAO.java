package dataaccess;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;

public interface GameDAO {
    GameData createGame(String username);
    GameData getGame(int gameID);
    ArrayList<GameData> listGames();
    void replaceGame(GameData newGame);
    void clear() throws SQLException, DataAccessException;
}
