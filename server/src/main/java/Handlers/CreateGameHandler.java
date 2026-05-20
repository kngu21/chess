package Handlers;

import Service.UnauthorizedException;
import Service.VoidService;
import com.google.gson.Gson;
import io.javalin.http.Context;

public class CreateGameHandler {
    private final Context text;
    private VoidService service;

    public record CreateGameRequest(String gameName){};
    public record CreateGameResult(int gameID, String whiteUsername, String blackUsername, String gameName){};

    public CreateGameHandler(Context text, VoidService service){
        this.text = text;
        this.service = service;
    }

    public CreateGameResult result() throws UnauthorizedException {
        CreateGameHandler.CreateGameRequest body = new Gson().fromJson(text.body(), CreateGameHandler.CreateGameRequest.class);
        String auth = text.header("authorization");
        return service.createGame(auth, body.gameName());
    }
}
