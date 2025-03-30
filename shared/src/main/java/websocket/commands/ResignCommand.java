package websocket.commands;

public class ResignCommand extends UserGameCommand{
    public ResignCommand(CommandType commandType, String authToken, Integer gameID) {
        super(CommandType.RESIGN, authToken, gameID);
    }
}
