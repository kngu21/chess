package Handlers;
import service.BadRequestException;
import service.UserService;
import com.google.gson.Gson;
import io.javalin.http.Context;

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
        return service.login(body.username(), body.password());
    }
}