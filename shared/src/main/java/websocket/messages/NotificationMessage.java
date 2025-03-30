package websocket.messages;

public class NotificationMessage extends ServerMessage{
    private final String notification;
    public NotificationMessage(ServerMessageType type, String notification) {
        super(ServerMessageType.NOTIFICATION);
        this.notification = notification;
    }

    public String getNotification(){
        return this.notification;
    }
}
