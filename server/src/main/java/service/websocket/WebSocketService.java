package service.websocket;

import chess.*;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import server.websocket.WrongTeamException;
import service.UnauthorizedException;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class WebSocketService {

    public static ServerMessage connect
            (AuthDAO authAccessObject, GameDAO gameAccessObject, String authToken, Integer gameID)
            throws UnauthorizedException, DataAccessException {
        authorize(authAccessObject, authToken);
        String username = authAccessObject.getUsername(authToken);
        ChessGame.TeamColor playerColor = gameAccessObject.getPlayerColor(gameID, username);
        String message;

        if (playerColor == null){
            message = username + " has joined as an observer!";
        }
        else{
            message = username + " has joined the game as: " + playerColor;
        }
        return new NotificationMessage(message);

    }

    public static ServerMessage makeMove
            (AuthDAO authAccessObject, GameDAO gameAccessObject,  String authToken, Integer gameID, ChessMove move)
            throws DataAccessException, InvalidMoveException, UnauthorizedException, WrongTeamException {

        ChessPosition startPosition = move.getStartPosition();
        int startRowNum = startPosition.getRow();
        String startColLet = convertColNumToLetter(startPosition.getColumn());
        ChessPosition endPosition = move.getEndPosition();
        int endRowNum = endPosition.getRow();
        String endColLet = convertColNumToLetter(endPosition.getColumn());

        authorize(authAccessObject, authToken);

        String username = authAccessObject.getUsername(authToken);
        ChessGame.TeamColor playerColor = gameAccessObject.getPlayerColor(gameID, username);
        GameData gameData = gameAccessObject.getGame(gameID);
        ChessGame game = gameData.getGame();
        if(game.getBoard().getPiece(new ChessPosition(
                move.getStartPosition().getRow(), move.getStartPosition().getColumn())).getTeamColor() != playerColor){
           throw new WrongTeamException("You can only move " + playerColor + " pieces.");
        }
        gameAccessObject.updateGameState(gameID, move);
        return new NotificationMessage(username + " moves, " + startColLet + startRowNum + " to " + endColLet + endRowNum);
    }

    public static ServerMessage leaveGame
            (AuthDAO authAccessObject, GameDAO gameAccessObject, String authToken, int gameID)
            throws DataAccessException, UnauthorizedException {
        authorize(authAccessObject, authToken);
        String username = authAccessObject.getUsername(authToken);
            gameAccessObject.resetPlayer(gameID, username);
        return new NotificationMessage(username + ", has left" + " the game.");
    }

    public static ServerMessage resign(AuthDAO authAccessObject, String authToken)
            throws DataAccessException, UnauthorizedException {
        authorize(authAccessObject, authToken);
        String username = authAccessObject.getUsername(authToken);

        return new NotificationMessage(username + ", has resigned");
    }

    public static ChessGame loadGame(int gameID, AuthDAO authAccessObject, GameDAO gameAccessObject, String authToken)
            throws DataAccessException, UnauthorizedException{
        authorize(authAccessObject, authToken);
        GameData gameData = gameAccessObject.getGame(gameID);
        return gameData.getGame();
    }

    public static void changeStatus(AuthDAO authAccessObject, GameDAO gameAccessObject,
                                    int gameID, GameStatus status, String authToken)
            throws UnauthorizedException, DataAccessException {
        authorize(authAccessObject, authToken);
        gameAccessObject.updateStatus(gameID, status);
    }

    public static GameStatus getStatus(AuthDAO authAccessObject , GameDAO gameAccessObject, int gameID, String authToken) throws DataAccessException {
        authorize(authAccessObject, authToken);
        return gameAccessObject.getGameStatus(gameID);
    }

    public static ChessGame.TeamColor getTeamColor(AuthDAO authAccessObject, String authToken, GameDAO gameAccessObject, int gameID) throws DataAccessException{
        authorize(authAccessObject, authToken);
        String username = authAccessObject.getUsername(authToken);
        return gameAccessObject.getPlayerColor(gameID, username);
    }

    private static String convertColNumToLetter(int colNum){
        String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H"};
        return letters[colNum - 1];
    }

    private static void authorize(AuthDAO authAccessObject, String authToken) throws UnauthorizedException{
        try{
            authAccessObject.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Error: unauthorized");
        }
    }



}
