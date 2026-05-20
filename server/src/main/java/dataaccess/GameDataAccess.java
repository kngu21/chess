package dataaccess;
import chess.ChessGame;
import model.GameData;
import java.util.ArrayList;
import java.util.HashMap;

public class GameDataAccess implements GameDAO{
    private final HashMap<Integer, GameData> games;
    private int gameID;
    public GameDataAccess(){
        this.games = new HashMap<>();
        this.gameID = 1;
    }
    public GameData createGame(String gameName){
        ChessGame game = new ChessGame();
        GameData newGame = new GameData(gameID++, null, null, gameName, game);
        games.put(newGame.gameID(), newGame);
        return newGame;
    }

    public GameData getGame(int gameID){
        return games.get(gameID);
    }

    public void replaceGame(GameData newGame){
        GameData original = games.get(newGame.gameID());
        games.put(original.gameID(), newGame);
    }

    public ArrayList<GameData> listGames(){
        return new ArrayList<>(games.values());
    }

    public void clear(){
        games.clear();
    }
}
