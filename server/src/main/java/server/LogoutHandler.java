package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import result.LogoutResult;
import service.BadRequestException;
import service.UnauthorizedException;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route
{
    private final AuthDAO authDAO;
    private final Gson gson = new Gson();

    public LogoutHandler(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    @Override
    public Object handle(Request request, Response response){
        String authToken = request.headers("Authorization");
        try{
            UserService.logout(authToken, authDAO);
            response.status(200);
            response.type("application/json");
            return gson.toJson(new LogoutResult("{}"));
        } catch (UnauthorizedException e){
            response.status(401);
            response.type("application/json");
            return gson.toJson(new LogoutResult(e.getMessage()));
        } catch (Exception e){
            response.status(500);
            response.type("application/json");
            return gson.toJson(new LogoutResult("Internal Server Error"));
        }
    }
}
