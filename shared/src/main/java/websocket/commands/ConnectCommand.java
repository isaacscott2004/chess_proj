package websocket.commands;

public class ConnectCommand extends UserGameCommand{

    public ConnectCommand(CommandType commandType, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
    }
}
