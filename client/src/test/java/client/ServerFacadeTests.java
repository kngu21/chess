package client;

import org.junit.jupiter.api.*;
import server.Server;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    @BeforeAll
    public static void init() throws IOException, InterruptedException {
        server = new Server();
        var port = server.run(0);
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
    void registerSuccess() throws Exception {
        assertTrue(facade.register("player1", "password", "p1@email.com"));
    }
    @Test
    void registerFail() throws Exception{
        facade.register("player", "password", "playa@gmail.com");
        assertFalse(facade.register("player", "sneaky", "p@gmail.com"));
    }

    @Test
    void loginSuccess() throws Exception {
        facade.register("chessMaster", "checkmate", "cm@email.com");
        assertTrue(facade.login("chessMaster", "checkmate"));
    }
    @Test
    void loginFail() throws Exception{
        facade.register("player", "password", "playa@gmail.com");
        assertFalse(facade.login("player", "Password"));
    }

    @Test
    void logoutSuccess() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        facade.login("player1", "password");
        assertTrue(facade.logout());
    }
    @Test
    void logoutFail() throws Exception{
        facade.register("executioner", "password", "p1@email.com");
        facade.login("executioner", "password");
        facade.logout();
        assertFalse(facade.logout());
    }

    @Test
    void listGamesSuccess() throws Exception {
        facade.register("playerZ", "ZisBest", "ZZZ@email.com");
        facade.login("playerZ", "ZisBest");
        facade.createGame("The Battle");
        assertTrue(facade.listGames().length() > 50);
    }
    @Test
    void listGamesFail() throws Exception{
        facade.register("the_wasp", "stinger", "waspy@email.com");
        facade.login("the_wasp", "stinger");
        facade.createGame("Winner");
        facade.logout();
        assertThrows(Exception.class, () -> facade.listGames());
    }

    @Test
    void createGameSuccess() throws Exception {
        facade.register("warrior1", "zenstate", "armour@email.com");
        facade.login("warrior1", "zenstate");
        assertTrue(facade.createGame("Warrior's Playground"));
    }
    @Test
    void createGameFail() throws Exception {
        facade.register("Ring_Bearer", "torulethemall", "frodo@email.com");
        facade.login("Ring_Bearer", "torulethemall");
        facade.createGame("Elves");
        facade.logout();
        assertFalse(facade.createGame("Hobbits"));
    }

    @Test
    void joinGameSuccess() throws Exception {
        facade.register("captain_hawkeye", "cantmiss", "barton@email.com");
        facade.login("captain_hawkeye", "cantmiss");
        facade.createGame("Warrior's Playground");
        assertTrue(facade.joinGame(1, "WHITE"));
    }
    @Test
    void joinGameFail() throws Exception {
        facade.register("Kermit", "rainbowconnection", "hi-ho@email.com");
        facade.login("Kermit", "rainbowconnection");
        facade.createGame("Muppets");
        facade.joinGame(1, "WHITE");
        assertFalse(facade.joinGame(1, "WHITE"));
    }

    @Test
    void observeGameSuccess() throws Exception {
        facade.register("h_potter", "expelliarmus", "HP@email.com");
        facade.login("h_potter", "expelliarmus");
        facade.createGame("Hogwarts");
        assertTrue(facade.observeGame(1));
    }
    @Test
    void observeGameFail() throws Exception {
        facade.register("Aslan", "symbolism", "lionwitchwardrobe@email.com");
        facade.login("Aslan", "symbolism");
        facade.createGame("Narnia");
        assertFalse(facade.observeGame(6));
    }
}
