package service.websocket;

import chess.*;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import service.UnauthorizedException;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class WebSocketService {

    public static ServerMessage connect
            (AuthDAO authAccessObject, GameDAO gameAccessObject, String authToken, Integer gameID, Boolean isObserver)
            throws UnauthorizedException, DataAccessException {
        try{
            authAccessObject.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        String username = authAccessObject.getUsername(authToken);
        ChessGame.TeamColor playerColor = gameAccessObject.getPlayerColor(gameID, username);
        String message;
        if (isObserver){
            message = username + " has joined as an observer!";
        }
        else{
            message = username + " has joined the game as: " + playerColor;
        }
        return new NotificationMessage(message);

    }

    public static NotificationMessage makeMove
            (AuthDAO authAccessObject, GameDAO gameAccessObject,  String authToken, Integer gameID, ChessMove move)
            throws DataAccessException, InvalidMoveException, UnauthorizedException {

        ChessPosition startPosition = move.getStartPosition();
        int startRowNum = startPosition.getRow();
        String startColLet = convertColNumToLetter(startPosition.getColumn());
        ChessPosition endPosition = move.getEndPosition();
        int endRowNum = endPosition.getRow();
        String endColLet = convertColNumToLetter(endPosition.getColumn());

        try{
            authAccessObject.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        String username = authAccessObject.getUsername(authToken);
        gameAccessObject.updateGameState(gameID, move);

        return new NotificationMessage(username + "move: " + startRowNum + startColLet + " to " + endRowNum + endColLet);
    }

    public static NotificationMessage leaveGame
            (AuthDAO authAccessObject, GameDAO gameAccessObject, String authToken, int gameID)
            throws DataAccessException {
        try{
            authAccessObject.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        String username = authAccessObject.getUsername(authToken);
        gameAccessObject.resetPlayer(gameID, username);
        return new NotificationMessage(username + ", has left" + " the game.");
    }

    public static NotificationMessage resign(AuthDAO authAccessObject, String authToken) throws DataAccessException {
        try{
            authAccessObject.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        String username = authAccessObject.getUsername(authToken);

        return new NotificationMessage(username + ", has resigned");
    }

    private static String convertColNumToLetter(int colNum){
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        return letters[colNum - 1];
    }

    public static ChessGame loadGame(int gameID, AuthDAO authAccessObject, GameDAO gameAccessObject, String authToken) throws DataAccessException{
        try{
            authAccessObject.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        GameData gameData = gameAccessObject.getGame(gameID);
        return gameData.getGame();
    }

    public static void changeStatus(AuthDAO authAccessObject, GameDAO gameAccessObject, int gameID, GameStatus status, String authToken) throws DataAccessException {
        try{
            authAccessObject.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        gameAccessObject.updateStatus(gameID, status);
    }



}
