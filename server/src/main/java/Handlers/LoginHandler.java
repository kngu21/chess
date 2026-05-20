package Handlers;
import Service.AlreadyTakenException;
import Service.BadRequestException;
import Service.UserService;
import com.google.gson.Gson;
import io.javalin.http.Context;
import model.UserData;

public class LoginHandler {
    private final Context text;
    private UserService service;

    public record LoginRequest(String username, String password){};
    public record LoginResult(String username, String authToken){};

    public LoginHandler(Context text, UserService service){
        this.text = text;
        this.service = service;
    }

    public LoginResult result() throws BadRequestException {
        LoginHandler.LoginRequest body = new Gson().fromJson(text.body(), LoginHandler.LoginRequest.class);
        String auth = text.header("authorization");
        System.out.println(auth);
        return service.login(body.username(), body.password(), auth);
    }
}