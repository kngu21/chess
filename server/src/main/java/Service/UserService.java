package Service;
import java.util.ArrayList;

public class UserService {
    private final ArrayList<String> request;
    public UserService(ArrayList<String> request){
        this.request = request;
    }
    public record RegisterResult(String username, String password, String email){

    }
    public String getResult(String request){
        String result = new String();
        return result;
    }
}
