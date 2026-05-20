package dataaccess;
import model.GameData;
public interface GameDAO {
    GameData createGame(String gameName);
    GameData getGame(String gameID);
    void clear();
}
