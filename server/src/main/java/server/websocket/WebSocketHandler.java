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
import service.UnauthorizedException;
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
            case CONNECT -> connect(new ConnectCommand(command.getAuthToken(), command.getGameID()), session);
            case MAKE_MOVE -> {
                MakeMoveCommand makeMoveCommand = new Gson().fromJson(str, MakeMoveCommand.class);
                makeMove(makeMoveCommand, session);
            }
            case RESIGN -> resignGame(new ResignCommand(command.getAuthToken(), command.getGameID()), session);
            case LEAVE -> leaveGame(new LeaveCommand(command.getAuthToken(), command.getGameID()), session);
            default -> sendMessage(new ErrorMessage("Error: Unknown command"), session);
            }
        }

    private void connect(ConnectCommand command, Session session) {
        try{
            this.connectionManager.addSessionToGame(command.getGameID(), session);
            ServerMessage message = WebSocketService.connect(authDAO, gameDAO, command.getAuthToken(), command.getGameID());
            ChessGame game = WebSocketService.loadGame(command.getGameID(), authDAO, gameDAO, command.getAuthToken());
            LoadGameMessage gameMessage = new LoadGameMessage(game);
            sendMessage(gameMessage, session);
            broadcastMessage(command.getGameID(), message, session);

        } catch (DataAccessException e){
            ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
            sendMessage(errorMessage, session);
        }catch (UnauthorizedException e){
            ErrorMessage errorMessage = new ErrorMessage("Error: Unauthorized");
            sendMessage(errorMessage, session);
        }
    }
    private void makeMove(MakeMoveCommand command, Session session){
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();
        try{
            GameStatus status = WebSocketService.getStatus(authDAO, gameDAO, gameID, authToken);
            if(status != GameStatus.PLAYABLE){
                ErrorMessage errorMessage = getErrorMessage(status);
                sendMessage(errorMessage, session);
                return;
            }
            ServerMessage message = WebSocketService.makeMove(authDAO, gameDAO, authToken, gameID, command.getMove());
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
        }catch (UnauthorizedException e){
            ErrorMessage errorMessage = new ErrorMessage("Error: Unauthorized");
            sendMessage(errorMessage, session);
        } catch (WrongTeamException e){
            ErrorMessage errorMessage = new ErrorMessage("Error: you can't move pieces from the other team");
            sendMessage(errorMessage, session);
        }
    }

    private void leaveGame(LeaveCommand command, Session session){
        try {
            ServerMessage message =  WebSocketService.leaveGame(authDAO, gameDAO, command.getAuthToken(), command.getGameID());
            broadcastMessage(command.getGameID(), message, session);
            this.connectionManager.removeSessionFromGame(command.getGameID(), session);
        } catch (DataAccessException e){
            ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
            sendMessage(errorMessage, session);
        }catch (UnauthorizedException e){
            ErrorMessage errorMessage = new ErrorMessage("Error: Unauthorized");
            sendMessage(errorMessage, session);

        }
    }
    private void resignGame(ResignCommand command, Session session){
        try {
            GameStatus status = WebSocketService.getStatus(authDAO, gameDAO, command.getGameID(), command.getAuthToken());
            ChessGame.TeamColor playerColor = null;
            try {
                playerColor = WebSocketService.getTeamColor(authDAO, command.getAuthToken(), gameDAO, command.getGameID());
            }catch (DataAccessException e){
                sendMessage(new ErrorMessage("Unable to get player color"), session);
            }
            if(status == GameStatus.RESIGNED){
                ErrorMessage errorMessage = new ErrorMessage("Someone already resigned!");
                sendMessage(errorMessage, session);
            } else if (status != GameStatus.PLAYABLE){
                ErrorMessage errorMessage = new ErrorMessage("This game is over.");
                sendMessage(errorMessage, session);
            }
            else if(playerColor == null){ //observer
                ErrorMessage errorMessage = new ErrorMessage("Observers can't resign");
                sendMessage(errorMessage, session);
            } else {
                ServerMessage message = WebSocketService.resign(authDAO, command.getAuthToken());
                WebSocketService.changeStatus(authDAO, gameDAO, command.getGameID(), GameStatus.RESIGNED, command.getAuthToken());
                broadcastMessageToAll(command.getGameID(), message);
            }

        } catch (DataAccessException e){
            ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
            sendMessage(errorMessage, session);
        } catch (UnauthorizedException e){
            ErrorMessage errorMessage = new ErrorMessage("Error: Unauthorized");
            sendMessage(errorMessage, session);
        }

    }

    private void sendMessage(ServerMessage message, Session session) {
        try {
            session.getRemote().sendString(message.toString());
        } catch (IOException e){
            throw new WebSocketException("Unable to send message.");
        }
    }
    private void broadcastMessage(int gameID, ServerMessage message, Session excludedSession){
        Set<Session> sessions = Set.copyOf(this.connectionManager.getSessionsFromGame(gameID));
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
        Set<Session> sessions = Set.copyOf(this.connectionManager.getSessionsFromGame(gameID));
        for(Session cSession : sessions){
            try {
                cSession.getRemote().sendString(message.toString());
            } catch (IOException e){
                throw  new WebSocketException("Unable to send message");
            }
        }
    }

    private static ErrorMessage getErrorMessage(GameStatus status) {
        ErrorMessage errorMessage;
        if(status == GameStatus.CHECKMATE){
            errorMessage = new ErrorMessage("This game is over, there was a " + status.name().toLowerCase());
        } else if (status == GameStatus.STALEMATE) {
            errorMessage = new ErrorMessage("This game is in a " + status.name().toLowerCase());
        } else {
            errorMessage = new ErrorMessage("This game is over, someone " + status.name().toLowerCase());
        }
        return errorMessage;
    }







}
