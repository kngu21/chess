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
        ChessGame game = new ChessGame();
        GameData newGame = new GameData(GameID++, null, null, gameName, game);
        games.put(GameID, newGame);
        return newGame;
    }

    public GameData getGame(int gameID){
        return games.get(gameID);
    }

    public void replaceGame(GameData newGame){
        GameData original = games.get(newGame.GameID());
        games.put(original.GameID(), newGame);
    }

    public ArrayList<GameData> listGames(){
        return new ArrayList<GameData>(games.values());
    }

    public void clear(){
        games.clear();
    }
}
