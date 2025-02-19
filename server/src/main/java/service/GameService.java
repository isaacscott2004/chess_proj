package service;

import dataaccess.*;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.ListGamesResult;

public class GameService {
    public static ListGamesResult listGames(ListGamesRequest request, String authToken, AuthDAO authAccessObject, GameDAO gameAccessObject) throws UnauthorizedException{
        try{
            authAccessObject.getAuth(authToken);
        } catch (DataAccessException e){
            throw new UnauthorizedException("Error: unauthorized");
        }
        return new ListGamesResult(gameAccessObject.getListGames(), null);
    }

    public static CreateGameResult createGame(CreateGameRequest request, String authToken, AuthDAO authAccessObject, GameDAO gameAccessObject) throws BadRequestException, UnauthorizedException, AlreadyTakenException {
        if(request.gameName() == null){
            throw new BadRequestException("Error: (gameName and/or authToken must not be null)");
        }
        try{
            authAccessObject.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        if(gameAccessObject.isNameTaken(request.gameName())){
            throw new AlreadyTakenException("Error: (gameName already taken)");
        }
        Integer gameID = gameAccessObject.createGame(request.gameName());
        return new CreateGameResult(gameID, null);
    }

    public static void joinGame(JoinGameRequest request, String authToken,  AuthDAO authAccessObject, GameDAO gameAccessObject) throws BadRequestException, UnauthorizedException, AlreadyTakenException{
        String username;
        if(request.playerColor() == null || request.gameID() == null){
            throw new BadRequestException("Error: (authToken and/or color and/or gameID cannot be empty)");
        }
        try{
            username = authAccessObject.getUsername(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        try{
            gameAccessObject.checkGameID(request.gameID());
        } catch (DataAccessException e) {
            throw new BadRequestException("Error: (Invalid gameID)");
        }
        try{
            gameAccessObject.updateGame(username, request.playerColor(), request.gameID());
        } catch (DataAccessException e){
            throw new AlreadyTakenException("Error: already taken");
        }

    }





}
