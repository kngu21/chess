package handlers;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import websocket.messages.ServerMessage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final Map<WsContext, Boolean> connections = new ConcurrentHashMap<>();
    Map<WsContext, Integer> sessionToGame = new ConcurrentHashMap<>();
    Map<WsContext, String> sessionToAuth = new ConcurrentHashMap<>();

    public void add(int gameID, String authToken, WsContext ctx) {
        connections.put(ctx,true);
        sessionToGame.put(ctx, gameID);
        sessionToAuth.put(ctx, authToken);
    }

    public void remove(WsContext ctx) {
        connections.remove(ctx);
        sessionToGame.remove(ctx);
        sessionToAuth.remove(ctx);
    }

    public void send(WsContext ctx, ServerMessage message) throws IOException {
        if (ctx.session.isOpen()) {
            ctx.send(new Gson().toJson(message));
        }
    }

    public void broadcast(WsContext excludeSession, ServerMessage notification) throws IOException {
        String msg = new Gson().toJson(notification);
        for (WsContext c : connections.keySet()) {
            if (c.session.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.send(msg);
                }
            }
        }
    }

    public void broadcastAll(ServerMessage notification) throws IOException {
        String msg = new Gson().toJson(notification);
        for (WsContext c : connections.keySet()) {
            if (c.session.isOpen()) {
                c.send(msg);
            }
        }
    }
}