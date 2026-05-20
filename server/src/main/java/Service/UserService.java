package Service;
import Handlers.LoginHandler;
import Handlers.RegisterHandler;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.Objects;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }
    public RegisterHandler.RegisterResult register(UserData request) throws AlreadyTakenException {
        UserData exists = userDAO.getUser(request.username());
        if(exists != null){
            throw new AlreadyTakenException("Username already taken");
        }
        UserData newUser = new UserData(request.username(), request.password(), request.email());
        userDAO.createUser(newUser);
        AuthData newData = authDAO.createAuth(request.username());
        return new RegisterHandler.RegisterResult(request.username(), newData.authToken());
    }
    public LoginHandler.LoginResult login(String username, String password, String authToken) throws BadRequestException {
        AuthData exists = authDAO.getAuth(username);
        if(exists == null){
            throw new BadRequestException("Bad request");
        }
        if(!Objects.equals(exists.authToken(), authToken)){
            throw new BadRequestException("Bad request");
        }
        UserData user = userDAO.getUser(username);
        if(!user.password().equals(password)){
            throw new BadRequestException("Bad request");
        }
        AuthData newData = authDAO.createAuth(username);
        return new LoginHandler.LoginResult(username, newData.authToken());
    }
}
