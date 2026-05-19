package Service;
import Handlers.RegisterHandler;
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
        AuthData newAuth = new AuthData(request.password(), request.username());
        AuthData newData = authDAO.createAuth(request.username());
        return new RegisterHandler.RegisterResult(request.username(), newData.authToken());
    }
}
