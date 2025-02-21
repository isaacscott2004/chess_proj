package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import request.RegisterRequest;
import result.RegisterResult;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    private final Gson gson = new Gson();

    public RegisterHandler(AuthDAO authDAO, UserDAO userDAO){
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }
    @Override
    public Object handle(Request request, Response response) {
        RegisterRequest registerRequest = gson.fromJson(request.body(), RegisterRequest.class);
        try{
            RegisterResult result = UserService.register(registerRequest, authDAO, userDAO);
            response.status(200);
            return gson.toJson(result);
        } catch (BadRequestException e){
            response.status(400);
            return gson.toJson(new RegisterResult(null, null, e.getMessage()));
        } catch (AlreadyTakenException e){
            response.status(403);
            return gson.toJson(new RegisterResult(null, null, e.getMessage()));
        } catch (Exception e){
            response.status(500);
            return gson.toJson(new RegisterResult(null, null, "Internal Server Error"));
        }
    }
}
