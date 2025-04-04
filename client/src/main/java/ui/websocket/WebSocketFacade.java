package ui.websocket;

import chess.ChessMove;
import client.ResponseException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import websocket.commands.*;
import websocket.deserializer.Deserializer;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    private final Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler){
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ServerMessage.class, new Deserializer())
                    .create();
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message){
                    ServerMessage notification = gson.fromJson(message, ServerMessage.class);
                    notificationHandler.notify(notification);



                }
            });
        } catch (DeploymentException | IOException | URISyntaxException e){
            throw new ResponseException(500, e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

    public void connect(String authToken, int gameID) throws ResponseException{
        try{
            UserGameCommand command = new ConnectCommand(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e){
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws ResponseException{
        try{
            UserGameCommand command = new MakeMoveCommand(authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e){
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void leave(String authToken, int gameID) throws ResponseException{
        try {
            UserGameCommand command = new LeaveCommand(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e){
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void resign(String authToken, int gameID) throws ResponseException{
        try {
            UserGameCommand command = new ResignCommand(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e){
            throw new ResponseException(500, e.getMessage());
        }
    }


}
