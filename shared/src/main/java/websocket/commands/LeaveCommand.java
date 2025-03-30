package websocket.commands;

public class LeaveCommand extends UserGameCommand{
    public LeaveCommand(CommandType commandType, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
    }
}
