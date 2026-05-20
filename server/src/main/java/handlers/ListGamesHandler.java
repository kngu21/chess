package handlers;
import service.UnauthorizedException;
import service.VoidService;
import io.javalin.http.Context;
import model.GameInfo;

import java.util.ArrayList;

public class ListGamesHandler {
    private final Context text;
    private final VoidService service;

    public record ListGamesResult(ArrayList<GameInfo> games){}

    public ListGamesHandler(Context text, VoidService service){
        this.text = text;
        this.service = service;
    }

    public ListGamesHandler.ListGamesResult result() throws UnauthorizedException {
        String auth = text.header("authorization");
        ArrayList<GameInfo> games = service.listGames(auth);
        return new ListGamesResult(games);
    }
}
