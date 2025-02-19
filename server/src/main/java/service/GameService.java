package service;

import dataaccess.*;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import java.util.Collection;

public class GameService {
    public static ListGamesResult listGames(ListGamesRequest request) throws OtherException, UnauthorizedException{
        AuthDAO authAccessObject = request.authAccessObject();
        GameDAO gameAccessObject = request.gameAccessObject();
        if(request.authToken() == null){
            throw new OtherException("Error: (authToken cannot be empty)");
        }
        try{
            authAccessObject.getAuth(request.authToken());
        } catch (DataAccessException e){
            throw new UnauthorizedException("Error: unauthorized");
        }
        return new ListGamesResult( gameAccessObject.getListGames());
    }

    public static CreateGameResult createGame(CreateGameRequest request) throws OtherException, UnauthorizedException, AlreadyTakenException {
        AuthDAO authAccessObject = request.authAccessObject();
        GameDAO gameAccessObject = request.gameAccessObject();
        if(request.gameName() == null || request.authToken() == null){
            throw new OtherException("Error: (gameName and/or authToken must not be null)");
        }
        try{
            authAccessObject.getAuth(request.authToken());
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        if(gameAccessObject.isNameTaken(request.gameName())){
            throw new AlreadyTakenException("Error: (gameName already taken)");
        }
        Integer gameID = gameAccessObject.createGame(request.gameName());
        return new CreateGameResult(gameID);
    }

    public static void joinGame(JoinGameRequest request) throws OtherException, UnauthorizedException, AlreadyTakenException{
        AuthDAO authAccessObject = request.authAccessObject();
        GameDAO gameAccessObject = request.gameAccessObject();
        String username;
        if(request.authToken() == null || request.color() == null || request.gameID() == null){
            throw new OtherException("Error: (authToken and/or color and/or gameID cannot be empty)");
        }
        try{
            username = authAccessObject.getUsername(request.authToken());
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        try{
            gameAccessObject.checkGameID(request.gameID());
        } catch (DataAccessException e) {
            throw new OtherException("Error: (Invalid gameID)");
        }
        try{
            gameAccessObject.updateGame(username, request.color(), request.gameID());
        } catch (DataAccessException e){
            throw new AlreadyTakenException("Error: already taken");
        }

    }





}
