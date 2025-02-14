package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.GameData;
import request.ListGamesRequest;
import result.ListGamesResult;

import java.util.ArrayList;
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
}
