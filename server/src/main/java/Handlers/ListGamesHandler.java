package Handlers;
import Service.UnauthorizedException;
import Service.VoidService;
import io.javalin.http.Context;
import model.GameInfo;

import java.util.ArrayList;

public class ListGamesHandler {
    private final Context text;
    private VoidService service;

    public record ListGamesResult(ArrayList<GameInfo> game){};

    public ListGamesHandler(Context text, VoidService service){
        this.text = text;
        this.service = service;
    }

    public ListGamesHandler.ListGamesResult result() throws UnauthorizedException {
        String auth = text.header("authorization");
        return new ListGamesResult(service.listGames(auth));
    }
}
