package service.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import service.UnauthorizedException;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class WebSocketService {

    public static NotificationMessage connect
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
        return new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);

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

        return new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                username + "move: " + startRowNum + startColLet + " to " + endRowNum + endColLet);
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
        return new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + ", has left" +
                " the game.");
    }

    public static NotificationMessage resign(AuthDAO authAccessObject, String authToken) throws DataAccessException {
        try{
            authAccessObject.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        String username = authAccessObject.getUsername(authToken);

        return new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + ", has resigned");
    }

    private static String convertColNumToLetter(int colNum){
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        return letters[colNum - 1];
    }



}
