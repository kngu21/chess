package handlers;

import com.google.gson.Gson;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import websocket.commands.UserGameCommand;


public class WebsocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        UserGameCommand action = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        switch (action.getCommandType()) {
            case CONNECT -> connect(ctx, action);
            case MAKE_MOVE -> makeMove();
            case LEAVE -> leave();
            case RESIGN -> resign();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    public void connect(WsMessageContext ctx, UserGameCommand cmd){
        String authToken = cmd.getAuthToken();
        int gameID = cmd.getGameID();

    }

    public void makeMove(){

    }

    public void leave(){

    }

    public void resign(){

    }
}
