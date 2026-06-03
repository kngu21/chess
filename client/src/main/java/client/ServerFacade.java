package client;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.AuthData;
import model.GameInfo;
import model.GameListData;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static ui.EscapeSequences.*;

public class ServerFacade {
    private final String serverURL;
    private final HttpClient client = HttpClient.newHttpClient();
    private String authToken = null;
    private final Gson gson = new Gson();
    public ServerFacade(String serverURL){
        this.serverURL = serverURL;
    }
    public boolean login(String username, String password) throws IOException, InterruptedException {
        String json = """
                {
                "username": "%s",
                "password": "%s"
                }
                """.formatted(username, password);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(serverURL + "/session")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() == 200){
            AuthData authData = gson.fromJson(response.body(), AuthData.class);
            this.authToken = authData.authToken();
            return true;
        }
        else {
            System.out.println(response.body());
            return false;
        }
    }

    public boolean register(String username, String password, String email) throws IOException, InterruptedException {
        String json = """
                {
                "username": "%s",
                "password": "%s",
                "email": "%s"
                }
                """.formatted(username, password, email);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(serverURL + "/user")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() == 200){
            AuthData authData = gson.fromJson(response.body(), AuthData.class);
            this.authToken = authData.authToken();
            return true;
        }
        else {
            System.out.println(response.body());
            return false;
        }
    }

    public boolean logout() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(serverURL + "/session")).header("authorization", authToken).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    public String listGames() throws IOException, InterruptedException {
        StringBuilder games = new StringBuilder();
        int i = 1;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(serverURL + "/game")).header("authorization", authToken).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        GameListData gameList = gson.fromJson(response.body(), GameListData.class);
        for(GameInfo info : gameList.games()){
            String white = info.whiteUsername() == null ? "-" : info.whiteUsername();
            String black = info.blackUsername() == null ? "-" : info.blackUsername();
            games.append(i).append(String.format(". GameID: %s, WhiteUsername: %s, BlackUsername: %s, GameName: %s\n", info.gameID(), white, black, info.gameName()));
            i++;
        }
        return games.toString();
    }

    public boolean createGame(String gameName) throws IOException, InterruptedException {
        String json = """
                {
                "gameName": "%s"
                }
                """.formatted(gameName);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(serverURL + "/game")).header("authorization", authToken).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    public void borderRow(List<Character> list){
        System.out.print(SET_BG_COLOR_LIGHT_GREY + "   " + RESET_BG_COLOR);
        for(int i = 0; i < 8; i++){
            System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_BOLD+ String.format(" %s ", list.get(i)) + RESET_BG_COLOR);
        }
        System.out.print(SET_BG_COLOR_LIGHT_GREY + "   " + RESET_BG_COLOR);
        System.out.println();
    }

    public String returnPiece(ChessPiece piece){
        if(piece == null){
            return EMPTY;
        }
        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            return switch (piece.getPieceType()){
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case ROOK -> WHITE_ROOK;
                case PAWN -> WHITE_PAWN;
            };
        }
        else {
            return switch (piece.getPieceType()) {
                case KING -> BLACK_KING;
                case QUEEN -> BLACK_QUEEN;
                case BISHOP -> BLACK_BISHOP;
                case KNIGHT -> BLACK_KNIGHT;
                case ROOK -> BLACK_ROOK;
                case PAWN -> BLACK_PAWN;
            };
        }
    }

    public void drawGame(ChessGame game, String color){
        List<Character> whiteList = new ArrayList<>(List.of('a','b','c','d','e','f','g','h'));
        borderRow(whiteList);
        for(int i = 0; i < 8; i++) {
            System.out.print(String.format(SET_BG_COLOR_LIGHT_GREY + " %s ", (i-8)*-1));
            for (int j = 0; j < 8; j++) {
                if((i+j) % 2 == 0){
                    System.out.print(SET_BG_COLOR_WHITE + String.format("%s",returnPiece(game.getBoard().getPiece(new ChessPosition(i+1,j+1)))) + RESET_BG_COLOR);
                }
                else{
                    System.out.print(SET_BG_COLOR_BLACK + String.format("%s",returnPiece(game.getBoard().getPiece(new ChessPosition(i+1,j+1)))) + RESET_BG_COLOR);
                }
            }
            System.out.print(String.format(SET_BG_COLOR_LIGHT_GREY + " %s ", (i-8)*-1) + RESET_BG_COLOR + "\n");
        }
        borderRow(whiteList);
    }
}
