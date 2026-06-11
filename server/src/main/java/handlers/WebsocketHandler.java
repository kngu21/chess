package handlers;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;


public class WebsocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public WebsocketHandler(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws DataAccessException, IOException {
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

    public void connect(WsMessageContext ctx, UserGameCommand cmd) throws DataAccessException, IOException {
        try {
            String authToken = cmd.getAuthToken();
            int gameID = cmd.getGameID();
            if(authDAO.getAuth(authToken) == null){
                connections.send(ctx, new ErrorMessage("Error: Invalid authToken"));
                return;
            }
            String username = authDAO.getAuth(authToken).username();
            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                connections.send(ctx, new ErrorMessage("Error: Game not found."));
                return;
            }
            connections.add(gameID, authToken, ctx);
            connections.send(ctx, new LoadGameMessage(gameData.game()));
            String role;
            if (gameData.whiteUsername().equals(username)) {
                role = "WHITE";
            } else if (gameData.blackUsername().equals(username)) {
                role = "BLACK";
            } else {
                role = "OBSERVER";
            }
            String msg = username + " joined as " + role;
            connections.broadcast(ctx, new NotificationMessage(msg));
        } catch (DataAccessException e){
            connections.send(ctx, new ErrorMessage("Server error: " + e.getMessage()));
        }
    }

    public void makeMove(){

    }

    public void leave(){

    }

    public void resign(){

    }
}
