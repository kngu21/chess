package client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final String serverURL;
    private final HttpClient client = HttpClient.newHttpClient();
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
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(serverURL)).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() == 200){
            return true;
        }
        return false;
    }
}
