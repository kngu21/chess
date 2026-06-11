package handlers;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import websocket.messages.ServerMessage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    Map<WsContext, Integer> sessionToGame = new ConcurrentHashMap<>();
    Map<WsContext, String> sessionToAuth = new ConcurrentHashMap<>();

    public void add(int gameID, String authToken, WsContext ctx) {
        sessionToAuth.entrySet().removeIf(e -> e.getValue().equals(authToken));
        sessionToGame.entrySet().removeIf(e -> e.getKey().equals(ctx));
        sessionToGame.put(ctx, gameID);
        sessionToAuth.put(ctx, authToken);
    }

    public void remove(WsContext ctx) {
        sessionToGame.remove(ctx);
        sessionToAuth.remove(ctx);
    }

    public void send(WsContext ctx, ServerMessage message) {
        if (ctx.session.isOpen()) {
            ctx.send(new Gson().toJson(message));
        }
    }

    public void broadcast(int gameID, WsContext excludeSession, ServerMessage notification){
        String msg = new Gson().toJson(notification);
        for (var c : sessionToGame.entrySet()) {
            WsContext ctx  = c.getKey();
            int gID = c.getValue();
            if (gID == gameID && ctx.session.isOpen() && ctx.session != excludeSession.session) {
                ctx.send(msg);
            }
        }
    }

    public void broadcastAll(int gameID, ServerMessage notification) {
        String msg = new Gson().toJson(notification);
        for (var c : sessionToGame.entrySet()) {
            WsContext ctx = c.getKey();
            int gID = c.getValue();
            if (gID == gameID && ctx.session.isOpen()) {
                ctx.send(msg);
            }
        }
    }
}