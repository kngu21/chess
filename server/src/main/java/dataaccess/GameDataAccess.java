package dataaccess;
import chess.ChessGame;
import model.GameData;
import java.util.ArrayList;
import java.util.HashMap;

public class GameDataAccess implements GameDAO{
    private HashMap<Integer, GameData> games;
    private int GameID;
    public GameDataAccess(){
        this.games = new HashMap<>();
        this.GameID = 1;
    }
    public GameData createGame(String gameName){
        GameData newGame = new GameData(GameID++, null, null, gameName);
        games.put(GameID, newGame);
        return newGame;
    }

    public GameData getGame(int gameID){
        return games.get(gameID);
    }

    public ArrayList<GameData> listGames(){
        return new ArrayList<GameData>(games.values());
    }

    public void clear(){
        games.clear();
    }
}
