package client;

import com.google.gson.Gson;
import model.AuthData;
import model.GameInfo;
import model.GameListData;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(serverURL + "/session"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json)).build();
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
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(serverURL + "/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json)).build();
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
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(serverURL + "/session"))
                .header("authorization", authToken).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    public String listGames() throws IOException, InterruptedException {
        StringBuilder games = new StringBuilder();
        int i = 1;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(serverURL + "/game"))
                .header("authorization", authToken).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        GameListData gameList = gson.fromJson(response.body(), GameListData.class);
        for(GameInfo info : gameList.games()){
            String white = info.whiteUsername() == null ? "-" : info.whiteUsername();
            String black = info.blackUsername() == null ? "-" : info.blackUsername();
            games.append(i).append(String.format(". GameID: %s, WhiteUsername: %s, BlackUsername: %s, GameName: %s\n",
                    info.gameID(), white, black, info.gameName()));
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
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(serverURL + "/game"))
                .header("authorization", authToken)
                .POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    public boolean joinGame(int gameID, String color) throws IOException, InterruptedException {
        String json = """
            {
                "gameID": %d,
                "playerColor": "%s"
            }
            """.formatted(gameID, color);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverURL + "/game"))
                .header("authorization", authToken)
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return true;
        } else {
            System.out.println(response.body());
            return false;
        }
    }

    public boolean observeGame(int gameID) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(serverURL + "/game"))
                .header("authorization", authToken).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        GameListData gameList = gson.fromJson(response.body(), GameListData.class);
        for (GameInfo info : gameList.games()) {
            if (info.gameID() == gameID) {
                return true;
            }
        }
        return false;
    }
}
