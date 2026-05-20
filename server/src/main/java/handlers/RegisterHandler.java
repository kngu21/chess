package handlers;
import service.AlreadyTakenException;
import service.UserService;
import com.google.gson.Gson;
import io.javalin.http.Context;
import model.UserData;

public class RegisterHandler {
    private final Context text;
    private final UserService service;

    public record RegisterRequest(String username, String password, String email){}
    public record RegisterResult(String username, String authToken){}

    public RegisterHandler(Context text, UserService service){
        this.text = text;
        this.service = service;
    }

    public RegisterResult result() throws AlreadyTakenException {
        RegisterHandler.RegisterRequest body = new Gson().fromJson(text.body(), RegisterHandler.RegisterRequest.class);
        return service.register(new UserData(body.username(), body.password(), body.email));
    }
}
