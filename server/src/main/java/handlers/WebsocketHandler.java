package handlers;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
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


public class WebsocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebsocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx){
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
        connections.remove(ctx);
    }

    public void connect(WsMessageContext ctx, UserGameCommand cmd) {
        try {
            String authToken = cmd.getAuthToken();
            int gameID = cmd.getGameID();
            if(authDAO.getAuth(authToken) == null){
                connections.send(ctx, new ErrorMessage("Error: Invalid authToken"));
                return;
            }
            connections.add(gameID, authToken, ctx);
            String username = authDAO.getAuth(authToken).username();
            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                connections.send(ctx, new ErrorMessage("Error: Game not found."));
                return;
            }
            System.out.println("About to send LoadGameMessage");
            connections.send(ctx, new LoadGameMessage(gameData.game()));
            System.out.println("Sent LoadGameMessage");
            String role;
            if (username.equals(gameData.whiteUsername())) {
                role = "WHITE";
            } else if (username.equals(gameData.blackUsername())) {
                role = "BLACK";
            } else {
                role = "OBSERVER";
            }
            String msg = username + " joined as " + role;
            connections.broadcast(gameID, ctx, new NotificationMessage(msg));
        } catch (DataAccessException e){
            connections.send(ctx, new ErrorMessage("Server error: " + e.getMessage()));
        }
    }

    public void makeMove(WsMessageContext ctx, UserGameCommand cmd) {
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
            ChessGame game = gameData.game();
            if (game.gameOver()) {
                connections.send(ctx, new ErrorMessage("Error: game is already over"));
                return;
            }
            ChessGame.TeamColor playerColor = username.equals(gameData.whiteUsername()) ? ChessGame.TeamColor.WHITE :
                            username.equals(gameData.blackUsername()) ? ChessGame.TeamColor.BLACK : null;
            if (playerColor == null) {
                connections.send(ctx, new ErrorMessage("Error: observers cannot make moves."));
                return;
            }
            if (game.getTeamTurn() != playerColor) {
                connections.send(ctx, new ErrorMessage("Error: not your turn"));
                return;
            }
            try {
                game.makeMove(move);
            } catch(InvalidMoveException e){
                connections.send(ctx, new ErrorMessage("Error: Invalid move."));
                return;
            }
            gameDAO.replaceGame(gameData);
            connections.broadcastAll(gameID, new LoadGameMessage(game));
            String start = "" + (char)('a' + move.getStartPosition().getColumn() - 1)
                    + move.getStartPosition().getRow();
            String end = "" + (char)('a' + move.getEndPosition().getColumn() - 1)
                    + move.getEndPosition().getRow();
            String msg = username + " made move " + start + " to " + end;
            connections.broadcast(gameID, ctx, new NotificationMessage(msg));
            ChessGame.TeamColor opponent =
                    (playerColor == ChessGame.TeamColor.WHITE)
                            ? ChessGame.TeamColor.BLACK
                            : ChessGame.TeamColor.WHITE;
            if (game.isInCheckmate(opponent)) {
                game.setGameOver();
                gameDAO.replaceGame(gameData);
                connections.broadcastAll(gameID, new NotificationMessage(username + " wins by checkmate"));
                return;
            }
            if (gameData.game().isInStalemate(opponent)) {
                gameData.game().setGameOver();
                gameDAO.replaceGame(gameData);
                connections.broadcastAll(gameID, new NotificationMessage(" Game ended in stalemate"));
                return;
            }
            if (gameData.game().isInCheck(opponent)) {
                connections.broadcastAll(gameID, new NotificationMessage(opponent + " is in check"));
            }
        } catch (DataAccessException e){
            connections.send(ctx, new ErrorMessage("Server error: " + e.getMessage()));
        }
    }

    public void leave(WsMessageContext ctx, UserGameCommand cmd){
            try {
                String authToken = cmd.getAuthToken();
                var auth = authDAO.getAuth(authToken);
                if (auth == null) {
                    connections.send(ctx, new ErrorMessage("Error: Invalid authToken"));
                    return;
                }
                String username = auth.username();
                int gameID = cmd.getGameID();

                GameData gameData = gameDAO.getGame(gameID);
                if (gameData == null) {
                    connections.send(ctx, new ErrorMessage("Error: Game not found."));
                    return;
                }
                boolean isWhite = username.equals(gameData.whiteUsername());
                boolean isBlack = username.equals(gameData.blackUsername());
                GameData updated;
                if (isWhite) {
                    updated = new GameData(
                            gameID,
                            null,
                            gameData.blackUsername(),
                            gameData.gameName(),
                            gameData.game()
                    );
                    gameDAO.replaceGame(updated);
                } else if (isBlack) {
                    updated = new GameData(
                            gameID,
                            gameData.whiteUsername(),
                            null,
                            gameData.gameName(),
                            gameData.game()
                    );
                    gameDAO.replaceGame(updated);
                }
                connections.broadcast(gameID, ctx, new NotificationMessage(username + " left the game"));
                connections.remove(ctx);
            } catch (DataAccessException e) {
                connections.send(ctx, new ErrorMessage("Server error: " + e.getMessage()));
            }
    }

    public void resign(WsMessageContext ctx, UserGameCommand cmd){
        try {
            String authToken = cmd.getAuthToken();
            var auth = authDAO.getAuth(authToken);
            if (auth == null) {
                connections.send(ctx, new ErrorMessage("Error: Invalid authToken"));
                return;
            }
            String username = auth.username();
            int gameID = cmd.getGameID();
            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                connections.send(ctx, new ErrorMessage("Error: Game not found."));
                return;
            }
            ChessGame game = gameData.game();
            if (game.gameOver()) {
                connections.send(ctx, new ErrorMessage("Error: game is already over"));
                return;
            }
            boolean isWhite = username.equals(gameData.whiteUsername());
            boolean isBlack = username.equals(gameData.blackUsername());

            if (!isWhite && !isBlack) {
                connections.send(ctx, new ErrorMessage("Error: observers cannot resign."));
                return;
            }
            ChessGame updated = game.setGameOver();
            GameData newer = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), updated);
            gameDAO.replaceGame(newer);
            GameData check = gameDAO.getGame(gameID);
            System.out.println("After replace, gameOver = " + check.game().gameOver());
            connections.broadcastAll(gameID, new NotificationMessage(username + " resigned"));
            } catch (DataAccessException e) {
                connections.send(ctx, new ErrorMessage("Server error: " + e.getMessage()));
        }
    }
}
