package server.websocket;


import chess.ChessGame;
import chess.GameStatus;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.websocket.WebSocketService;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Set;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connectionManager;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO){
        connectionManager = new ConnectionManager();
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @OnWebSocketConnect
    public void onConnect(Session session){
//        String gameID = session.
//        if(gameID == null){
//            throw new WebSocketException("Error");
//        }
//        int numGameID;
//        try{
//            numGameID = Integer.parseInt(gameID);
//        } catch (NumberFormatException e){
//            throw new WebSocketException("Error: gameID must be a number");
//        }
//        this.connectionManager.addSessionToGame(numGameID, session);

    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason){
        this.connectionManager.removeSession(session);

    }

    @OnWebSocketError
    public void onError(Throwable throwable){
        System.out.println("WebSocket Error: " + throwable.getMessage());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String str){
            UserGameCommand command = new Gson().fromJson(str, UserGameCommand.class);
            switch (command.getCommandType()){
            case CONNECT -> connect(new ConnectCommand(command.getAuthToken(), command.getGameID(), false), session);
            case MAKE_MOVE -> {
                if(command instanceof MakeMoveCommand makeMoveCommand){
                        makeMove(makeMoveCommand, session);
                } else {
                    throw new WebSocketException("Invalid object type for MAKE_MOVE command");
                }
            }
            case RESIGN -> {
                if(command instanceof ResignCommand resignCommand){
                       resignGame(resignCommand, session);
                } else {
                    throw new WebSocketException("Invalid object type for RESIGN command");
                }
            }
            case LEAVE -> {
                if(command instanceof LeaveCommand leaveCommand){
                        leaveGame(leaveCommand, session);
                } else {
                    throw new WebSocketException("Invalid object type for LEAVE command");
                }
            }

        }
    }

    private void connect(ConnectCommand command, Session session) {
        try{
            this.connectionManager.addSessionToGame(command.getGameID(), session);
            ServerMessage message = WebSocketService.connect(authDAO, gameDAO, command.getAuthToken(), command.getGameID(), command.isObserver());
            ChessGame game = WebSocketService.loadGame(command.getGameID(), authDAO, gameDAO, command.getAuthToken());
            LoadGameMessage gameMessage = new LoadGameMessage(game);
            sendMessage(gameMessage, session);
            broadcastMessage(command.getGameID(), message, session);

        } catch (DataAccessException e){
            ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
            sendMessage(errorMessage, session);

        }
    }
    private void makeMove(MakeMoveCommand command, Session session){
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();
        try{
            NotificationMessage message = WebSocketService.makeMove(authDAO, gameDAO, authToken, gameID, command.getMove());
            ChessGame game = WebSocketService.loadGame(gameID, authDAO, gameDAO, authToken);

            LoadGameMessage gameMessage = new LoadGameMessage(game);
            broadcastMessageToAll(gameID, gameMessage);
            broadcastMessage(gameID, message, session);

            if(game.isInCheck(game.getTeamTurn())){
                NotificationMessage inCheckMessage = new NotificationMessage(game.getTeamTurn() + " is in check!");
                broadcastMessageToAll(gameID, inCheckMessage);
            } else if(game.isInCheckmate(game.getTeamTurn())){
                WebSocketService.changeStatus(authDAO, gameDAO, gameID, GameStatus.CHECKMATE, authToken);
                NotificationMessage inCheckMateMessage = new NotificationMessage(game.getTeamTurn() + " in in checkmate!");
                broadcastMessageToAll(gameID, inCheckMateMessage);
            } else if(game.isInStalemate(game.getTeamTurn())){
                WebSocketService.changeStatus(authDAO, gameDAO, gameID, GameStatus.STALEMATE, authToken);
                NotificationMessage inStalemateMessage = new NotificationMessage("Game is in a stalemate");
                broadcastMessageToAll(gameID, inStalemateMessage);
            }

        } catch (DataAccessException e) {
            ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
            sendMessage(errorMessage, session);
        } catch (InvalidMoveException e){
            ErrorMessage errorMessage = new ErrorMessage("Invalid move");
            sendMessage(errorMessage, session);
        }
    }

    private void leaveGame(LeaveCommand command, Session session){
        try {
            NotificationMessage message =  WebSocketService.leaveGame(authDAO, gameDAO, command.getAuthToken(), command.getGameID());
            broadcastMessage(command.getGameID(), message, session);
        } catch (DataAccessException e){
            ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
            sendMessage(errorMessage, session);
        }
    }
    private void resignGame(ResignCommand command, Session session){
        try {
            NotificationMessage message = WebSocketService.resign(authDAO, command.getAuthToken());
            WebSocketService.changeStatus(authDAO, gameDAO, command.getGameID(), GameStatus.RESIGNED, command.getAuthToken());
            broadcastMessageToAll(command.getGameID(), message);
        } catch (DataAccessException e){
            ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
            sendMessage(errorMessage, session);
        }

    }

    private void sendMessage(ServerMessage message, Session session) {
        try {
            Gson gson = new Gson();
            String jsonMessage = gson.toJson(message);
            session.getRemote().sendString(jsonMessage);

        } catch (IOException e){
            throw new WebSocketException("Unable to send message.");
        }
    }
    private void broadcastMessage(int gameID, ServerMessage message, Session excludedSession){
        Set<Session> sessions = this.connectionManager.getSessionsFromGame(gameID);
        for(Session cSession : sessions){
            if(!(cSession.equals(excludedSession))){
                try{
                    cSession.getRemote().sendString(message.toString());
                } catch (IOException e){
                    throw new WebSocketException("Unable to send message.");
                }
            }
        }
    }

    private void broadcastMessageToAll(int gameID, ServerMessage message){
        Set<Session> sessions = this.connectionManager.getSessionsFromGame(gameID);
        for(Session cSession : sessions){
            try {
                cSession.getRemote().sendString(message.toString());
            } catch (IOException e){
                throw  new WebSocketException("Unable to send message");
            }
        }
    }







}
