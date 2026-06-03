package client;

import org.junit.jupiter.api.*;
import server.Server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    @BeforeAll
    public static void init() throws IOException, InterruptedException {
        server = new Server();
        var desiredPort = 0;
        var port = server.run(desiredPort);
        var serverURL = "http://localhost:" + port;
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(serverURL);

        HttpClient.newHttpClient().send(
                HttpRequest.newBuilder()
                        .uri(URI.create(serverURL + "/db"))
                        .DELETE()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void register() throws Exception {
        assertTrue(facade.register("player1", "password", "p1@email.com"));
    }
}
