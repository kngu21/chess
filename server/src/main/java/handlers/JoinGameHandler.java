package handlers;

import service.AlreadyTakenException;
import service.GameService;
import service.UnauthorizedException;
import com.google.gson.Gson;
import io.javalin.http.Context;

public class JoinGameHandler {
    private final Context text;
    private final GameService service;

    public record JoinGameRequest(String playerColor, int gameID){}

    public JoinGameHandler(Context text, GameService service){
        this.text = text;
        this.service = service;
    }

    public void result() throws UnauthorizedException, AlreadyTakenException {
        JoinGameHandler.JoinGameRequest body = new Gson().fromJson(text.body(), JoinGameHandler.JoinGameRequest.class);
        String auth = text.header("authorization");
        service.joinGame(auth, body.playerColor(), body.gameID());
    }
}
