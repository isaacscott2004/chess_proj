package service;

import dataaccess.*;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGamesResult;
import java.util.Collection;

public class GameService {
    public static ListGamesResult listGames(ListGamesRequest request){
        AuthDAO authAccessObject = DAOImplmentation.getAuthDAO();
        GameDAO gameAccessObject = DAOImplmentation.getGameDAO();
        if(request.authToken() == null){
            return new ListGamesResult(null, "Error: (authToken cannot be empty)");
        }
        try{
            authAccessObject.getAuth(request.authToken());
        } catch (DataAccessException e){
            return new ListGamesResult(null, "Error: unauthorized");
        }
        return new ListGamesResult( gameAccessObject.getListGames(), null);
    }

    public static CreateGameResult createGame(CreateGameRequest request){
        AuthDAO authAccessObject = DAOImplmentation.getAuthDAO();
        GameDAO gameAccessObject = DAOImplmentation.getGameDAO();
        if(request.gameName() == null || request.authToken() == null){
            return new CreateGameResult(null, "Error: (gameName and/or authToken must not be null)");
        }
        if(gameAccessObject.isNameTaken(request.gameName())){
            return new CreateGameResult(null, "Error: (gameName already taken)");
        }
        try{
            authAccessObject.getAuth(request.authToken());
        } catch (DataAccessException e) {
           return new CreateGameResult(null, "Error: unauthorized");
        }
        Integer gameID = gameAccessObject.createGame(request.gameName());
        return new CreateGameResult(gameID, null);
    }

    public static JoinGameResult joinGame(JoinGameRequest request){
        AuthDAO authAccessObject = DAOImplmentation.getAuthDAO();
        GameDAO gameAccessObject = DAOImplmentation.getGameDAO();
        String username;
        if(request.authToken() == null || request.color() == null || request.gameID() == null){
            return new JoinGameResult("Error: (authToken and/or color and/or gameID cannot be empty)");
        }
        try{
            username = authAccessObject.getUsername(request.authToken());
        } catch (DataAccessException e) {
            return new JoinGameResult("Error: unauthorized");
        }
        try{
            gameAccessObject.checkGameID(request.gameID());
        } catch (DataAccessException e) {
            return new JoinGameResult("Error: (Invalid gameID)");
        }
        try{
            gameAccessObject.updateGame(username, request.color(), request.gameID());
        } catch (DataAccessException e){
            return new JoinGameResult("Error: already taken");
        }
        return new JoinGameResult(null);




    }



}
