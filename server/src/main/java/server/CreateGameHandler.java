package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import request.CreateGameRequest;
import result.CreateGameResult;
import service.BadRequestException;
import service.GameService;
import service.UnauthorizedException;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final Gson gson = new Gson();

    public CreateGameHandler(AuthDAO authDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }


    @Override
    public Object handle(Request request, Response response){
        CreateGameRequest createGameRequest = gson.fromJson(request.body(), CreateGameRequest.class);
        String authToken = request.headers("Authorization");
        try{
            CreateGameResult result = GameService.createGame(createGameRequest, authToken, authDAO, gameDAO);
            response.status(200);
            response.type("application/json");
            return gson.toJson(result);
        } catch (BadRequestException e){
            response.status(400);
            response.type("application/json");
            return gson.toJson(new CreateGameResult(null, e.getMessage()));
        } catch (UnauthorizedException e){
            response.status(401);
            response.type("application/json");
            return gson.toJson(new CreateGameResult(null, e.getMessage()));
        } catch (Exception e){
            response.status(500);
            response.type("application/json");
            return gson.toJson(new CreateGameResult(null, e.getMessage()));
        }

    }
}
