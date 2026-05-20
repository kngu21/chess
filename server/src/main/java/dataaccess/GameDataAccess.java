package dataaccess;
import chess.ChessGame;
import model.GameData;
import java.util.HashMap;
import java.util.UUID;

public class GameDataAccess implements GameDAO{
    private HashMap<String, GameData> games;
    public GameDataAccess(){
        this.games = new HashMap<>();
    }
    public GameData createGame(String gameName){
        int hi = 1;
        ChessGame game = new ChessGame();
        GameData newGame = new GameData(1234, "whiteUsername", "blackUsername", "gamey", game);
        games.put(gameName, newGame);
        return newGame;
    }

    public GameData getGame(String gameID){
        return games.get(gameID);
    }

    public void clear(){
        games.clear();
    }
}
