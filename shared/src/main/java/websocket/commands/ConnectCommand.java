package websocket.commands;

public class ConnectCommand extends UserGameCommand{
    private final boolean isObserver;
    public ConnectCommand(CommandType commandType, String authToken, Integer gameID, Boolean isObserver) {
        super(CommandType.CONNECT, authToken, gameID);
        this.isObserver = isObserver;
    }

    public boolean isObserver(){
        return this.isObserver;
    }
}
