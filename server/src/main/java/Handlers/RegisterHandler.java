package Handlers;
import Service.UserService;
import com.google.gson.Gson;
import model.UserData;

public class RegisterHandler {
    private final UserService request;
    public RegisterHandler(UserService request){
        this.request = request;
    }

}
