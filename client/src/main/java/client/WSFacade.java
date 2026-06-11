package client;

import chess.ChessMove;
import com.google.gson.Gson;
import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import jakarta.websocket.*;
import java.net.URI;
import java.net.URISyntaxException;

public class WSFacade extends Endpoint{

    Session session;
    ServerMessagesHandler serverMessagesHandler;

    public WSFacade(String url, ServerMessagesHandler serverMessagesHandler) throws IOException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.serverMessagesHandler = serverMessagesHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(String.class, message -> {
                JsonObject obj = JsonParser.parseString(message).getAsJsonObject();
                String type = obj.get("serverMessageType").getAsString();
                ServerMessage msg;
                switch (type) {
                    case "LOAD_GAME" ->
                            msg = new Gson().fromJson(message, LoadGameMessage.class);

                    case "NOTIFICATION" ->
                            msg = new Gson().fromJson(message, NotificationMessage.class);

                    case "ERROR" ->
                            msg = new Gson().fromJson(message, ErrorMessage.class);

                    default -> {
                        System.out.println("Unknown message type: " + type);
                        return;
                    }
                }
                serverMessagesHandler.notify(msg);
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new IOException(ex);
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, int gameId) throws IOException {
        send(new UserGameCommand(
                UserGameCommand.CommandType.CONNECT,
                authToken,
                gameId,
                null
        ));
    }

    public void makeMove(String authToken, int gameId, ChessMove move) throws IOException {
        // You will extend UserGameCommand to include move data
        send(new UserGameCommand(
                UserGameCommand.CommandType.MAKE_MOVE,
                authToken,
                gameId,
                move
        ));
    }

    public void leave(String authToken, int gameId) throws IOException {
        send(new UserGameCommand(
                UserGameCommand.CommandType.LEAVE,
                authToken,
                gameId
        ));
    }

    public void resign(String authToken, int gameId) throws IOException {
        send(new UserGameCommand(
                UserGameCommand.CommandType.RESIGN,
                authToken,
                gameId
        ));
    }

    private void send(UserGameCommand cmd) throws IOException {
        try {
            String json = new Gson().toJson(cmd);
            session.getBasicRemote().sendText(json);
        } catch (IOException ex) {
            throw new IOException();
        }
    }
}