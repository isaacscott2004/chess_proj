package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.JoinGameResult;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.GameService;
import service.UnauthorizedException;
import spark.Request;
import spark.Response;
import spark.Route;

public class JoinGameHandler implements Route {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final Gson gson = new Gson();

    public JoinGameHandler(AuthDAO authDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @Override
    public Object handle(Request request, Response response){
        JoinGameRequest joinGameRequest = gson.fromJson(request.body(), JoinGameRequest.class);
        String authToken = request.headers("Authorization");
        try{
            GameService.joinGame(joinGameRequest, authToken, authDAO, gameDAO);
            response.status(200);
            response.type("application/json");
            return gson.toJson(new JoinGameResult("{}"));
        } catch (BadRequestException e){
            response.status(400);
            response.type("application/json");
            return gson.toJson(new JoinGameResult(e.getMessage()));
        } catch(UnauthorizedException e){
            response.status(401);
            response.type("application/json");
            return gson.toJson(new JoinGameResult(e.getMessage()));
        } catch (AlreadyTakenException e){
            response.status(403);
            response.type("application/json");
            return gson.toJson(new JoinGameResult(e.getMessage()));
        } catch (Exception e){
            response.status(500);
            response.type("application/json");
            return gson.toJson(new JoinGameResult("Internal Server Error"));
        }
    }
}
