package websocket.messages;

import com.google.gson.Gson;

public class ErrorMessage extends ServerMessage{
    private String errorMessage;

    private ErrorMessage(){
        super(ServerMessageType.ERROR);

    }
    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }

    public String getMessage() {
        return errorMessage;
    }

    public String toString(){
        return new Gson().toJson(this);    }
}
