package dataaccess;
import model.GameData;
public interface GameDAO {
    GameData createGame(String username);
    GameData getGame(int gameID);
    void clear();
}
