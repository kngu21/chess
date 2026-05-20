package service;
import handlers.LoginHandler;
import handlers.RegisterHandler;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

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

    public LoginHandler.LoginResult login(String username, String password) throws BadRequestException {
        UserData exists = userDAO.getUser(username);
        if(exists == null){
            throw new UnauthorizedException("Unauthorized");
        }
        if(!exists.password().equals(password)){
            throw new UnauthorizedException("Unauthorized");
        }
        AuthData newData = authDAO.createAuth(username);
        return new LoginHandler.LoginResult(username, newData.authToken());
    }

    public void logout(String authToken){
        AuthData exists = authDAO.getAuth(authToken);
        if(exists == null){
            throw new UnauthorizedException("Unauthorized");
        }
        authDAO.removeAuth(authToken);
    }
}
