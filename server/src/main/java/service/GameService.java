package service;

import dataaccess.*;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGamesResult;

public class GameService {
    /**
     * gets the list of current active games
     * @param authToken the token to be authorized
     * @param authAccessObject access to AuthData
     * @param gameAccessObject access to GameData
     * @return a ListGamesResult object with a message
     * @throws UnauthorizedException if the authToken is not authorized
     */
    public static ListGamesResult listGames(String authToken, AuthDAO authAccessObject, GameDAO gameAccessObject)
            throws UnauthorizedException, DataAccessException {
        try{
            authAccessObject.getAuth(authToken);
        } catch (DataAccessException e){
            throw new UnauthorizedException("Error: unauthorized");
        }
        return new ListGamesResult(gameAccessObject.getListGames(), null);
    }

    /**
     * creates a new Chess game
     * @param request a request that contains the name of the game
     * @param authToken token to be authorized
     * @param authAccessObject access to AuthData
     * @param gameAccessObject access to GameData
     * @return a CreateGameResult with a null message and the gameID
     * @throws BadRequestException if the gameName is null
     * @throws UnauthorizedException if the authToken is not authorized
     */
    public static CreateGameResult createGame(CreateGameRequest request, String authToken,
                                              AuthDAO authAccessObject, GameDAO gameAccessObject)
            throws BadRequestException, UnauthorizedException, DataAccessException {
        if(request.gameName() == null){
            throw new BadRequestException("Error: (gameName must not be null)");
        }
        try{
            authAccessObject.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        Integer gameID = gameAccessObject.createGame(request.gameName());
        return new CreateGameResult(gameID, null);
    }

    /**
     * Joins a game from the available list of current games
     * @param request a JoinGameRequest with the gameID and the color(only BLACK or WHITE)
     * @param authToken the token to be authorized
     * @param authAccessObject access to AuthData
     * @param gameAccessObject access to GameData
     * @return JoinGameResult with a message
     * @throws BadRequestException if color and/or gameID is null
     * @throws UnauthorizedException if the authToken is not authorized
     * @throws AlreadyTakenException if the specified color is already taken
     */
    public static JoinGameResult joinGame(JoinGameRequest request, String authToken,
                                          AuthDAO authAccessObject, GameDAO gameAccessObject)
            throws BadRequestException, UnauthorizedException, AlreadyTakenException{
        String username;
        if(request.playerColor() == null || request.gameID() == null){
            throw new BadRequestException("Error: (color and/or gameID cannot be empty)");
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
        return new JoinGameResult("Game successfully joined!");

    }





}
