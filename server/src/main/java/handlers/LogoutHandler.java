package handlers;
import dataaccess.DataAccessException;
import service.BadRequestException;
import service.UserService;
import io.javalin.http.Context;

public class LogoutHandler {
    private final Context text;
    private final UserService service;

    public LogoutHandler(Context text, UserService service){
        this.text = text;
        this.service = service;
    }

    public void result() throws BadRequestException, DataAccessException {
        String auth = text.header("authorization");
        service.logout(auth);
    }
}
