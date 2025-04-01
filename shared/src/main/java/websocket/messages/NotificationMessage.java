package websocket.messages;

import com.google.gson.Gson;

public class NotificationMessage extends ServerMessage {
    private String message;

    private NotificationMessage(){
        super(ServerMessageType.NOTIFICATION);
    }

    public NotificationMessage(String message){
        super(ServerMessageType.NOTIFICATION);
        this.message = message;

    }

    public String getMessage() {
        return message;
    }

    public String toString(){
        return new Gson().toJson(this);
    }
}
