package dataaccess;
import model.GameData;
public interface GameDAO {
    GameData createGame(String username);
    GameData getGame(String gameID);
    void clear();
}
