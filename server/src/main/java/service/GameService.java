package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
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
        MemoryAuthDAO authAccessObject = new MemoryAuthDAO();
        MemoryGameDAO gameAccessObject = new MemoryGameDAO();
        Collection<GameData> games;
        if(request.authToken() == null){
            return new ListGamesResult(null, "Error: (authToken cannot be empty)");
        }
        try{
            authAccessObject.getAuth(request.authToken());
        } catch (DataAccessException e){
            return new ListGamesResult(null, "Error: unauthorized");
        }
        try{
            games = gameAccessObject.getListGames();
        } catch (DataAccessException e){
            return new ListGamesResult(null, "Error: (There are no games available)");
        }
        return new ListGamesResult(games, null);
    }

    public static CreateGameResult createGame(CreateGameRequest request){
        MemoryAuthDAO authAccessObject = new MemoryAuthDAO();
        MemoryGameDAO gameAccessObject = new MemoryGameDAO();
        if(request.gameName() == null || request.authToken() == null){
            return new CreateGameResult(null, "Error: (gameName and/or authToken must not be null)");
        }
        try{
            authAccessObject.getAuth(request.authToken());
        } catch (DataAccessException e) {
           return new CreateGameResult(null, "Error: unauthorized");
        }
        Integer gameID = gameAccessObject.createGame(request.gameName());
        return new CreateGameResult(gameID, null);
    }

//    public static JoinGameResult joinGame(JoinGameRequest request){
//        MemoryAuthDAO authAccessObject = new MemoryAuthDAO();
//        MemoryGameDAO gameAccessObject = new MemoryGameDAO();
//        if(request.authToken() == null || request.color() == null || request.gameID() == null){
//            return new JoinGameResult("Error: (authToken and/or color and/or gameID cannot be empty)");
//        }
//        try{
//            authAccessObject.getAuth(request.authToken());
//        } catch (DataAccessException e) {
//            return new JoinGameResult("Error: unauthorized");
//        }
//        try{
//            gameAccessObject.getGame(request.gameID());
//        } catch (DataAccessException e) {
//            return new JoinGameResult("Error: (Invalid gameID)");
//        }
//
//
//    }



}
