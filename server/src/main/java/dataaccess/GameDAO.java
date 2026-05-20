package dataaccess;
import model.GameData;
import java.util.ArrayList;

public interface GameDAO {
    GameData createGame(String username);
    GameData getGame(int gameID);
    ArrayList<GameData> listGames();
    void clear();
}
