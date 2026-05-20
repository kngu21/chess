package server;
import Handlers.*;
import Service.*;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;

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
            RegisterHandler reggie = new RegisterHandler(context, userService);
            context.result(new Gson().toJson(reggie.result()));
            context.status(200);
        }
        catch(AlreadyTakenException exception){
            context.status(403).result(new Gson().toJson("Username already taken"));
        }

    }

    private void loginUser(Context context){
        try {
            LoginHandler loggie = new LoginHandler(context, userService);
            context.result(new Gson().toJson(loggie.result()));
            context.status(200);
        }
        catch(BadRequestException exception){
            context.status(400).result(new Gson().toJson("Bad request"));
        }
    }

    private void logoutUser(Context context){
        try{
            LogoutHandler logout = new LogoutHandler(context, userService);
            logout.result();
            context.status(200);
        }
        catch(UnauthorizedException exception){
            context.status(400).result(new Gson().toJson("Unauthorized"));
        }
    }

    private void listGames(Context context){
        try{
            ListGamesHandler listGames = new ListGamesHandler(context, voidService);
            context.result(new Gson().toJson(listGames.result()));
            context.status(200);
        }
        catch(UnauthorizedException exception){
            context.status(400).result(new Gson().toJson("Unauthorized"));
        }
    }

    private void createGame(Context context){
        try {
            CreateGameHandler createGame = new CreateGameHandler(context, gameService);
            context.result(new Gson().toJson(createGame.result()));
            context.status(200);
        }
        catch(UnauthorizedException exception){
            context.status(400).result(new Gson().toJson("Unauthorized"));
        }
    }

    private void joinGame(Context context){
        try{
            JoinGameHandler joinGame = new JoinGameHandler(context, gameService);
            joinGame.result();
            context.status(200);
            context.json(new Object());
        }
        catch(AlreadyTakenException exception){
            context.status(400).result(new Gson().toJson("Already taken"));
        }
    }

    private void clearAll(Context context){
        try {
            voidService.clear();
            context.status(200);
        }
        catch(DataAccessException exception){
            context.status(500).result(new Gson().toJson("Error accessing data"));
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
