package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import result.ListGamesResult;
import service.GameService;
import service.UnauthorizedException;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGamesHandler implements Route {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final Gson gson = new Gson();

    public ListGamesHandler(AuthDAO authDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @Override
    public Object handle(Request request, Response response)  {
        String authToken = request.headers("Authorization");
        try{
            ListGamesResult result = GameService.listGames( authToken, authDAO, gameDAO);
            response.status(200);
            response.type("application/json");
            return gson.toJson(result);
        } catch (UnauthorizedException e){
            response.status(401);
            response.type("application/json");
            return gson.toJson(new ListGamesResult(null, e.getMessage()));
        } catch (Exception e){
            response.status(500);
            response.type("application/json");
            return gson.toJson(new ListGamesResult(null, e.getMessage()));
        }
    }
}
