package Handlers;
import Service.BadRequestException;
import Service.UserService;
import io.javalin.http.Context;

public class LogoutHandler {
    private final Context text;
    private UserService service;

    public LogoutHandler(Context text, UserService service){
        this.text = text;
        this.service = service;
    }

    public void result() throws BadRequestException {
        String auth = text.header("authorization");
        service.logout(auth);
    }
}
