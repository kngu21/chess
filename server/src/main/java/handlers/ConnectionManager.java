package handlers;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final Map<WsContext, WsContext> connections = new ConcurrentHashMap<>();
    Map<WsContext, Integer> sessionToGame = new ConcurrentHashMap<>();
    Map< String, WsContext> sessionToAuth = new ConcurrentHashMap<>();

    public void add(int gameID, String authToken, WsContext ctx) {
        connections.put(ctx,ctx);
        sessionToGame.put(ctx, gameID);
        sessionToAuth.put(authToken, ctx);
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

    public void sendTo(String authToken, ServerMessage message) throws IOException {
        WsContext target = null;
        for(String auth  : sessionToAuth.keySet()){
            if (auth.equals(authToken)) {
                target = sessionToAuth.get(auth);
            }
            if (target != null && target.session.isOpen()) {
                target.send(new Gson().toJson(message));
            }
        }
    }


    public void broadcast(WsContext excludeSession, ServerMessage notification) throws IOException {
        String msg = new Gson().toJson(notification);
        for (WsContext c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.send(msg);
                }
            }
        }
    }

    public void broadcastAll(ServerMessage notification) throws IOException {
        String msg = new Gson().toJson(notification);
        for (WsContext c : connections.values()) {
            if (c.session.isOpen()) {
                c.send(msg);
            }
        }
    }
}