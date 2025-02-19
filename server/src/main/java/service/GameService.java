package service;

import dataaccess.*;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.ListGamesResult;

public class GameService extends AuthenticateUser {
    public static ListGamesResult listGames(ListGamesRequest request, AuthDAO authAccessObject, GameDAO gameAccessObject) throws BadRequestException, UnauthorizedException{
        if(request.authToken() == null){
            throw new BadRequestException("Error: (authToken cannot be empty)");
        }
        try{
            authAccessObject.getAuth(request.authToken());
        } catch (DataAccessException e){
            throw new UnauthorizedException("Error: unauthorized");
        }
        return new ListGamesResult( gameAccessObject.getListGames());
    }

    public static CreateGameResult createGame(CreateGameRequest request, String authToken, AuthDAO authAccessObject, GameDAO gameAccessObject) throws BadRequestException, UnauthorizedException, AlreadyTakenException {
        if(request.gameName() == null || authToken == null){
            throw new BadRequestException("Error: (gameName and/or authToken must not be null)");
        }
        AuthenticateUser.Authenticate(authToken, authAccessObject);
        Integer gameID = gameAccessObject.createGame(request.gameName());
        return new CreateGameResult(gameID, null);
    }

    public static void joinGame(JoinGameRequest request, String authToken,  AuthDAO authAccessObject, GameDAO gameAccessObject) throws BadRequestException, UnauthorizedException, AlreadyTakenException{
        String username;
        if(request.color() == null || request.gameID() == null){
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
            gameAccessObject.updateGame(username, request.color(), request.gameID());
        } catch (DataAccessException e){
            throw new AlreadyTakenException("Error: already taken");
        }

    }





}
