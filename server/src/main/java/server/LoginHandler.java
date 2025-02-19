package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import request.LoginRequest;
import result.LoginResult;
import service.BadRequestException;
import service.UnauthorizedException;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler implements Route {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    private final Gson gson = new Gson();

    public LoginHandler(AuthDAO authDAO, UserDAO userDAO){
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }
    @Override
    public Object handle(Request request, Response response) {
        LoginRequest loginRequest = gson.fromJson(request.body(), LoginRequest.class);
        try{
            LoginResult result = UserService.login(loginRequest, authDAO, userDAO);
            response.status(200);
            response.type("application/json");
            return gson.toJson(result);
        } catch (BadRequestException e){
            response.status(400);
            response.type("application/json");
            return gson.toJson(new LoginResult(null, null, e.getMessage()));
        } catch (UnauthorizedException e){
            response.status(401);
            response.type("application/json");
            return gson.toJson(new LoginResult(null, null, e.getMessage()));
        } catch (Exception e){
            response.status(500);
            response.type("application/json");
            return gson.toJson(new LoginResult(null, null, "Internal Server Error"));

        }
    }
}
