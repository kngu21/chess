package Handlers;
import Service.UserService;
import com.google.gson.Gson;
import model.UserData;

public class RegisterHandler {
    private final UserService request;
    public record RegisterRequest(String username, String password, String email){};
    public record RegisterResult(String username, String authToken){};

    public RegisterHandler(UserService request){
        this.request = request;
    }

}
