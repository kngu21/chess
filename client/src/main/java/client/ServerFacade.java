package client;

import com.google.gson.Gson;
import model.AuthData;
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
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(serverURL + "/session")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("REGISTER STATUS = " + response.statusCode());
        System.out.println("REGISTER BODY   = " + response.body());
        if(response.statusCode() == 200){

            AuthData authData = gson.fromJson(response.body(), AuthData.class);
            this.authToken = authData.authToken();
            return true;
        }
        return false;
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
        System.out.println("REGISTER STATUS = " + response.statusCode());
        System.out.println("REGISTER BODY   = " + response.body());
        if(response.statusCode() == 200){
            AuthData authData = gson.fromJson(response.body(), AuthData.class);
            this.authToken = authData.authToken();
            return true;
        }
        return false;
    }
}
