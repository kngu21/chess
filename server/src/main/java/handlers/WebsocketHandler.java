package handlers;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
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
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import java.io.IOException;


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
            case MAKE_MOVE -> makeMove(ctx, action);
            case LEAVE -> leave(ctx, action);
            case RESIGN -> resign(ctx, action);
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
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

    public void makeMove(WsMessageContext ctx, UserGameCommand cmd) throws IOException {
        try {
            String authToken = cmd.getAuthToken();
            if(authDAO.getAuth(authToken) == null){
                connections.send(ctx, new ErrorMessage("Error: Invalid authToken"));
                return;
            }
            String username = authDAO.getAuth(authToken).username();
            int gameID = cmd.getGameID();
            ChessMove move = cmd.getMove();
            if (move == null) {
                connections.send(ctx, new ErrorMessage("Error: missing move data."));
                return;
            }
            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                connections.send(ctx, new ErrorMessage("Error: Game not found."));
                return;
            }
            if (gameData.game().gameOver()) {
                connections.send(ctx, new ErrorMessage("Error: game is already over"));
                return;
            }
            ChessGame.TeamColor playerColor = username.equals(gameData.whiteUsername()) ? ChessGame.TeamColor.WHITE :
                            username.equals(gameData.blackUsername()) ? ChessGame.TeamColor.BLACK : null;
            if (playerColor == null) {
                connections.send(ctx, new ErrorMessage("Error: observers cannot make moves."));
                return;
            }
            System.out.println("DEBUG username = " + username);
            System.out.println("DEBUG white = " + gameData.whiteUsername());
            System.out.println("DEBUG black = " + gameData.blackUsername());
            System.out.println("DEBUG playerColor = " + playerColor);
            System.out.println("DEBUG teamTurn = " + gameData.game().getTeamTurn());
            if (gameData.game().getTeamTurn() != playerColor) {
                connections.send(ctx, new ErrorMessage("Error: not your turn"));
                return;
            }
            try {
                gameData.game().makeMove(move);
            } catch(InvalidMoveException e){
                connections.send(ctx, new ErrorMessage("Error: Invalid move."));
                return;
            }
            GameData updatedGame = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.game()
            );

            gameDAO.replaceGame(updatedGame);
            connections.broadcastAll(new LoadGameMessage(updatedGame.game()));
            String msg = username + " made move " + move.getStartPosition().toString() + " to " + move.getEndPosition().toString();
            connections.broadcast(ctx, new NotificationMessage(msg));
            ChessGame.TeamColor opponent =
                    (playerColor == ChessGame.TeamColor.WHITE)
                            ? ChessGame.TeamColor.BLACK
                            : ChessGame.TeamColor.WHITE;

            if (updatedGame.game().isInCheckmate(opponent)) {
                updatedGame.game().setGameOver();
                connections.broadcastAll(new NotificationMessage(username + " wins by checkmate"));
                GameData newGame = new GameData(
                        updatedGame.gameID(),
                        updatedGame.whiteUsername(),
                        updatedGame.blackUsername(),
                        updatedGame.gameName(),
                        updatedGame.game()
                );

                gameDAO.replaceGame(newGame);
                return;
            } else if (updatedGame.game().isInCheck(opponent)) {
                connections.broadcastAll(new NotificationMessage(opponent + " is in check"));
            } else if (updatedGame.game().isInStalemate(opponent)) {
                updatedGame.game().setGameOver();
                GameData newGame = new GameData(
                        updatedGame.gameID(),
                        updatedGame.whiteUsername(),
                        updatedGame.blackUsername(),
                        updatedGame.gameName(),
                        updatedGame.game()
                );

                gameDAO.replaceGame(newGame);
                connections.broadcastAll(new NotificationMessage(" Game ended in stalemate"));
                return;
            }
        } catch (DataAccessException e){
            connections.send(ctx, new ErrorMessage("Server error: " + e.getMessage()));
        }
    }

    public void leave(WsMessageContext ctx, UserGameCommand cmd){

    }

    public void resign(WsMessageContext ctx, UserGameCommand cmd){

    }
}
