package client;

import com.google.gson.Gson;
import java.io.IOException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import jakarta.websocket.*;
import java.net.URI;
import java.net.URISyntaxException;

public class WSFacade extends Endpoint {

    Session session;
    ServerMessagesHandler serverMessagesHandler;

    public WSFacade(String url, ServerMessagesHandler serverMessagesHandler) throws IOException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.serverMessagesHandler = serverMessagesHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    serverMessagesHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new IOException();
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
                gameId
        ));
    }

    public void makeMove(String authToken, int gameId, Object move) throws IOException {
        // You will extend UserGameCommand to include move data
        send(new UserGameCommand(
                UserGameCommand.CommandType.MAKE_MOVE,
                authToken,
                gameId
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
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException ex) {
            throw new IOException();
        }
    }

}