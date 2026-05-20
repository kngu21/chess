package Handlers;
import Service.BadRequestException;
import Service.UserService;
import com.google.gson.Gson;
import io.javalin.http.Context;

public class LogoutHandler {
    private final Context text;
    private UserService service;

    public record LogoutRequest(String username, String password){};
    public record LogoutResult(boolean completed){};

    public LogoutHandler(Context text, UserService service){
        this.text = text;
        this.service = service;
    }

    public void result() throws BadRequestException {
        LogoutHandler.LogoutRequest body = new Gson().fromJson(text.body(), LogoutHandler.LogoutRequest.class);
        String auth = text.header("authorization");
        service.logout(auth);
    }
}
