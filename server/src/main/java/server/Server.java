package server;
import Handlers.*;
import service.*;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final GameService gameService;
    private final VoidService voidService;

    public Server() {
        AuthDAO auth = new AuthDataAccess();
        UserDAO user = new UserDataAccess();
        GameDAO game = new GameDataAccess();
        this.userService = new UserService(user, auth);
        this.gameService = new GameService(game, user, auth);
        this.voidService = new VoidService(user, auth, game);
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        // Register your endpoints and exception handlers here.

        javalin.post("/user", this::registerUser);
        javalin.delete("/db", this::clearAll);
        javalin.post("/session", this::loginUser);
        javalin.delete("/session", this::logoutUser);
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);
    }

    private void registerUser(Context context) {
        try {
            RegisterHandler.RegisterRequest body = new Gson().fromJson(context.body(), RegisterHandler.RegisterRequest.class);
            RegisterHandler reggie = new RegisterHandler(context, userService);
            if (body == null || body.username() == null || body.password() == null || body.email() == null) {
                context.status(400).result(new Gson().toJson(Map.of("message", "Error: bad request")));
                return;
            }
            context.result(new Gson().toJson(reggie.result()));
            context.status(200);
        }
        catch(BadRequestException exception){
            context.status(400).result(new Gson().toJson(Map.of("message","Error: bad request")));
        }
        catch(AlreadyTakenException exception){
            context.status(403).result(new Gson().toJson(Map.of("message","Error: already taken")));
        }
    }

    private void loginUser(Context context){
        try {
            LoginHandler.LoginRequest body = new Gson().fromJson(context.body(), LoginHandler.LoginRequest.class);
            if (body == null || body.username() == null || body.password() == null) {
                context.status(400).result(new Gson().toJson(Map.of("message", "Error: bad request")));
                return;
            }
            LoginHandler loggie = new LoginHandler(context, userService);
            var result = loggie.result();
            context.status(200).result(new Gson().toJson(result));;

        }
        catch(BadRequestException exception){
            context.status(400).result(new Gson().toJson(Map.of("message","Error: bad request")));
        }
        catch(UnauthorizedException exception){
            context.status(401).result(new Gson().toJson(Map.of("message","Error: unauthorized")));
        }
    }

    private void logoutUser(Context context){
        try{
            LogoutHandler logout = new LogoutHandler(context, userService);
            logout.result();
            context.status(200).result("{}");
        }
        catch(UnauthorizedException exception){
            context.status(401).result(new Gson().toJson(Map.of("message","Error: unauthorized")));
        }
    }

    private void listGames(Context context){
        try{
            ListGamesHandler listGames = new ListGamesHandler(context, voidService);
            var result = listGames.result();
            context.status(200);
            context.result(new Gson().toJson(result));
        }
        catch(UnauthorizedException exception){
            context.status(401).result(new Gson().toJson(Map.of("message","Error: unauthorized")));
        }
    }

    private void createGame(Context context){
        try {
            CreateGameHandler.CreateGameRequest body = new Gson().fromJson(context.body(), CreateGameHandler.CreateGameRequest.class);
            if (body == null || body.gameName() == null) {
                context.status(400).result(new Gson().toJson(Map.of("message", "Error: bad request")));
                return;
            }
            CreateGameHandler createGame = new CreateGameHandler(context, gameService);
            context.result(new Gson().toJson(createGame.result()));
            context.status(200);
        }
        catch(BadRequestException exception){
            context.status(400).result(new Gson().toJson(Map.of("message","Error: bad request")));
        }
        catch(UnauthorizedException exception){
            context.status(401).result(new Gson().toJson(Map.of("message","Error: unauthorized")));
        }
    }

    private void joinGame(Context context){
        try{
            JoinGameHandler joinGame = new JoinGameHandler(context, gameService);
            joinGame.result();
            context.status(200).result("{}");
        }
        catch(BadRequestException exception){
            context.status(400).result(new Gson().toJson(Map.of("message","Error: bad request")));
        }
        catch(UnauthorizedException exception){
            context.status(401).result(new Gson().toJson(Map.of("message","Error: unauthorized")));
        }
        catch(AlreadyTakenException exception){
            context.status(403).result(new Gson().toJson(Map.of("message", "Error: already taken")));
        }
    }

    private void clearAll(Context context){
        try {
            voidService.clear();
            context.status(200).result("{}");
        }
        catch(DataAccessException exception){
            context.status(500).result(new Gson().toJson(Map.of("message", "Error accessing data")));
        }
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
