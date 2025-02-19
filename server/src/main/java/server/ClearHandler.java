package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import result.ClearResult;
import service.ClearService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler implements Route {
    private final Gson gson = new Gson();
    private final AuthDAO authAccessObject;
    private final UserDAO userAccessObject;
    private final GameDAO gameAccessObject;

    public ClearHandler(AuthDAO authAccessObject, UserDAO userAccessObject, GameDAO gameAccessObject){
        this.authAccessObject = authAccessObject;
        this.userAccessObject = userAccessObject;
        this.gameAccessObject = gameAccessObject;
    }


    @Override
    public Object handle(Request request, Response response){
        try{
            ClearResult clearResult = ClearService.clear(authAccessObject, userAccessObject, gameAccessObject);
            response.status(200);
            response.type("application/json");
            return gson.toJson(clearResult);
        } catch (Exception e){
            response.status(500);
            response.type("application/json");
            return gson.toJson(new ClearResult("Internal Server Error"));
        }
    }
}
