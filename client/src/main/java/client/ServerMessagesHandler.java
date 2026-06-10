package client;

import websocket.messages.ServerMessage;

public interface ServerMessagesHandler {
    void notify(ServerMessage message);
}