package server;
import Handlers.RegisterHandler;
import Service.AlreadyTakenException;
import Service.GameService;
import Service.UserService;
import Service.VoidService;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.AuthDataAccess;
import dataaccess.UserDAO;
import dataaccess.UserDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.json.JavalinGson;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final GameService gameService;
    private final VoidService voidService;
    private AuthDAO auth;
    private UserDAO user;

    public Server() {
        this.auth = new AuthDataAccess();
        this.user = new UserDataAccess();
        this.userService = new UserService(user, auth);
        this.gameService = new GameService();
        this.voidService = new VoidService();
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        // Register your endpoints and exception handlers here.

        javalin.post("/user", this::registerUser);
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

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
