package websocket.messages;

public class ErrorMessage extends ServerMessage{
    private final String errorMessage;
    public ErrorMessage( String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }
    public String toString(){
        return "Error " + this.errorMessage;
    }
}
